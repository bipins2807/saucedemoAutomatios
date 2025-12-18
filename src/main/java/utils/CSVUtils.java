package utils;

import org.apache.commons.csv.*;
import java.io.FileReader;
import java.util.*;

public class CSVUtils {
		    public static List<String[]> readCSV(String filePath) {
	        List<String[]> data = new ArrayList<>();
	        try (FileReader reader = new FileReader(filePath);
	             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
	            for (CSVRecord record : parser) {
	                data.add(new String[]{
	                    record.get("Scenario"),
	                    record.get("Username"),
	                    record.get("Password"),
	                    record.get("FirstName"),
	                    record.get("LastName"),
	                    record.get("PostalCode"),
	                    record.get("Product")
	                });
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return data;
	    }
	

}
