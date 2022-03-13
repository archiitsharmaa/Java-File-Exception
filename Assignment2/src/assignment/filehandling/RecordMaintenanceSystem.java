package assignment.filehandling;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

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
	The following class is purposed to work as a Record Maintains System
		a) Read a file (CSV format) e.g ID,Name,Add,Gender,....etc. (ID is unique key of each record)
		b) Insert all the records in a target file (in the same format) e.g FileName :- EmployeeData.dat.
		c) Make sure it must not duplicate the records in target file, infact overwrite the record which is already present.
		d) At the end, must display how many new records have been added and how many over-written.
		e) Save previous record value, for each record over-written.
		f) And show all the newly added ids in sorted order.

 * @author archit.sharma
 *
 */

public class RecordMaintenanceSystem {

	// static variables accessed as global variables
	public static int duplicateCount = 0;
	public static List<Employee> valuesUpdated = new ArrayList<>();
	public static Logger log = LogManager.getLogger(RecordMaintenanceSystem.class.getName());

	// default config routes
	public final static String INPUT_DATA_DEFAULT_PATH = "./data/RawEmployeeRecord.csv";
	public final static String TARGET_DATABASE_DEFAULT_PATH = "./data/EmployeeData.dat";
	public final static String UPDATED_VALUES_DEFAULT_PATH = "./data/OldRecords.csv";

	// This method reads a file location and converts the data into list of
	// javaBeans
	public static List<Employee> csvFileReader(String file) throws Exception {

		if (file == null || file.isEmpty()) {
			file = INPUT_DATA_DEFAULT_PATH;
		}

		// try catch block to read a file data and add there values to java bean objects
		// using opencsv
		List<Employee> csvBeanReader = null;
		try (FileReader dataFile = new FileReader(file)) {

			// convert file to bean list
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

	// This method reads a file location and converts the data into list of
	// javaBeans
	public static List<Employee> csvDatabaseReader(String file) {

		if (file == null || file.isEmpty()) {
			file = TARGET_DATABASE_DEFAULT_PATH;
		}

		// try catch block to read a file data and add there values to java bean objects
		// using opencsv
		List<Employee> csvBeanReader = null;
		try (FileReader dataFile = new FileReader(file)) {

			// convert file to bean list
			csvBeanReader = new CsvToBeanBuilder(dataFile).withType(Employee.class).build().parse();

			// throws error when the file is empty
			if (csvBeanReader.size() == 0) {
				log.error("The database record were empty");
			}

		} catch (FileNotFoundException e) {
			log.error("Database File Not Found, new will be made");
		} catch (IOException e) {
			log.error("The Database read failed, new will be made");
		}
		return csvBeanReader;
	}

	// This method takes a list of bean objects and maps them to a hash map
	public static LinkedHashMap<String, Employee> beanMapper(List<Employee> beanList) {

		// Hash map declaration
		LinkedHashMap<String, Employee> userRecordMap = new LinkedHashMap<>();

		// checking for null values and return null values
		if (beanList == null) {
			return userRecordMap;
		}
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

			// employee object to store return types to be updated in values updated list
			Employee updatedRecords = databaseRecord.put(cr.getID(), cr);

			// adding repeated values to updated record
			if (updatedRecords != null) {
				duplicateCount++;
				valuesUpdated.add(updatedRecords);
			}
		}

		return databaseRecord;
	}

	// This method writes data on the file
	public static void recordWriter(String filepath, List<Employee> beanList, String defaultPath) throws Exception {

		if (filepath == null || filepath.isEmpty()) {
			filepath = defaultPath;
		}

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
			log.error("File Not found");
		} catch (IOException e) {
			log.error("The File write unsuccessfull");
		} catch (CsvDataTypeMismatchException e) {
			log.error("CSV data format mismatch");
		} catch (CsvRequiredFieldEmptyException e) {
			log.error("CSV data required field empty");
		}
	}

	// resource initializer checks for the resource file's error and exceptions in
	// it, if present empty etc
	public static void resourceIntializer() {
		// configures from the config files
		ReadProperties.getFile();
	}

	// gets the duplicate records from the input file stores them and removes them
	// with lateset values
	public static List<Employee> inputDuplicateRecorder(List<Employee> rawData) {

		// hashmap to detect unique values based on id
		LinkedHashMap<String, Employee> inputDataMap = new LinkedHashMap<String, Employee>();

		// lop to enter the valus from the list into set and get the correct duplicate
		// counter
		for (Employee emp : rawData) {

			// employee object to store return types to be updated in values updated list
			Employee updatedRecord = inputDataMap.put(emp.getID(), emp);

			// adding repeated values to updated record
			if (updatedRecord != null) {
				duplicateCount++;
				valuesUpdated.add(updatedRecord);
			}
		}

		// returns the value in arraylist
		return new ArrayList<Employee>(inputDataMap.values());

	}

	// main function
	public static void main(String[] args) {

		// try values stops functioning if previous linked values is missing with error
		// message
		try {

			// Initializes the resources (config) file
			resourceIntializer();

			// intializing file path from the property
			String inputdataConfigPath = ReadProperties.getResource("inputDataFile");
			String targetDatabaseConfigPath = ReadProperties.getResource("tagetDatabase");

			// reading files and storing them in a list of bean
			List<Employee> inputData = csvFileReader(inputdataConfigPath);

			// getting duplicated from input records and removing them
			inputData = inputDuplicateRecorder(inputData);

			// sort array of objects based on name
			Collections.sort(inputData, Comparator.comparing(Employee::getName));

			// reading files and storing them in a list of bean
			List<Employee> targetEmployeeData = csvDatabaseReader(targetDatabaseConfigPath);

			// mapping bean objects to map
			LinkedHashMap<String, Employee> targetEmployeeDataMap = beanMapper(targetEmployeeData);
			LinkedHashMap<String, Employee> updatedEmployeeData = newValuesMapper(targetEmployeeDataMap, inputData);

			// map values to bean object list
			List<Employee> employeeRecordBean = new ArrayList<>(updatedEmployeeData.values());

			// writing final data on database file
			recordWriter(targetDatabaseConfigPath, employeeRecordBean, TARGET_DATABASE_DEFAULT_PATH);

			// writing final data on updated values
			String valueUpdatedRecordFile = ReadProperties.getResource("updatedDataRecordFile");
			recordWriter(valueUpdatedRecordFile, valuesUpdated, UPDATED_VALUES_DEFAULT_PATH);

			// logging required values
			log.info("Total Duplicate values overwritten : " + duplicateCount);
			log.info("Total new Records added : " + (inputData.size() - duplicateCount));
		}

		// catching exceptions
		catch (Exception e) {
			log.fatal(e.getMessage());
		}

	}

}
		