package networkP2P.utils;

/**
 * This class stores the structure of the common properties file, this is considered to be
 * removed from second half of this semester.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class CommonProperties {
	
	public int NUMBER_OF_PREFERRED_NEIGHBORS = 0;
	public int UNCHOKING_INTERVAL = 0;
	public int OPTIMISTIC_UNCHOKING_INTERVAL = 0;
	public String FILE_NAME = "";
	public int FILE_SIZE = 0;
	public int PIECE_SIZE = 0;
	
	public int getNumberOfPreferredNeighbors() {
		return NUMBER_OF_PREFERRED_NEIGHBORS;
	}
	
	public void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		NUMBER_OF_PREFERRED_NEIGHBORS = numberOfPreferredNeighbors;
	}
	
	public int getUnchokingInterval() {
		return UNCHOKING_INTERVAL;
	}
	
	public void setUnchokingInterval(int unchokingInterval) {
		UNCHOKING_INTERVAL = unchokingInterval;
	}
	
	public int getOptimisticUnchokingInterval() {
		return OPTIMISTIC_UNCHOKING_INTERVAL;
	}
	
	public void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		OPTIMISTIC_UNCHOKING_INTERVAL = optimisticUnchokingInterval;
	}
	
	public String getFileName() {
		return FILE_NAME;
	}
	
	public void setFileName(String fileName) {
		FILE_NAME = fileName;
	}
	
	public int getFileSize() {
		return FILE_SIZE;
	}
	
	public void setFileSize(int fileSize) {
		FILE_SIZE = fileSize;
	}
	
	public int getPieceSize() {
		return PIECE_SIZE;
	}
	
	public void setPieceSize(int pieceSize) {
		PIECE_SIZE = pieceSize;
	}
	
}
