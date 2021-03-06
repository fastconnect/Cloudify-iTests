service {
	name serviceName
	icon "${iconName}.png"
	type "UNDEFINED"
	
	url applicationName
  
	lifecycle {

		init initService

		preInstall {println "This is the preInstall event" }
		postInstall {println "This is the postInstall event"}
		
		preStart {println "This is the preStart event" }

		start (["Win.*":"run.bat", "Linux":"run.sh"])

		postStart {println "This is the postStart event" }

		preStop {println "This is the preStop event" }
		postStop {println "This is the postStop event" }
		shutdown {println "This is the shutdown event" }
	}	
}