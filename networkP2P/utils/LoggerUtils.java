package networkP2P.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class handles the log information when some logs need to be recorded.
 * This needs to be handled in the second half of this semester.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class LoggerUtils{
	
	public String LOGGERNAME;
	private static Logger logger = Logger.getLogger(LoggerUtils.class.getName());
	
	
	public LoggerUtils(int myPeerID){
		LOGGERNAME = "log_peer_" + String.valueOf(myPeerID) + ".log";
		 
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler("./GCCQ/" + LOGGERNAME);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.INFO);
		    logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Logger getLogger() {
		return logger;
	}


}
