package org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.hp.storage.staticstorage;

import org.cloudifysource.esc.driver.provisioning.storage.StorageProvisioningException;
import org.cloudifysource.quality.iTests.framework.utils.ApplicationInstaller;
import org.cloudifysource.quality.iTests.framework.utils.RecipeInstaller;
import org.cloudifysource.quality.iTests.framework.utils.ServiceInstaller;
import org.cloudifysource.quality.iTests.test.AbstractTestSupport;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.AbstractStorageAllocationTest;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.AbstractStorageTest;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.services.ec2.Ec2CloudService;
import org.testng.annotations.*;

import java.util.concurrent.TimeoutException;

/**
 * Author: nirb
 * Date: 25/02/13
 */
public class HpFailedToInstallTest extends AbstractStorageAllocationTest {

    @BeforeClass(alwaysRun = true)
    protected void bootstrap() throws Exception {
        super.bootstrap();
    }

    @Test(timeOut = AbstractTestSupport.DEFAULT_TEST_TIMEOUT * 4, enabled = true)
    public void testLinux() throws Exception {
        storageAllocationTester.testFaultyInstallLinux();
    }

    @AfterMethod
    public void cleanup() {
        RecipeInstaller installer = storageAllocationTester.getInstaller();
        if (installer instanceof ServiceInstaller) {
            ((ServiceInstaller) installer).uninstallIfFound();
        } else {
            ((ApplicationInstaller) installer).uninstallIfFound();
        }
    }

    @AfterClass
    public void scanForLeakes() throws TimeoutException, StorageProvisioningException {
        super.scanForLeakedVolumesCreatedViaTemplate("SMALL_BLOCK");
    }

    @AfterClass(alwaysRun = true)
    protected void teardown() throws Exception {
        super.teardown();
    }


    @Override
    protected String getCloudName() {
        return "hp";
    }

    @Override
    protected boolean isReusableCloud() {
        return false;
    }
}
