package assignment.filehandling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

//The following class acts as a basic template to access config file
public class ReadProperties {

	// log4j instance
	public static Logger logger = LogManager.getLogger(RecordMaintenanceSystem.class.getName());
	// intialization
	public static Properties configFile = new Properties();

	// the following function returns the properties instance which can be used
	// toaccess the configs
	public static void getFile() {

		// try with resources to open and access the config file
		try (FileInputStream propertyfile = new FileInputStream("./resources/config.properties")) {
			configFile.load(propertyfile);
		}
		// logs the error and exists the system in case of empty config file
		catch (IOException e) {
			logger.error("Issue With user defined config file: empty/missing, Defalut Configs used");
		}
	}

	// the following fucntion returns the property type
	public static String getResource(String property) {
		return configFile.getProperty(property);
	}

}
