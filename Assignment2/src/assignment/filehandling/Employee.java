package assignment.filehandling;

import java.util.Objects;
import com.opencsv.bean.CsvBindByName;

/**
 * This class is purposed to be a bean class
 * @author archit.sharma
 *
 */

public class Employee {
	
	//annotators 
	@CsvBindByName()
	private String ID;
	@CsvBindByName()
	private String Name;
	@CsvBindByName()
	private String Age;
	@CsvBindByName()
	private String Gender;
	@CsvBindByName()
	private String Earning;
	@CsvBindByName()
	private String Expenditure;
	
	//setters and getters
	public String getID() {
		return ID;
	}
	public void setID(String id) {
		ID = id;
	}
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	
	public String getAge() {
		return Age;
	}
	public void setAge(String age) {
		Age = age;
	}	
	public String getGender() {
		return Gender;
	}
	public void setGender(String gender) {
		Gender = gender;
	}
	
	public String getEaring() {
		return Earning;
	}
	public void setEarning(String earning) {
		Earning = earning;
	}
	
	public String getExpenditure() {
		return Expenditure;
	}
	public void setExpenditure(String expenditure) {
		Expenditure = expenditure;
	}
	
	//genrative tostring
	@Override
	public String toString() {
		return "Employee [ID=" + ID + ", Name=" + Name + ", Age=" + Age + ", Gender=" + Gender + ", Earning=" + Earning
				+ ", Expenditure=" + Expenditure + "]";
	}
	
	//genrative hashcode() and hashequals()
	@Override
	public int hashCode() {
		return Objects.hash(Age, Earning, Expenditure, Gender, ID, Name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		return Objects.equals(Age, other.Age) && Objects.equals(Earning, other.Earning)
				&& Objects.equals(Expenditure, other.Expenditure) && Objects.equals(Gender, other.Gender)
				&& Objects.equals(ID, other.ID) && Objects.equals(Name, other.Name);
	}
}
