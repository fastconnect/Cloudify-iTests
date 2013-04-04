package org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.services.rackspace;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.cloudifysource.quality.iTests.framework.utils.IOUtils;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.services.JCloudsCloudService;
import org.cloudifysource.quality.iTests.test.cli.cloudify.cloud.services.tools.openstack.RackspaceClient;

public class RackspaceCloudService extends JCloudsCloudService {
	private static final int DEFAULT_SERVER_SHUTDOWN_TIMEOUT = 5 * 60000;
    private static final String RACKSPACE_CERT_PROPERTIES = CREDENTIALS_FOLDER + "/cloud/rackspace/rackspace-cred.properties";

    private Properties certProperties = getCloudProperties(RACKSPACE_CERT_PROPERTIES);
	private String user = certProperties.getProperty("user");
	private String apiKey = certProperties.getProperty("apiKey");
	private String openstackEndpoint = certProperties.getProperty("openstackEndpoint");
	private String openstackIdentityEndpoint = certProperties.getProperty("openstackIdentityEndpoint");
	private String hardwareId = certProperties.getProperty("hardwareId");
	private String linuxImageId = certProperties.getProperty("linuxImageId");

	private RackspaceClient rackspaceClient;

	public RackspaceCloudService() {
		super("rackspace");
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}


	@Override
	public void injectCloudAuthenticationDetails()
			throws IOException {

		getProperties().put("user", this.user);
		getProperties().put("apiKey", this.apiKey);
		getProperties().put("openstackEndpoint", this.openstackEndpoint);
		getProperties().put("openstackIdentityEndpoint", this.openstackIdentityEndpoint);
		getProperties().put("hardwareId", this.hardwareId);
		getProperties().put("linuxImageId", this.linuxImageId);
		
		Map<String, String> propsToReplace = new HashMap<String, String>();
		propsToReplace.put("machineNamePrefix " + "\"agent\"", "machineNamePrefix " + '"' + getMachinePrefix()
				+ "cloudify-agent" + '"');
		propsToReplace.put("managementGroup " + "\"management\"", "managementGroup " + '"' + getMachinePrefix()
				+ "cloudify-manager" + '"');
		propsToReplace.put("numberOfManagementMachines 1", "numberOfManagementMachines " + getNumberOfManagementMachines());
		propsToReplace.put("\"openstack.wireLog\": \"false\"", "\"openstack.wireLog\": \"true\"");

		IOUtils.replaceTextInFile(getPathToCloudGroovy(), propsToReplace);
	}

	private RackspaceClient createClient() {
		RackspaceClient client = new RackspaceClient();
		client.setConfig(getCloud());
		return client;
	}

	
	/*@Override
	public boolean scanLeakedAgentNodes() {
		
		if (rackspaceClient == null) {
			this.rackspaceClient = createClient();
		}
		
		String token = rackspaceClient.createAuthenticationToken();

		final String agentPrefix = getCloud().getProvider().getMachineNamePrefix();

		return checkForLeakedNode(token, agentPrefix);
	

	}*/
	
	
	/*@Override
	public boolean scanLeakedAgentAndManagementNodes() {
		if(rackspaceClient == null) {
			rackspaceClient = createClient();
		}
		String token = rackspaceClient.createAuthenticationToken();

		final String agentPrefix = getCloud().getProvider().getMachineNamePrefix();
		final String mgmtPrefix = getCloud().getProvider().getManagementGroup();
		
		final boolean result = checkForLeakedNode(token, agentPrefix, mgmtPrefix);
		this.rackspaceClient.close();
		return result;

	}*/

	/*private boolean checkForLeakedNode(String token, final String... prefixes) {
		List<Node> nodes;
		try {
			nodes = rackspaceClient.listServers(token);
		} catch (OpenstackException e) {
			throw new IllegalStateException("Failed to query openstack cloud for current servers", e);
		}

		List<Node> leakedNodes = new LinkedList<Node>();
		for (Node node : nodes) {
			if (node.getStatus().equals(OpenstackClient.MACHINE_STATUS_ACTIVE)) {
				for (String prefix : prefixes) {
					if (node.getName().startsWith(prefix)) {
						LogUtils.log("Found leaking node with name " + node.getName());
						leakedNodes.add(node);
					}
				}

			}
		}

		if (leakedNodes.size() > 0) {
			for (Node node : leakedNodes) {
				LogUtils.log("Shutting down: " + node);
				try {
					rackspaceClient.terminateServer(node.getId(), token, System.currentTimeMillis() + DEFAULT_SERVER_SHUTDOWN_TIMEOUT);
				} catch (Exception e) {
					LogUtils.log("Failed to terminate Rackspace openstack node: " + node.getId()
							+ ". This node may be leaking. Node details: " + node + ", Error was: " + e.getMessage(), e);
				}
			}
			return false;
		}
		
		return true;
	}*/
	

	@Override
	public void addOverrides(Properties overridesProps) {
		overridesProps.put("openstack.endpoint", openstackEndpoint);		
		overridesProps.put("openstack.identity.endpoint", openstackIdentityEndpoint);
	}
}
