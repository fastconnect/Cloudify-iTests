import org.cloudifysource.utilitydomain.context.ServiceContextFactory
import java.util.concurrent.TimeUnit

config = new ConfigSlurper().parse(new File("tomcat-service.properties").toURL())

println "#################### calculating cassandra host"
println "waiting for cassandra"
serviceContext = ServiceContextFactory.getServiceContext()
cassandraService = serviceContext.waitForService("cassandra-extend", 20, TimeUnit.SECONDS)
cassandraInstances = cassandraService.waitForInstances(cassandraService.numberOfPlannedInstances, 60, TimeUnit.SECONDS)
cassandraHost = cassandraInstances[0].hostAddress

script = "${config.name}/bin/catalina"

println "#################### got cassandra host: ${cassandraHost}"

//start tomcat
println "executing command ${script}"
new AntBuilder().sequential {
    exec(executable:"${script}.sh", osfamily:"unix") {
        env(key:"CATALINA_HOME", value: "${config.name}")
        env(key:"CATALINA_BASE", value: "${config.name}")
        env(key:"CATALINA_TMPDIR", value: "${config.name}/temp")
        env(key:"CATALINA_OPTS", value:"-Dcom.sun.management.jmxremote.port=11099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false")
        env(key:"CASSANDRA_IP", value:cassandraHost)
        arg(value:"run")
    }
    exec(executable:"${script}.bat", osfamily:"windows") {
        env(key:"CATALINA_HOME", value: "${config.name}")
        env(key:"CATALINA_BASE", value: "${config.name}")
        env(key:"CATALINA_TMPDIR", value: "${config.name}/temp")
        env(key:"CATALINA_OPTS", value:"-Dcom.sun.management.jmxremote.port=11099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false")
        env(key:"CASSANDRA_IP", value:cassandraHost)
        arg(value:"run")
    }
}

