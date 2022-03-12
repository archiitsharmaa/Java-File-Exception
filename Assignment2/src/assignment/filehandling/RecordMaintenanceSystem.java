package assignment.filehandling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 * The following class is purposed to work as a Record Maintains System
 * 
 * @author archit.sharma
 *
 */

public class RecordMaintenanceSystem {

	// static variables accessed as global variables
	public static int duplicateCount = 0;
	public static LinkedHashSet<Employee> valuesUpdated = new LinkedHashSet<>();
	public static Logger log = LogManager.getLogger(RecordMaintenanceSystem.class.getName());
	
	// This method reads a file location and converts the data into list of javaBeans
	public static List<Employee> csvReader(String file) throws Exception {

		// try catch block to read a file data and add there values to java bean objects using opencsv
		List<Employee> csvBeanReader = null;
		try (FileReader dataFile = new FileReader(file)) {
			
			//convert file to bean list
			csvBeanReader = new CsvToBeanBuilder(dataFile).withType(Employee.class).build().parse();

			// throws error when the file is empty
			if (csvBeanReader.size() == 0) {
				throw new Exception("The record were empty");
			}

		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File Not Found");
		} catch (IOException e) {
			throw new Exception("The File read failed");
		}
		return csvBeanReader;
	}

	// This method takes a list of bean objects and maps them to a hash map
	public static LinkedHashMap<String, Employee> beanMapper(List<Employee> beanList) {

		// Hash map declaration
		LinkedHashMap<String, Employee> userRecordMap = new LinkedHashMap<>();
		// Initializing values on map
		for (Employee cr : beanList) {
			userRecordMap.put(cr.getID(), cr);
		}
		return userRecordMap;
	}

	// This method takes a map and maps more bean object on it
	public static LinkedHashMap<String, Employee> newValuesMapper(LinkedHashMap<String, Employee> databaseRecord,
			List<Employee> inputRecord) {

		// adding values to the map and separating the updated records
		for (Employee cr : inputRecord) {
			if (databaseRecord.put(cr.getID(), cr) != null) {
				duplicateCount++;
				valuesUpdated.add(cr);
			}
		}

		return databaseRecord;
	}

	// This method writes data on the file
	public static void recordWriter(String filepath, List<Employee> beanList) throws Exception {

		// try catch block to write files
		try (FileWriter writer = new FileWriter(filepath)) {
			// Create Mapping Strategy to arrange the column name in order
			ColumnPositionMappingStrategy<Employee> mappingStrategy = new ColumnPositionMappingStrategy<Employee>();
			mappingStrategy.setType(Employee.class);

			// Arrange column name as provided in below array.
			String[] columns = new String[] { "ID", "Name", "Age", "Gender", "Earning", "Expenditure" };
			mappingStrategy.setColumnMapping(columns);

			// Creating StatefulBeanToCsv object
			StatefulBeanToCsvBuilder<Employee> builder = new StatefulBeanToCsvBuilder<Employee>(writer);
			StatefulBeanToCsv<Employee> beanWriter = builder.withSeparator(CSVWriter.DEFAULT_SEPARATOR).build();

			// Write list to StatefulBeanToCsv object
			beanWriter.write(beanList);

		} catch (FileNotFoundException e) {
			throw new Exception("File Not found");
		} catch (IOException e) {
			throw new Exception("The File write unsuccessfull");
		} catch (CsvDataTypeMismatchException e) {
			throw new Exception("CSV data format mismatch");
		} catch (CsvRequiredFieldEmptyException e) {
			throw new Exception("CSV data required field empty");
		}
	}
	
	//method to get the file path from the config file values
	public static String filePathGenrator (String property, Properties pathConfigs) throws Exception {
		
		//returns the path if the name of the property is correct and present
		try {
		return pathConfigs.getProperty(property);
		}
		
		//throws error if the property name is wrong
		catch(Exception e) {
			throw new Exception("Property name entered wrong");
		}
		
	}
	
	//resource initializer checks for the resource file's error and exceptions in it, if present empty etc
	public static Properties resourceIntializer() throws Exception {
		
		//returns the configuration from the config files
		try {
			return ReadProperties.getFile();
			
		}
		//throws error if both the user defined as well as default configs are missing
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}
				
	}
	
	
	//gets the duplicate records from the input file stores them and removes them with lateset values
	public static List<Employee> inputDuplicateRecorder(List<Employee> rawData) {
		//hashset fot unique values
		LinkedHashSet<Employee> inputDataSet = new LinkedHashSet<>();
		
		//lop to enter the valus from the list into set and get the correct duplicate counter
		for(Employee emp : rawData) {
			if(!inputDataSet.add(emp)) {
				duplicateCount++;
				valuesUpdated.add(emp);
			}
		}
		
		//returns the value in arraylist
		return new ArrayList<Employee> (inputDataSet);
		
	}
	
	//main function
	public static void main(String[] args) {
		
		//try values stops functioning if previous linked values is missing with error message
		try {
		
		//intializes the resources (config) file	
		Properties pathConfigFile = resourceIntializer();
		
				// reading files and storing them in a list of bean
		List<Employee> inputData = csvReader(
				filePathGenrator("inputataFile", pathConfigFile));
		
		//getting duplicated from input records and removing them
		inputData = inputDuplicateRecorder(inputData);
		
		// reading files and storing them in a list of bean
		List<Employee> targetEmployeeData = csvReader(
				filePathGenrator("inputDatabase", pathConfigFile));
				
		// mapping bean objects to map
		LinkedHashMap<String, Employee> targetEmployeeDataMap = beanMapper(targetEmployeeData);
		LinkedHashMap<String, Employee> updatedEmployeeData = newValuesMapper(targetEmployeeDataMap, inputData);

		// map values to bean object list
		List<Employee> employeeRecordBean = new ArrayList<>(updatedEmployeeData.values());
		
		// writing final data on database file
		String employeeRecordFile = filePathGenrator("targetDataFile", pathConfigFile);
		recordWriter(employeeRecordFile, employeeRecordBean);

		//writing final data on updated values
		String valueUpdatedRecordFile = filePathGenrator("updatedDataRecordFile", pathConfigFile);
		recordWriter(valueUpdatedRecordFile, (new ArrayList<Employee>(valuesUpdated)));

		//logging required values
		log.info("Total Duplicate values overwritten : " + duplicateCount);
		log.info("Total new Records added : " + (inputData.size() - duplicateCount));
		}
		
		//catching exceptions
		catch(Exception e) {
			log.fatal(e.getMessage());
		}

	}
	
}
		