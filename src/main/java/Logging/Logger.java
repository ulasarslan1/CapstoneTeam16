package Logging;

import java.time.LocalDate;

public interface Logger {
	
	//Functions
    void logInfo(String component, String message);
    void logWarning(String component, String message);
    void logError(String component, String message);
    
    boolean moveLogFile(LocalDate date, String targetDirectory);
    boolean deleteLogFile(LocalDate date);
    boolean archiveLogFile(LocalDate date);
    
    
}