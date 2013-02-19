
import org.cloudifysource.dsl.utils.ServiceUtils;


service {
	name "groovy"
	type "UNDEFINED"
	
	elastic true
	numInstances 1
	maxAllowedInstances 1
	lifecycle { 
	
		
		start {
			println "This is a message from the start closure"
		} 
		
		locator {
			return [ServiceUtils.ProcessUtils.getSigar().pid]
		}
		stop {
			println "This is a message from the stop closure"
		}
		
	}

}