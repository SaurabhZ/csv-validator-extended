package deloitte.szunke;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.gov.nationalarchives.csv.validator.api.java.CsvValidator;
import uk.gov.nationalarchives.csv.validator.api.java.FailMessage;
import uk.gov.nationalarchives.csv.validator.api.java.Substitution;
import au.com.bytecode.opencsv.CSVReader;

public class CSVValidator {
	
    private static List<String> errorMessages = new ArrayList<String>();
    private static List<String> columnNames = new ArrayList<String>();
    private static String COLON = ":";
	
	public List<String> validate(String csvFilename, String csvSchemaFilename, 
			boolean failFast, List<Substitution> pathSubstitutions, 
			Boolean enforceCaseSensitivePathChecks){
		errorMessages.clear();
		try {
			//Read and save the file headers
			if(columnNames.isEmpty()){
		        File schema = new File(csvSchemaFilename);
				BufferedReader schemaReader = new BufferedReader(new FileReader(schema));
		        String schemaVersion = schemaReader.readLine();
		        String totalColumns = schemaReader.readLine();
		    	String temp = schemaReader.readLine();
		        while(temp!=null){
		        	String result[] = temp.split(COLON);
		        	columnNames.add(result[0]);
		        	temp = schemaReader.readLine();
		        }
		        schemaReader.close();
			}
	        CSVReader reader = new CSVReader(new FileReader(csvFilename));
            String headers[] = reader.readNext();
            if(!Arrays.equals(headers,columnNames.toArray(new String[0]))){
            	errorMessages.add("Columns headers don't match. Please check the CSV file.");
            }
            reader.close();
            
            if(errorMessages.isEmpty()) {
	            //Validate the file data	
            	List<FailMessage> messages = CsvValidator.validate(
				csvFilename,
				csvSchemaFilename,
				false,
				pathSubstitutions,
				true);
            	for(FailMessage message: messages){
            		errorMessages.add(message.getMessage());
            	}
            }
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        
        return errorMessages;
	}
}
