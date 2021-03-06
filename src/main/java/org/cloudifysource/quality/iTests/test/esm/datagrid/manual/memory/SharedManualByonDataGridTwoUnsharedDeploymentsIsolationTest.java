package org.cloudifysource.quality.iTests.test.esm.datagrid.manual.memory;

import iTests.framework.utils.GsmTestUtils;
import org.cloudifysource.quality.iTests.test.esm.AbstractFromXenToByonGSMTest;
import org.openspaces.admin.machine.Machine;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.elastic.ElasticMachineProvisioningConfig;
import org.openspaces.admin.pu.elastic.config.ManualCapacityScaleConfigurer;
import org.openspaces.admin.space.ElasticSpaceDeployment;
import org.openspaces.admin.space.Space;
import org.openspaces.core.util.MemoryUnit;
import org.testng.annotations.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 *  Setup:
 *  myGrid: Shared deployment, total 512m, per container 256m
 *  myGrid2: Shared deployment, total 512m, per container 256m
 *
 *  Note: Both deployments are shared but the have different share id thus are expected not to share machines
 *
 *  expecting each deployment to be on two different 2 machines.
 *  expecting 4 machines in total
 *
 *  There are two versions: one with DiscoveredMachineProvisioningConfig and the other with
 *  XenServerMachineProvisioningConfig. 
 *
 * @author dank
 */


public class SharedManualByonDataGridTwoUnsharedDeploymentsIsolationTest extends AbstractFromXenToByonGSMTest {

    @BeforeMethod
    public void beforeTest() {
        super.beforeTestInit();
    }

    @BeforeClass
    protected void bootstrap() throws Exception {
        super.bootstrapBeforeClass();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        super.afterTest();
    }

    @AfterClass(alwaysRun = true)
    protected void teardownAfterClass() throws Exception {
        super.teardownAfterClass();
    }


    /**
     * Disabled until GS-9342 is fixed
     * @throws Exception
     */
    @Test(timeOut = DEFAULT_TEST_TIMEOUT, groups = "1", enabled = false)
    public void manualXenDataGridDeploymentTwoIndenpendentUnsharedIsolationXenMachineProvisioningTest() throws Exception {
        repetitiveAssertNumberOfGSAsAdded(1, OPERATION_TIMEOUT);
        manualXenDataGridDeploymentTwoIndenpendentUnsharedIsolationTest(getMachineProvisioningConfig());
    }
    
    @Test(timeOut = DEFAULT_TEST_TIMEOUT, groups = "1", enabled = true)
    public void manualXenDataGridDeploymentTwoIndenpendentUnsharedIsolationDiscoveredsMachineProvisioningTest() throws Exception {
        setupDiscovereMachineProvisioningEnvironment();
        repetitiveAssertNumberOfGSAsAdded(4, OPERATION_TIMEOUT);
        manualXenDataGridDeploymentTwoIndenpendentUnsharedIsolationTest(getDiscoveredMachineProvisioningConfig());
    }

    public void manualXenDataGridDeploymentTwoIndenpendentUnsharedIsolationTest(ElasticMachineProvisioningConfig machineProvisioningConfig) throws Exception {

        repetitiveAssertNumberOfGSCsAdded(0, OPERATION_TIMEOUT);
        final ProcessingUnit pu = super.deploy(new ElasticSpaceDeployment("myGrid")
                .maxMemoryCapacity(512, MemoryUnit.MEGABYTES)
                .memoryCapacityPerContainer(256,MemoryUnit.MEGABYTES)
                .scale(new ManualCapacityScaleConfigurer()
                        .memoryCapacity(512,MemoryUnit.MEGABYTES)
                        .create())
                .sharedMachineProvisioning("myGridShare", machineProvisioningConfig)
        );

        final ProcessingUnit pu2 = super.deploy(new ElasticSpaceDeployment("myGrid2")
                .maxMemoryCapacity(512, MemoryUnit.MEGABYTES)
                .memoryCapacityPerContainer(256,MemoryUnit.MEGABYTES)
                .scale(new ManualCapacityScaleConfigurer()
                        .memoryCapacity(512,MemoryUnit.MEGABYTES)
                        .create())
                .sharedMachineProvisioning("myGrid2Share", machineProvisioningConfig)
        );
        GsmTestUtils.waitForScaleToCompleteIgnoreCpuSla(pu, 2, OPERATION_TIMEOUT);
        GsmTestUtils.waitForScaleToCompleteIgnoreCpuSla(pu2, 2, OPERATION_TIMEOUT);
        Space space = pu.waitForSpace(OPERATION_TIMEOUT, TimeUnit.MILLISECONDS);
        Space space2 = pu2.waitForSpace(OPERATION_TIMEOUT, TimeUnit.MILLISECONDS);

        assertNotNull("Failed getting space instance", space);
        assertNotNull("Failed getting space2 instance", space2);

        assertTrue("Failed waiting for space instances",
                space.waitFor(space.getTotalNumberOfInstances(),
                        OPERATION_TIMEOUT, TimeUnit.MILLISECONDS));
        assertTrue("Failed waiting for space2 instances",
                space2.waitFor(space2.getTotalNumberOfInstances(),
                        OPERATION_TIMEOUT, TimeUnit.MILLISECONDS));


        repetitiveAssertNumberOfGSAsAdded(4, OPERATION_TIMEOUT);

        repetitiveAssertNumberOfGSCsAdded(4, OPERATION_TIMEOUT);
        repetitiveAssertNumberOfGSCsRemoved(0, OPERATION_TIMEOUT);


        assertTrue("Primary/Backup should be deployed on different machines",
                !space.getInstances()[0].getMachine().equals(space.getInstances()[1].getMachine()));

        assertTrue("Primary/Backup should be deployed on different machines",
                !space2.getInstances()[0].getMachine().equals(space2.getInstances()[1].getMachine()));


        Set<Machine> machines = new HashSet<Machine>();

        machines.add(space.getInstances()[0].getMachine());
        machines.add(space.getInstances()[1].getMachine());
        machines.add(space2.getInstances()[0].getMachine());
        machines.add(space2.getInstances()[1].getMachine());

        assertEquals("PUs should be on four different machines", 4, machines.size());

        assertUndeployAndWait(pu);

    }

    public void setupDiscovereMachineProvisioningEnvironment() throws Exception {
        startNewByonMachines(getElasticMachineProvisioningCloudifyAdapter(),3,OPERATION_TIMEOUT,TimeUnit.MILLISECONDS);
    }


}


