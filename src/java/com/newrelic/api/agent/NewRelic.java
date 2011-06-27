package com.newrelic.api.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * The New Relic api.  Consumers of this api can either add the newrelic-api.jar to their classpath or copy this 
 * source file into their source.
 * @author sdaubin
 *
 */
public final class NewRelic {
	
	static BufferedWriter bufferedWriter;
	static String enter= System.getProperty("line.separator");
	
	static{
		File f= new File("newrelic.log");
		FileWriter writer;
		try {
			writer = new FileWriter(f);
			bufferedWriter= new BufferedWriter(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//************************** Metric API ****************************************//

	/**
	 * Record a metric value for the given name.
	 * @param name
	 * @param millis
	 */
	public static void recordMetric(String name, float value) {}

	/**
	 * Record a response time in milliseconds for the given metric name.
	 * @param name
	 * @param millis
	 */
	public static void recordResponseTimeMetric(String name, long millis) {}

	/**
	 * Increment the metric counter for the given name.
	 * @param name
	 */
	public static void incrementCounter(String name) {}

	//**************************  Error collector ***********************************//

	/**
	 * Notice an exception and report it to the RPM service.  If this method is called within a transaction,
	 * the exception will be reported with the transaction when it finishes.  
	 * If it is invoked outside of a transaction, a traced error will be created and reported to RPM.
	 * @param throwable
	 * @param params	Custom parameters to include in the traced error.  May be null
	 */
	public static void noticeError(Throwable throwable, Map<String, String> params) {}

	/**
	 * Report an exception to the RPM service.
	 * @param throwable
	 * @see #noticeError(Throwable, Map)
	 */
	public static void noticeError(Throwable throwable) {}

	/**
	 * Notice an error and report it to the RPM service.  If this method is called within a transaction,
	 * the error message will be reported with the transaction when it finishes.  
	 * If it is invoked outside of a transaction, a traced error will be created and reported to RPM.
	 * @param message
	 * @param params	Custom parameters to include in the traced error.  May be null
	 */
	public static void noticeError(String message, Map<String, String> params) {}


	//****************************  Transaction APIs ********************************//

	/**
	 * Add a key/value pair to the current transaction.  These are reported in errors and transaction traces.
	 * 
	 * @param key
	 * @param value
	 * @ 
	 */
	public static void addCustomParameter(String key, Number value) {}

	public static void mockAddCustomParameter(String key, Number value) {
		try {
			bufferedWriter.write(key+"="+value+enter);
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a key/value pair to the current transaction.  These are reported in errors and transaction traces.
	 * 
	 * @param key
	 * @param value
	 * @ 
	 */
	public static void addCustomParameter(String key, String value) {}

	public static void mockAddCustomParameter(String key, String value) {
		try {
			bufferedWriter.write(key+"="+value+enter);
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the name of the current transaction.
	 * @param category
	 * @param name	The name of the transaction starting with a forward slash.  example: /store/order
	 */
	public static void setTransactionName(String category, String name) {}

	/**
	 * Ignore the current transaction.
	 */
	public static void ignoreTransaction() {}

	/**
	 * Ignore the current transaction for calculating Apdex score.
	 */
	public static void ignoreApdex() {}

	//****************************  Real User Monitoring ********************************//

	public static String getBrowserTimingHeader() {
		return null;
	}

	public static String getBrowserTimingFooter() {
		return null;
	}
}
