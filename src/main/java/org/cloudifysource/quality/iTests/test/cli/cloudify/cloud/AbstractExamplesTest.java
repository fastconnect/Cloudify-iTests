package org.cloudifysource.quality.iTests.test.cli.cloudify.cloud;

import com.j_spaces.kernel.PlatformVersion;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import iTests.framework.utils.AssertUtils;
import iTests.framework.utils.LogUtils;
import iTests.framework.utils.ScriptUtils;
import iTests.framework.utils.WebUtils;
import org.cloudifysource.domain.Application;
import org.cloudifysource.domain.Service;
import org.cloudifysource.dsl.internal.ServiceReader;
import org.cloudifysource.dsl.utils.IPUtils;
import org.cloudifysource.dsl.utils.ServiceUtils;
import org.cloudifysource.quality.iTests.test.cli.cloudify.CommandTestUtils;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.services.ec2.Ec2WinCloudService;
import org.cloudifysource.restclient.GSRestClient;
import org.cloudifysource.restclient.RestException;
import org.testng.annotations.AfterMethod;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public abstract class AbstractExamplesTest extends NewAbstractCloudTest {

	private static final int WINDOWS_INSTALLATION_TIMEOUT = 50;
    private static final int DEFAULT_INSTALLATION_TIMEOUT = 50;
	private static String applicationName;

	@AfterMethod
	public void cleanup() throws IOException, InterruptedException {
		super.uninstallApplicationIfFound(applicationName);
		super.scanForLeakedAgentNodes();
	}


	protected void testTravel() throws Exception {
		doTest(ScriptUtils.getBuildRecipesApplicationsPath() + "/travel", null);
	}

	protected void testPetclinic() throws Exception {
		doTest(ScriptUtils.getBuildRecipesApplicationsPath() + "/petclinic", null);
	}

	protected void testPetclinicSimple() throws Exception {
		doTest(ScriptUtils.getBuildRecipesApplicationsPath() + "/petclinic-simple", null);
	}

	protected void testHelloWorld() throws Exception {
		doTest(ScriptUtils.getBuildRecipesApplicationsPath() + "/helloworld", null);
	}

	protected void testTravelChef() throws Exception {
		doTest(ScriptUtils.getBuildRecipesApplicationsPath() + "/travel-chef", null);
	}

	protected void testComputers(String localGitAppsPath) throws Exception {
		doTest(localGitAppsPath + "/computers", null);
	}

	protected void testDrupalBabies(String localGitAppsPath) throws Exception {
		doTest(localGitAppsPath + "/drupal-babies", null);
	}

    protected void testDrupalChef(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/drupal-chef", null);
    }

	protected void testBiginsights(String localGitAppsPath) throws Exception {
		doTest(localGitAppsPath + "/hadoop-biginsights", null);
	}

    protected void testMasterSlave(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/masterslave", null);
    }

    protected void testLamp(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/lamp", null);
    }
    protected void testMySQLAdmin(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/mysql-admin", null);
    }

    protected void testRedmine(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/redmine", null);
    }

    protected void testRedminePuppet(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/redmine-puppet", null);
    }

    protected void testHelloPuppet(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/hello-puppet", null);
    }

    protected void testStorm(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/storm", null);
    }

    protected void testXAPStream(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/xapstream", null);
    }

	protected void testPetclinicJboss(String localGitAppsPath) throws Exception {
		doTest(localGitAppsPath + "/jboss-petclinic", null);
	}

	protected void testPetclinicWas(String localGitAppsPath) throws Exception {
		doTest(localGitAppsPath + "/petclinic-was", null);
	}

	protected void testTravelLb(String localGitAppsPath) throws Exception {
		doTest(localGitAppsPath + "/travel-lb", null);
	}

	protected void testStatelessAndStateful() throws Exception {
		String path = CommandTestUtils.getPath("src/main/resources/apps/USM/usm/applications");
		doTest(path + "/StatefulAndStatelessApp", null);

	}

	protected void testMysqlJboss(String localGitAppsPath) throws Exception {
		doTest(localGitAppsPath + "/jboss-mysql", null);
	}

    protected void testHttpSession(String localGitAppsPath) throws Exception {
        doTest(localGitAppsPath + "/HttpSession", null);
    }

	protected void doTest(String applicationPath, String overrideApplicationName) throws Exception {
		LogUtils.log("Reading Application from file : " + applicationPath);
		Application application = ServiceReader.getApplicationFromFile(new File(applicationPath)).getApplication();
		LogUtils.log("Succesfully read Application : " + application);
		applicationName = application.getName();
		LogUtils.log("Application name is " + applicationName);

		if (overrideApplicationName != null) {
			LogUtils.log("Overriding application name with : " + overrideApplicationName);
			applicationName = overrideApplicationName;
		}

		boolean hasApacheLB = false;
		int apachePort = 0;
		for (Service service : application.getServices()) {
			if (service.getName().equals("apacheLB")) {
				hasApacheLB = true;
				File propsFile = new File(applicationPath + "/apacheLB", "apacheLB-service.properties");
				LogUtils.log("Application " + applicationName + " has an apacheLB service. reading properties file from " + propsFile.getAbsolutePath());
				ConfigObject config = new ConfigSlurper().parse(propsFile.toURI().toURL());
				apachePort = (Integer) config.get("currentPort");
				LogUtils.log("apacheLB port is " + apachePort);
				break;
			}
		}

		if (getService() instanceof Ec2WinCloudService) {
			applicationPath = applicationPath + "-win";
			applicationName = applicationName + "-win";
			installApplicationAndWait(applicationPath, applicationName, WINDOWS_INSTALLATION_TIMEOUT);
		} else {
			installApplicationAndWait(applicationPath, applicationName, DEFAULT_INSTALLATION_TIMEOUT);
		}

		verifyServices(applicationName, application.getServices());
		verifyApplicationUrls(applicationName, hasApacheLB, apachePort);
		verifyApplicationDependencies(applicationName, application);
		uninstallApplicationAndWait(applicationName, false, 20);
		super.scanForLeakedAgentNodes();
	}
	
	private void verifyApplicationDependencies(final String applicationName, final Application application) throws
            MalformedURLException, RestException {
		final GSRestClient client = new GSRestClient("", "", new URL(getRestUrl()), PlatformVersion.getVersionNumber());
		
		for (Service service : application.getServices()) {
			validateServiceDependency(client, service, applicationName);
		}
	}
	
	private void validateServiceDependency(final GSRestClient client,
			final Service service, final String applicationName) throws RestException {
		String absolutePUName = ServiceUtils.getAbsolutePUName(applicationName, service.getName());
		Map<String, Object> adminData = (Map<String, Object>) client.getAdminData(
							"ProcessingUnits/Names/" + absolutePUName + "/ApplicationDependencies");

		String dependencyList = (String) adminData.get("ApplicationDependencies");
		for (String dependency : service.getDependsOn()) {
			String absoluteDependencyName = ServiceUtils.getAbsolutePUName(applicationName, dependency);
			if (!dependencyList.contains(absoluteDependencyName)) {
				AssertUtils.assertFail("Service " + service.getName() + " does not contain the " +
                        absoluteDependencyName + " dependency");
			}
		}
	}


	private void verifyApplicationUrls(String appName, boolean hasApacheLB, int port) {

		Client client = Client.create(new DefaultClientConfig());
		final WebResource service = client.resource(this.getRestUrl());

		if(hasApacheLB){

			String restApacheService = service.path("/admin/ProcessingUnits/Names/" + appName + ".apacheLB/ProcessingUnitInstances/0/ServiceDetailsByServiceId/USM/Attributes/Cloud%20Public%20IP").get(String.class);
			int urlStartIndex = restApacheService.indexOf(":") + 2;
			int urlEndIndex = restApacheService.indexOf("\"", urlStartIndex);

			String apacheServiceHostURL = restApacheService.substring(urlStartIndex, urlEndIndex);

			assertPageExists("http://" + IPUtils.getSafeIpAddress(apacheServiceHostURL) + ":" + port + "/");
		}
	}

	private void verifyServices(String applicationName, List<Service> services) throws IOException, InterruptedException {

		String command = "connect " + getRestUrl() + ";use-application " + applicationName + ";list-services";
		String output = CommandTestUtils.runCommandAndWait(command);

		for(Service singleService : services){
			AssertUtils.assertTrue("the service " + singleService.getName() + " is not running", output.contains(singleService.getName()));
		}
	}

	@Override
	protected boolean isReusableCloud() {
		return false;
	}

	protected void assertPageExists(String url) {

		try {
			WebUtils.isURLAvailable(new URL(url));
		} catch (Exception e) {
			AssertUtils.assertFail(e.getMessage());
		}
	}


	/**
	 * Installs and uninstalls a service that uses a linux agent template.
	 * 
	 * @throws Exception
	 */
	protected void testLinuxAgent() throws Exception {
		super.installServiceAndWait("tomcat", "tomcat");
		super.uninstallServiceAndWait("tomcat");
	}
}
