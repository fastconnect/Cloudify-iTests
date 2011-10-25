package test.webui.services.deployment;

import static test.utils.AdminUtils.loadGSCs;
import static test.utils.AdminUtils.loadGSM;
import static test.utils.LogUtils.log;

import org.junit.Assert;
import org.openspaces.admin.gsa.GridServiceAgent;
import org.openspaces.admin.gsm.GridServiceManager;
import org.openspaces.admin.machine.Machine;
import org.openspaces.admin.pu.DeploymentStatus;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.ProcessingUnitDeployment;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import test.utils.DeploymentUtils;
import test.utils.LogUtils;
import test.utils.ProcessingUnitUtils;
import test.webui.AbstractSeleniumTest;
import test.webui.interfaces.IDeployWindow;
import test.webui.objects.LoginPage;
import test.webui.objects.services.PuTreeGrid;
import test.webui.objects.services.ServicesTab;
import test.webui.objects.services.PuTreeGrid.WebUIProcessingUnit;

public class ProcessingUnitDeployTest extends AbstractSeleniumTest {
	
	Machine machineA;
	
	@BeforeMethod(alwaysRun = true)
	public void startSetup() {
		
		log("waiting for 1 machine");
		admin.getMachines().waitFor(1);

		log("waiting for 1 GSA");
		admin.getGridServiceAgents().waitFor(1);

		GridServiceAgent[] agents = admin.getGridServiceAgents().getAgents();
		GridServiceAgent gsaA = agents[0];

		machineA = gsaA.getMachine();

		log("starting: 1 GSM and 2 GSC's on 1 machine");
		GridServiceManager gsmA = loadGSM(machineA); 
		loadGSCs(machineA, 2);
		
		//we deploy 2 processing units with the Admin API so that they will appear in the deployment list on the web-ui
		
		DeploymentUtils.prepareApp("data");
		ProcessingUnit puProcessor = gsmA.deploy(new ProcessingUnitDeployment(DeploymentUtils.getProcessingUnit("data", "processor")));
		ProcessingUnitUtils.waitForDeploymentStatus(puProcessor, DeploymentStatus.INTACT);
		puProcessor.undeploy();
		ProcessingUnitUtils.waitForDeploymentStatus(puProcessor, DeploymentStatus.UNDEPLOYED);
		
		ProcessingUnit puSessionTest = gsmA.deploy(new ProcessingUnitDeployment(DeploymentUtils.getArchive("session-test-embedded.war")));
		ProcessingUnitUtils.waitForDeploymentStatus(puSessionTest, DeploymentStatus.INTACT);
		puSessionTest.undeploy();
		ProcessingUnitUtils.waitForDeploymentStatus(puSessionTest, DeploymentStatus.UNDEPLOYED);
		
	}
	
	 @Test(timeOut = DEFAULT_TEST_TIMEOUT, groups = {"cloudify" , "xap"})
	public void puDeploymentTest() throws InterruptedException {
		
		String puName = "processor";
		
		// get new login page
		LoginPage loginPage = getLoginPage();
		
		// get new topology tab
		ServicesTab topologyTab = loginPage.login().switchToServices();
		
		LogUtils.log("deplying processor...");
		// open the deployment window
		IDeployWindow puDeploy = topologyTab.openProcessingUnitDeployWindow(puName, 
				null, null, null, null, null, "[None]", null, null);
		
		// submit deployment specs
		puDeploy.sumbitDeploySpecs();
		
		// deploy onto the grid
		puDeploy.deploy();
		
		// wait for PU
		ProcessingUnit pu = null;
		int seconds = 0;
		while (pu == null) {
			if (seconds >= 30) Assert.fail("admin wasnt able to capture pu"); 
			pu = admin.getProcessingUnits().getProcessingUnit(puName);
			seconds++;
			Thread.sleep(1000);
		}
		ProcessingUnitUtils.waitForDeploymentStatus(pu, DeploymentStatus.INTACT);
		
		// close the deployment window
		puDeploy.closeWindow();
		
		// get pu tree grid
		PuTreeGrid puGrid = topologyTab.getPuTreeGrid();
		
		// assert pu is present
		WebUIProcessingUnit processingUnit = puGrid.getProcessingUnit(puName);
		
		assertTrue(processingUnit != null);
		
		// undeploy pu
		pu.undeploy();
		ProcessingUnitUtils.waitForDeploymentStatus(pu, DeploymentStatus.UNDEPLOYED);
		
		puName = "session-test-embedded";
		
		LogUtils.log("deplyong session test embedded...");
		// open a different deployment window
		puDeploy = topologyTab.openProcessingUnitDeployWindow(puName, 
				null, null, null, null, null, "[None]", null, null);
		
		// submit deployment specs
		puDeploy.sumbitDeploySpecs();
		
		// deploy onto the grid
		puDeploy.deploy();
		
		// wait for PU
		pu = null;
		seconds = 0;
		while (pu == null) {
			if (seconds >= 30) Assert.fail("admin wasnt able to capture pu"); 
			pu = admin.getProcessingUnits().getProcessingUnit(puName);
			seconds++;
			Thread.sleep(1000);
		}
		ProcessingUnitUtils.waitForDeploymentStatus(pu, DeploymentStatus.INTACT);
		
		// close the deployment window
		puDeploy.closeWindow();
		
		
		// assert pu is present
		processingUnit = puGrid.getProcessingUnit(puName);
		
		assertTrue(processingUnit != null);
		
		// undeploy pu
		pu.undeploy();
		ProcessingUnitUtils.waitForDeploymentStatus(pu, DeploymentStatus.UNDEPLOYED);
		
	}
}
