package test.cli.cloudify;

import java.io.IOException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.tools.SGTestHelper;
import framework.utils.ApplicationInstaller;
import framework.utils.LocalCloudBootstrapper;

public class CustomSecurityFileTest extends AbstractSecuredLocalCloudTest{

	private static final String SGTEST_ROOT_DIR = SGTestHelper.getSGTestRootDir().replace('\\', '/');
	private static final String CLOUD_ADMIN_USER_AND_PASSWORD = "John"; 
	private static final String VIEWER_USER_AND_PASSWORD = "Amanda"; 
	private static final String APP_NAME = "simple";
	private static final String CUSTUM_SECURITY_FILE_PATH = SGTEST_ROOT_DIR + "/src/main/config/security/spring-security.xml";
	private static final String APP_PATH = SGTEST_ROOT_DIR + "/src/main/resources/apps/USM/usm/applications/" + APP_NAME;
	
	@Override
	@BeforeMethod
	public void beforeTest() {
		LocalCloudBootstrapper bootstrapper = new LocalCloudBootstrapper();
		bootstrapper.securityFilePath(CUSTUM_SECURITY_FILE_PATH);
		beforeTest(bootstrapper);		
	}
	
	@Test(timeOut = DEFAULT_TEST_TIMEOUT, enabled = false)
	public void installWithcustomCloudAdminTest() throws IOException, InterruptedException {
		
		ApplicationInstaller appInstaller = new ApplicationInstaller(restUrl, APP_NAME);
		String output = appInstaller.setCloudifyUsername(CLOUD_ADMIN_USER_AND_PASSWORD).setCloudifyPassword(CLOUD_ADMIN_USER_AND_PASSWORD).setRecipePath(APP_PATH).install();
				
		appInstaller.assertInstall(output);
	}
	
	@Test(timeOut = DEFAULT_TEST_TIMEOUT, enabled = false)
	public void installWithCustomViewerTest() throws IOException, InterruptedException{
		
		ApplicationInstaller appInstaller = new ApplicationInstaller(restUrl, APP_NAME);
		String output = appInstaller.setCloudifyUsername(VIEWER_USER_AND_PASSWORD).setCloudifyPassword(VIEWER_USER_AND_PASSWORD).setRecipePath(APP_PATH).setExpectToFail(true).install();

		assertTrue("install access granted to " + VIEWER_USER_AND_PASSWORD, output.contains("no_permission_access_is_denied"));
		appInstaller.assertInstall(output);
	}
}
