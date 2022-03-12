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

	// the following function returns the properties instance which can be used to
	// access the configs
	public static Properties getFile() throws Exception{

		// intialization
		Properties configFile = new Properties();

		// try with resources to open and access the config file
		try (FileInputStream propertyfile = new FileInputStream(
				 "./resources/config.properties")) {
			configFile.load(propertyfile);
		}
		// logs the error and exists the system in case of empty config file
		catch (IOException e) {
			
			logger.error("Issue With user defined config file: empty/missing, Defalut Config File used");
			
			try (FileInputStream propertyfile = new FileInputStream("./resources/defaultconfig.properties")) {
				configFile.load(propertyfile);
			}
			
			catch(IOException exception) {
			throw new Exception("Default configs also missing ");
			}
			
			
			
			
		}
		
		return configFile;

	}

}
