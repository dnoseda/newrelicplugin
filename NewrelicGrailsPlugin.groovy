import com.newrelic.api.agent.NewRelic
class NewrelicGrailsPlugin {
	// the plugin version
	def version = "0.3"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "1.3.5 > *"
	// the other plugins this plugin depends on
	def dependsOn = [:]
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
	"grails-app/views/error.gsp"
	]
    def author = "Damian Noseda"
    def authorEmail = "dnoseda@gmail.com"
    def title = "NewRelic"
    def description = '''\\
Integration of newrelic with grails applications
'''

	def doWithWebDescriptor = { xml ->
	}

	def doWithSpring = {
	}

	def doWithDynamicMethods = {ctx ->
		//Enable groovy meta programming
		ExpandoMetaClass.enableGlobally()
		//For each service class in Grails, the plugin use groovy meta programming (invokeMethod)
		//to 'intercept' method call and collect infomation for monitoring purpose.
		//The code below mimics 'MonitoringSpringInterceptor.invoke()'

		application.serviceClasses.each {serviceArtifactClass ->
			def serviceClass = serviceArtifactClass.getClazz()

			serviceClass.metaClass.invokeMethod = {String name, args ->
				def metaMethod = delegate.metaClass.getMetaMethod(name, args)
				if (!metaMethod) {
					List methods = delegate.metaClass.getMethods();
					boolean found = false
					for (int i = 0; i < methods.size(); i++) {
						groovy.lang.MetaMethod method = (groovy.lang.MetaMethod) methods.get(i);
						if (method.getName() == name) {
							metaMethod = method
							found = true
							break
						}

					}
					if(!found && delegate.metaClass.properties.find {it.name == name}){
						def property = delegate."${name}"
						if(property instanceof Closure){
							found = true
							metaMethod = [doMethodInvoke: {dlg, arguments-> property.call(arguments)}]
						}
					}
					if (!found){
						return delegate.metaClass.invokeMissingMethod(delegate, name, args)
					}
				}

				final String requestName = "${serviceClass.name}.${name}";
				long initTime = System.currentTimeMillis()

				boolean systemError = false;
				try {
					NewRelic.incrementCounter(requestName)
					initTime = System.currentTimeMillis()
					return metaMethod.doMethodInvoke(delegate, args)
				} catch (final Error e) {
					systemError = true;
					NewRelic.noticeError(e, [msg:"calling to $requestName"])
					throw e;
				} finally {
					NewRelic.recordMetric(requestName, initTime - System.currentTimeMillis())
				}
			}
		}
	}

	def doWithApplicationContext = { applicationContext ->
	}

	def onChange = { event ->
	}

	def onConfigChange = { event ->
	}
}
