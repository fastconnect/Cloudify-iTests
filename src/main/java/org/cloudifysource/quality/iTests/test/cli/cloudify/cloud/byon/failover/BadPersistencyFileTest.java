package org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.byon.failover;

import org.testng.annotations.*;

/**
 * User: nirb
 * Date: 06/03/13
 */
public class BadPersistencyFileTest extends AbstractByonManagementPersistencyTest {

    @BeforeClass(alwaysRun = true)
    public void bootstrapAndInit() throws Exception{
        super.bootstrapAndInstallService();
    }

    @Test(timeOut = DEFAULT_TEST_TIMEOUT * 4, enabled = true)
    public void testBadPersistencyFile() throws Exception {
        super.testBadPersistencyFile();
    }
}