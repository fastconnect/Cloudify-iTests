cloud {
    name = "openstack"

    configuration {
        managementMachineTemplate "LINUX"
    }

    provider {
        provider "openstack-nova"
        machineNamePrefix "cloudify-agent-"
        managementOnlyFiles ([])
        managementGroup "cloudify-manager-"
        numberOfManagementMachines 1
        reservedMemoryCapacityPerMachineInMB 1024
    }

    user {
        user "${tenant}:${user}"
        apiKey apiKey
    }

    cloudCompute {
        templates ([
            LINUX : computeTemplate{
                imageId imageId
                remoteDirectory remoteDirectory
                machineMemoryMB 1600
                hardwareId hardwareId
                localDirectory "upload"
                keyFile keyFile
                username "ubuntu"
                options ([
                    _COMPUTE_SERVICE_NAME_,
                    _NETWORK_SERVICE_NAME_,
                    _SKIP_EXTERNAL_NETWORKING_,
                    "keyPairName" : keyPair
                ])
                overrides ([
                    "jclouds.endpoint": openstackUrl
                ])
                computeNetwork {
                    networks (["SOME_INTERNAL_NETWORK_1"])
                }
            }
        ])
    }
}