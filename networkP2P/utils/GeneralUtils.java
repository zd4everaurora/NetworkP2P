package networkP2P.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import networkP2P.PeerInfo;


/**
 * This class is a general utilization class, which contains public variables and methods.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class GeneralUtils {
	
	private static GeneralUtils instance = null; 
	
	public static final String HANDSHAKE_HEADER = "CEN5501C2008SPRING";
	public static final byte[] ZERO_BITS = new byte[]{0,0,0,0,0,0,0,0,0,0};
	public int myPeerID = 0;

	
	public static final byte CHOKE = 0;			// No payload
	public static final byte UNCHOKE = 1;		// No payload
	public static final byte INTERESTED = 2;		// No payload
	public static final byte NOT_INTERESTED = 3; // No payload
	public static final byte HAVE = 4;			// payload 4 bytes
	public static final byte BITFIELD = 5;		// payload size of piece number bytes
	public static final byte REQUEST = 6;		// payload 4 bytes
	public static final byte PIECE = 7;			// payload 4 bytes plus one piece size
	public static final byte HANDSHAKE = 8;		// no payload
	
	private static final String CONFIG_FILE = "Common.cfg";
	private static final String PEER_FILE = "PeerInfo.cfg";
	
	private static final String SNUMBER_OF_PREFERRED_NEIGHBORS = "NumberOfPreferredNeighbors";
	private static final String SUNCHOKING_INTERVAL = "UnchokingInterval";
	private static final String SOPTIMISTIC_UNCHOKING_INTERVAL = "OptimisticUnchokingInterval";
	private static final String SFILE_NAME = "FileName";
	private static final String SFILE_SIZE = "FileSize";
	private static final String SPIECE_SIZE = "PieceSize";
	private CommonProperties commonProperties = null;
//	private ArrayList <PeerInfo> peerArr = null;
	public Hashtable<Integer, PeerInfo> peerIDInfo = null;
	
	/**
	 * The constructor will set the configuration properties from the files.
	 * @throws IOException
	 */
	private GeneralUtils() {
		initialCommonProperties();
//		peerArr = readPeerFile();
		peerIDInfo = new Hashtable<Integer, PeerInfo>();
	}
	
	/**
	 * Get an instance from the class
	 * @return
	 * @throws IOException
	 */
	public static GeneralUtils getInstance() {
		if(instance == null)
			instance = new GeneralUtils();
		return instance;
	}
	
	/**
	 * This method is to get the common properties from Common.cfg
	 * @return
	 * @throws IOException
	 */
	public void initialCommonProperties() {
		Properties pro;
		CommonProperties aCommonProperties = null;
		try {
			pro = new Properties();
			pro.load(new FileInputStream(CONFIG_FILE));
			aCommonProperties = new CommonProperties();
			aCommonProperties.setFileName(pro.getProperty(SFILE_NAME));
			aCommonProperties.setFileSize(Integer.valueOf(pro.getProperty(SFILE_SIZE)));
			aCommonProperties.setNumberOfPreferredNeighbors(Integer.valueOf(pro.getProperty(SNUMBER_OF_PREFERRED_NEIGHBORS)));
			aCommonProperties.setOptimisticUnchokingInterval(Integer.valueOf(pro.getProperty(SOPTIMISTIC_UNCHOKING_INTERVAL)));
			aCommonProperties.setPieceSize(Integer.valueOf(pro.getProperty(SPIECE_SIZE)));
			aCommonProperties.setUnchokingInterval(Integer.valueOf(pro.getProperty(SUNCHOKING_INTERVAL)));
		} catch (FileNotFoundException fnfe){
//			throw fnfe;
			System.out.println("config file not found.");
			fnfe.printStackTrace();
		} catch (IOException ioe){
//			throw ioe;
			System.out.println("error reading config file.");
			ioe.printStackTrace();
		}
		commonProperties = aCommonProperties;
	}
	
	/**
	 * This method is to get the peers information from PeerInfo.cfg
	 * @return
	 * @throws IOException
	 */
	public ArrayList <PeerInfo> readPeerFile() throws IOException{
		File file = null;
	    BufferedReader reader = null;
	    ArrayList <PeerInfo> peerArr = null;
	    
	    try {
	    	file = new File(GeneralUtils.PEER_FILE);
	        reader = new BufferedReader(new FileReader(file));
	        peerArr = new ArrayList <PeerInfo>();
	        String tempString = null;
//	        int line = 1;
//	        tempString = reader.readLine();
	        while ((tempString = reader.readLine()) != null && !"".equals(tempString.trim())) {
//	            System.out.println("line " + line + ": " + tempString);
	            String temp[] = tempString.split(" ");
	            PeerInfo aPeerInfo = new PeerInfo();
	            aPeerInfo.setPeerID(Integer.valueOf(temp[0]));
	            aPeerInfo.setHostName(temp[1]);
	            aPeerInfo.setPort(Integer.valueOf(temp[2]));
	            aPeerInfo.setHasFile(Integer.valueOf(temp[3]));
	            peerArr.add(aPeerInfo);
	            getPeerIDInfo().put(Integer.valueOf(temp[0]), aPeerInfo);
//	            line++;
	        }
	        reader.close();
	    } catch (IOException e) {
//	        e.printStackTrace();
	    } finally {
	        if (reader != null) {
	            try {
	                reader.close();
	            } catch (IOException e1) {
	            }
	        }
	    }
        return peerArr;
	}
	
	
	
	 public static String listToString(List<String> stringList){
         if (stringList==null) {
             return null;
         }
         StringBuilder res = new StringBuilder();
         boolean flag = false;
         for (String s : stringList) {
             if (flag) {
            	 res.append(", ");
             }else {
                 flag=true;
             }
             res.append(s);
         }
         return res.toString();
     }
	
	public static void setMyPeerID(int aPeerID){
		getInstance().myPeerID = aPeerID;
	}
	
	public static int getMyPeerID(){
		return getInstance().myPeerID;
	}

	public static Hashtable<Integer, PeerInfo> getPeerIDInfo() {
		return getInstance().peerIDInfo;
	}

	public static void setPeerIDInfo(Hashtable<Integer, PeerInfo> peerIDInfo) {
		getInstance().peerIDInfo = peerIDInfo;
	}
	
	public static CommonProperties getCommonProperties() {
		return getInstance().commonProperties;
	}

	public static void setCommonProperties(CommonProperties commonProperties) {
		getInstance().commonProperties = commonProperties;
	}

}
