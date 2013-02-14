package org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.ec2;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.cloudifysource.quality.iTests.framework.utils.LogUtils;
import org.cloudifysource.quality.iTests.framework.utils.ScriptUtils;
import org.cloudifysource.quality.iTests.test.AbstractTestSupport;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.NewAbstractCloudTest;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.services.ec2.Ec2CloudService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Ec2PropertiesFileTest extends NewAbstractCloudTest {
	private static final String MANAGEMENT_MACHINE_TEMPLATE_MY_HARDWARE_ID = "managementMachineTemplate myHardwareId";
	final String serviceName = "tomcat";
	final String tomcatServicePath = ScriptUtils.getBuildPath() + "/recipes/services/" + serviceName;

	@Override
	protected String getCloudName() {
		return "ec2";
	}
	
	@BeforeClass(alwaysRun = true)
	protected void bootstrap() throws Exception {
		super.bootstrap();
	}
	
	@Test(timeOut = AbstractTestSupport.DEFAULT_TEST_TIMEOUT * 4, enabled = true)
	public void testInstallationWithPropsFile() throws Exception {
		String pathToCloudFolder = ((Ec2CloudService)cloudService).getPathToCloudFolder();
		File cloudConfig = new File(pathToCloudFolder, "ec2-cloud.groovy");
		String configFileAsString = FileUtils.readFileToString(cloudConfig);
		LogUtils.log("asserting cloud was bootstrapped with the correct properties (properties were overridden)");
		AbstractTestSupport.assertTrue("Management machine did not start using the properties defined in the properties file",
                configFileAsString.contains(MANAGEMENT_MACHINE_TEMPLATE_MY_HARDWARE_ID));

		LogUtils.log("Testing service installation");
		installServiceAndWait(tomcatServicePath, serviceName);
		uninstallServiceAndWait(serviceName);
		
		super.scanForLeakedAgentNodes();
	}

	@Override
	protected void customizeCloud() throws Exception {
		
		//Set the management machine template option to be taken from the cloud props file.
		((Ec2CloudService)cloudService).getAdditionalPropsToReplace().put("managementMachineTemplate \"SMALL_LINUX\"",
				MANAGEMENT_MACHINE_TEMPLATE_MY_HARDWARE_ID);
		
		// add this prop to the properties file attached to the cloud driver
		getService().getProperties().put("myHardwareId", "SMALL_UBUNTU");
	}
	
	@AfterClass(alwaysRun = true)
	protected void teardown() throws Exception {
		super.teardown();
	}
	
	@Override
	protected boolean isReusableCloud() {
		return false;
	}

}
