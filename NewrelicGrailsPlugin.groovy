class NewrelicGrailsPlugin {
	// the plugin version
	def version = "0.1"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "1.3.5 > *"
	// the other plugins this plugin depends on
	def dependsOn = [:]
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
	"grails-app/views/error.gsp"
	]

	// TODO Fill in these fields
	def author = "Your name"
	def authorEmail = ""
	def title = "Plugin summary/headline"
	def description = '''\\
	Brief description of the plugin.
	'''

	// URL to the plugin's documentation
	def documentation = "http://grails.org/plugin/newrelic"

	def doWithWebDescriptor = { xml ->
		// TODO Implement additions to web.xml (optional), this event occurs before 
	}

	def doWithSpring = {
		// TODO Implement runtime spring config (optional)
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
						/*throw new MissingMethodException(name, delegate.class, args)*/
					}
				}

				final String requestName = "${serviceClass.name}.${name}";

				boolean systemError = false;
				try {
					SPRING_COUNTER.bindContextIncludingCpu(requestName);
					return metaMethod.doMethodInvoke(delegate, args)
				} catch (final Error e) {
					systemError = true;
					throw e;
				} finally {
					SPRING_COUNTER.addRequestForCurrentContext(systemError);
				}
			}
		}
	}

	def doWithApplicationContext = { applicationContext ->
		// TODO Implement post initialization spring config (optional)
	}

	def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	def onConfigChange = { event ->
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}
}
