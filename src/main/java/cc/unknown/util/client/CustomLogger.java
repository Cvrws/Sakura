package cc.unknown.util.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomLogger {
    public final Logger logger = LogManager.getLogger("Sakura");
    
    public void info(String message) {
    	logger.info("[INFO] " + message);
    }
    
    public void warn(String message) {
    	logger.warn("[WARN] " + message);
    }

    public void error(String message) {
    	logger.error("[ERROR] " + message);
    }
    
    public void error(String message, Exception e) {
    	logger.error("[ERROR] " + message, e);
    }
}