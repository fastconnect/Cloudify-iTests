package org.cloudifysource.quality.iTests.test.webui.recipes.applications;

import iTests.framework.utils.AssertUtils;
import iTests.framework.utils.AssertUtils.RepetitiveConditionProvider;
import iTests.framework.utils.ProcessingUnitUtils;

import java.io.IOException;

import org.openspaces.admin.gsc.GridServiceContainer;
import org.openspaces.admin.machine.Machine;
import org.openspaces.admin.pu.DeploymentStatus;
import org.openspaces.admin.pu.ProcessingUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.gigaspaces.webuitf.LoginPage;
import com.gigaspaces.webuitf.MainNavigation;
import com.gigaspaces.webuitf.topology.TopologyTab;
import com.gigaspaces.webuitf.topology.applicationmap.ApplicationMap;
import com.gigaspaces.webuitf.topology.applicationmap.ApplicationNode;
import com.gigaspaces.webuitf.topology.logspanel.LogsMachine;
import com.gigaspaces.webuitf.topology.logspanel.LogsPanel;
import com.gigaspaces.webuitf.topology.logspanel.PuLogsPanelService;

public class TerminateServiceContainerLogsPanelTest extends AbstractSeleniumApplicationRecipeTest {
	
//	private static final String TOMCAT_SERVICE_FULL_NAME = ServiceUtils.getAbsolutePUName(TRAVEL_APPLICATION_NAME, DEFAULT_TOMCAT_SERVICE_NAME);
	
	@Override
	@BeforeMethod
	public void install() throws IOException, InterruptedException {
		setCurrentApplication(TRAVEL_APPLICATION_NAME);
		super.install();
	}
	
	@Test(timeOut = DEFAULT_TEST_TIMEOUT * 2 , enabled = false)
	public void terminateContainerTest() throws InterruptedException, IOException {
		
		// get new login page
		LoginPage loginPage = getLoginPage();
		
		MainNavigation mainNav = loginPage.login();

		TopologyTab topology = mainNav.switchToTopology();
		
		ApplicationMap appMap = topology.getApplicationMap();
		
		topology.selectApplication(TRAVEL_APPLICATION_NAME);
		
		ApplicationNode tomcatNode = appMap.getApplicationNode(DEFAULT_TOMCAT_SERVICE_NAME);
		
		tomcatNode.select();
		
		ProcessingUnit travelPu = admin.getProcessingUnits().getProcessingUnit(DEFAULT_TOMCAT_SERVICE_NAME);
		
		final GridServiceContainer travelContainer = travelPu.getInstances()[0].getGridServiceContainer();
		
		LogsPanel logsPanel = topology.getTopologySubPanel().switchToLogsPanel();
		
		PuLogsPanelService travelLogsService = logsPanel.getPuLogsPanelService(DEFAULT_TOMCAT_SERVICE_NAME);
		
		Machine localHost = travelContainer.getMachine();
		
		final LogsMachine logsLocalHost = travelLogsService.getMachine(localHost,travelPu);
		
		assertTrue(logsLocalHost.containsGridServiceContainer(travelContainer));
		
		int gscAgentId = travelContainer.getAgentId();
		
		travelContainer.kill();
		
		mainNav.switchToTopology();
		
		tomcatNode.select();
		
		ProcessingUnitUtils.waitForDeploymentStatus(travelPu, DeploymentStatus.SCHEDULED);
		ProcessingUnitUtils.waitForDeploymentStatus(travelPu, DeploymentStatus.INTACT);
		
		appMap.deselectAllNodes();
		
		RepetitiveConditionProvider condition = new RepetitiveConditionProvider() {
			
			@Override
			public boolean getCondition() {
				return (!logsLocalHost.containsGridServiceContainer(travelContainer));
			}
		};
		
		AssertUtils.repetitiveAssertTrue("Container" + gscAgentId + "is still present", condition, waitingTime);
		uninstallApplication(TRAVEL_APPLICATION_NAME, true);
		
	}

}
