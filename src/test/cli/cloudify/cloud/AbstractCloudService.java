package test.cli.cloudify.cloud;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import framework.utils.AssertUtils;
import framework.utils.LogUtils;
import framework.utils.ScriptUtils;
import framework.utils.WebUtils;
import framework.utils.AssertUtils.RepetitiveConditionProvider;

import test.cli.cloudify.CloudTestUtils;
import test.cli.cloudify.CommandTestUtils;

public abstract class AbstractCloudService implements CloudService {
	
	public static final int NUM_OF_MANAGEMENT_MACHINES = 1;
	
    protected URL[] restAdminUrl = new URL[NUM_OF_MANAGEMENT_MACHINES];
    protected URL[] webUIUrl = new URL[NUM_OF_MANAGEMENT_MACHINES];
	
	public abstract String getCloudName();
	
	public abstract String getUser();
	
	public abstract String getApiKey();
	
	public abstract void injectAuthenticationDetails() throws IOException;
	
	
	public URL getWebuiURL() {
		return webUIUrl[0];
	}
	
    public URL getMachinesUrl(String url) throws Exception {
        return new URL(stripSlash(url) + "/admin/machines");
    }
	
    private static String stripSlash(String str) {
        if (str == null || !str.endsWith("/")) {
            return str;
        }
        return str.substring(0, str.length()-1);
    }
 
    
	@Override
	public void bootstrapCloud() throws IOException, InterruptedException {
		
		injectAuthenticationDetails();
		String output = CommandTestUtils.runCommandAndWait("bootstrap-cloud --verbose " + getCloudName());
		restAdminUrl = extractRestAdminUrls(output, NUM_OF_MANAGEMENT_MACHINES);
		webUIUrl = extractWebuiUrls(output, NUM_OF_MANAGEMENT_MACHINES);
		assertBootstrapServicesAreAvailable();
	    
	    URL machinesURL;
		try {
			machinesURL = getMachinesUrl(restAdminUrl[0].toString());
		    AssertUtils.assertEquals("Expecting " + NUM_OF_MANAGEMENT_MACHINES + " machines", 
		    		NUM_OF_MANAGEMENT_MACHINES, CloudTestUtils.getNumberOfMachines(machinesURL));
		} catch (Exception e) {
			LogUtils.log("caught exception while geting number of management machines", e);
		}
	}

	@Override
	public void teardownCloud() {
		
		try {
			CommandTestUtils.runCommandAndWait("teardown-cloud --verbose -force " + getCloudName());
		} catch (IOException e) {
			Assert.fail("teardown-cloud failed. SHUTDOWN VIRTUAL MACHINES MANUALLY !!!",e);
		} catch (InterruptedException e) {
			Assert.fail("teardown-cloud failed. SHUTDOWN VIRTUAL MACHINES MANUALLY !!!",e);
		}
		finally {
			try {
				deleteCloudFiles(getCloudName());
			} catch (IOException e) {
				Assert.fail("Failed to clean up after test finished: " + e.getMessage(), e);
			}
		}	
	}

	@Override
	public String getRestUrl() {
		return restAdminUrl[0].toString();
	}
	
	@Override 
	public String getWebuiUrl() {
		return webUIUrl[0].toString();
	}
	
	
	private URL[] extractRestAdminUrls(String output, int numOfManagementMachines) throws MalformedURLException {
		
		URL[] restAdminUrls = new URL[numOfManagementMachines];
		
		Pattern restPattern = Pattern.compile(CloudTestUtils.REST_URL_REGEX);
		
		Matcher restMatcher = restPattern.matcher(output);
		
		// This is sort of hack.. currently we are outputing this over ssh and locally with different results
		
		AssertUtils.assertTrue("Could not find remote (internal) rest url", restMatcher.find());
		
		for (int i = 0; i < numOfManagementMachines ; i++) {
			AssertUtils.assertTrue("Could not find actual rest url", restMatcher.find());

			String rawRestAdminUrl = restMatcher.group(1);
		
			restAdminUrls[i] = new URL(rawRestAdminUrl);
		}

		return restAdminUrls;

	}

	private URL[] extractWebuiUrls(String cliOutput, int numberOfManagementMachines) throws MalformedURLException {
		
		URL[] webuiUrls = new URL[numberOfManagementMachines];
		
		Pattern webUIPattern = Pattern.compile(CloudTestUtils.WEBUI_URL_REGEX);
		
		Matcher webUIMatcher = webUIPattern.matcher(cliOutput);
		
		// This is sort of hack.. currently we are outputing this over ssh and locally with different results
		
		AssertUtils.assertTrue("Could not find remote (internal) webui url", webUIMatcher.find()); 
		
		for (int i = 0; i < numberOfManagementMachines ; i++) {
			AssertUtils.assertTrue("Could not find actual webui url", webUIMatcher.find());

			String rawWebUIUrl = webUIMatcher.group(1);
			
			webuiUrls[i] = new URL(rawWebUIUrl);
		}
		
		return webuiUrls;
	}
	
	private void deleteCloudFiles(String cloudName) throws IOException {
		
		File cloudPluginDir = new File(ScriptUtils.getBuildPath() , "tools/cli/plugins/esc/" + cloudName + "/");
		File originalCloudDslFile = new File(cloudPluginDir, cloudName + "-cloud.groovy");
		File backupCloudDslFile = new File(cloudPluginDir, cloudName + "-cloud.backup");
		File targetPemFolder = new File(ScriptUtils.getBuildPath(), "tools/cli/plugins/esc/" + cloudName + "/upload/");
		
		for (File file : targetPemFolder.listFiles()) {
			if (file.getName().contains(".pem")) {
				FileUtils.deleteQuietly(file);
				break;
			}
		}
		
		FileUtils.copyFile(backupCloudDslFile, originalCloudDslFile);
		FileUtils.deleteQuietly(backupCloudDslFile);
		

	}

	
	private void assertBootstrapServicesAreAvailable() throws MalformedURLException {
		
		for (int i = 0; i < restAdminUrl.length; i++) {
			// The rest home page is a JSP page, which will fail to compile if there is no JDK installed. So use testrest instead
			assertWebServiceAvailable(new URL( restAdminUrl[i].toString() + "/service/testrest"));
			assertWebServiceAvailable(webUIUrl[i]);
		}

		
	}
	
	private static void assertWebServiceAvailable(final URL url) {
        AssertUtils.repetitiveAssertTrue(url + " is not up", new RepetitiveConditionProvider() {
            public boolean getCondition() {
                try {
                    return WebUtils.isURLAvailable(url);
                } catch (Exception e) {
                    return false;
                }
            }
        }, CloudTestUtils.OPERATION_TIMEOUT);	    
	}


}
