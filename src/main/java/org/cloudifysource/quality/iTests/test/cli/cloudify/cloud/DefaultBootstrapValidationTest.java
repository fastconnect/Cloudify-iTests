package org.cloudifysource.quality.iTests.test.cli.cloudify.cloud;

import iTests.framework.tools.SGTestHelper;
import iTests.framework.utils.IOUtils;

import java.io.File;
import java.io.IOException;

import org.cloudifysource.quality.iTests.framework.utils.CloudBootstrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class DefaultBootstrapValidationTest extends NewAbstractCloudTest {

	private String groovyFileName;
	CloudBootstrapper bootstrapper;

	
	@BeforeClass
	public void init() throws Exception {
		bootstrapper = new CloudBootstrapper();
		bootstrapper.scanForLeakedNodes(true);
		bootstrapper.setBootstrapExpectedToFail(true);
	}
	
	
	public void wrongCredentialsTest(final String groovyFileName) throws Exception {
		try {
			this.groovyFileName = groovyFileName;
			bootstrapper.scanForLeakedNodes(false);
			super.bootstrap(bootstrapper);
			String bootstrapOutput = bootstrapper.getLastActionOutput();
			assertTrue("The credentials are wrong but the wrong error was thrown. Reported error: " + bootstrapOutput,
					bootstrapOutput.contains("HTTP/1.1 401 Unauthorized"));
		} catch (Exception e) {
			throw e;
		} finally {
			bootstrapper.scanForLeakedNodes(true);
		}
	}

	
	public void wrongImageTest(final String groovyFileName) throws Exception {
		this.groovyFileName = groovyFileName;
		super.bootstrap(bootstrapper);
		String bootstrapOutput = bootstrapper.getLastActionOutput();
		assertTrue("The imageId is invalid but the wrong error was thrown. Reported error: " + bootstrapOutput,
				bootstrapOutput.contains("Image") && bootstrapOutput.contains("is not valid for location"));
	}
	
	
	public void wrongHardwareTest(final String groovyFileName) throws Exception {
		this.groovyFileName = groovyFileName;
		super.bootstrap(bootstrapper);
		String bootstrapOutput = bootstrapper.getLastActionOutput();
		assertTrue("The hardwareId is invalid but the wrong error was thrown. Reported error: " + bootstrapOutput,
				bootstrapOutput.contains("hardware") && bootstrapOutput.contains("is not valid for location"));
	}
	
	
	public void wrongSecurityGroupTest(final String groovyFileName) throws Exception {
		this.groovyFileName = groovyFileName;
		super.bootstrap(bootstrapper);
		String bootstrapOutput = bootstrapper.getLastActionOutput();
		assertTrue("The security group name is wrong but the wrong error was thrown. "
				+ "Reported error: " + bootstrapOutput,
				bootstrapOutput.contains("Security group") && bootstrapOutput.contains("does not exist"));
	}
	
	
	public void wrongKeyPairTest(final String groovyFileName) throws Exception {
		this.groovyFileName = groovyFileName;
		super.bootstrap(bootstrapper);
		String bootstrapOutput = bootstrapper.getLastActionOutput();
		assertTrue("The key-pair name is wrong but the wrong error was thrown. Reported error: " + bootstrapOutput,
				bootstrapOutput.contains("is invalid or in the wrong availability zone"));
	}
	
	
	public void wrongCloudifyUrlTest(final String groovyFileName) throws Exception {
		this.groovyFileName = groovyFileName;
		super.bootstrap(bootstrapper);
		String bootstrapOutput = bootstrapper.getLastActionOutput();
		assertTrue("The cloudify URL is wrong but the wrong error was thrown. Reported error: " + bootstrapOutput,
				bootstrapOutput.contains("Invalid cloudify URL"));
	}
	

	@AfterClass
	public void teardown() throws Exception {
		super.teardown();
	}
	

	@Override
	protected boolean isReusableCloud() {
		return false;
	}
	

	protected void customizeCloud() throws IOException {
		//replace the cloud.groovy with a wrong version, to fail the validation.
		File standardGroovyFile = new File(getService().getPathToCloudFolder(), getCloudName() + "-cloud.groovy");
		File wrongGroovyFile = new File(SGTestHelper.getSGTestRootDir() + "/src/main/resources/apps/cloudify/cloud/" + getCloudName() + "/" + groovyFileName);
		IOUtils.replaceFile(standardGroovyFile, wrongGroovyFile);
		File newFile = new File(getService().getPathToCloudFolder(), groovyFileName);
		if (newFile.exists()) {
			newFile.renameTo(standardGroovyFile);
		}
	}
	
}