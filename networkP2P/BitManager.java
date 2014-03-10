package networkP2P;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import networkP2P.utils.ConversionUtils;
import networkP2P.utils.GeneralUtils;

/**
 * This class manages all the pieces information for the peer's neighbors, whether contains or 
 * not. It will update the piece on receiving the messages from its neighbors, 
 * i.e. bitfields, have.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class BitManager {
	
	public static BitManager bitMan;
	public int FileSize = 0;
	public int pieceSize = 0;
	public static int pieceNum = 0;
	public int pieceCount = 0;
	public int myPeerID = 0;
	public int pieceBufferSize = 100;
	public int currBufferSize = 0;
	public byte[] myBitFields;
	public Hashtable <Integer, byte[]> peerNeighborBitsInfo;
	private RandomAccessFile myFile;
	public boolean isCompleted = false;
	
	/**
	 * The constructor of the class. It will store the file and piece properties, and 
	 * put the record into the hashtable.
	 * @param peerID
	 * @param hasFile
	 * @throws IOException
	 */
	private BitManager(int hasFile) throws IOException{
		peerNeighborBitsInfo = new Hashtable <Integer, byte[]> ();
		FileSize = GeneralUtils.getInstance().getCommonProperties().FILE_SIZE;
		pieceSize = GeneralUtils.getInstance().getCommonProperties().PIECE_SIZE;
		myPeerID = GeneralUtils.getMyPeerID();
		pieceCount = (FileSize%pieceSize == 0) ? (FileSize/pieceSize) : (FileSize/pieceSize + 1);
		pieceNum = (pieceCount%8 == 0) ? (pieceCount/8): (pieceCount/8 + 1);
		myBitFields = new byte[pieceNum];

		new File("./GCCQ/peer_" + myPeerID).mkdirs();
		if(hasFile == 1){
			System.out.println("mark as complete");
			isCompleted = true;
			markAsComplete(myPeerID);
			myFile = new RandomAccessFile("./GCCQ/peer_" + myPeerID + "/" + GeneralUtils.getCommonProperties().FILE_NAME,"r");
		}
		else {
			myFile = new RandomAccessFile("./GCCQ/peer_" + myPeerID + "/" + GeneralUtils.getCommonProperties().FILE_NAME,"rwd");
			myFile.setLength(pieceCount*pieceSize);
		}

	}
	
	/**
	 * This is directly obtain the existing instance of the class.
	 * Suppose the instance is already set in this case, since the instance is initialized at
	 * the beginning.
	 * @return An instance of BitManager
	 * @throws IOException
	 */
	public static BitManager getInstance() throws IOException {
		return bitMan;
	}
	
	/**
	 * This is to return an instance of bitManager. If it is null, it will create a new instance.
	 * We only need one instance like a globe value.
	 * @param peerID
	 * @param hasFile
	 * @return An instance of BitManager
	 * @throws IOException
	 */
	public static BitManager getInstance(int hasFile) throws IOException {
		if (bitMan == null)
			bitMan = new BitManager(hasFile);
		return bitMan;
	}
	
	/**
	 * This method is to get the file size, which is used in the whole P2P process.
	 * Where is the file location and name?
	 * @return The file size
	 */
	public int getFileSize(){
		return 0;
	}
	
	public boolean isInterested (byte[] bitfieldMesPayLoad){
		if (bitfieldMesPayLoad.length == myBitFields.length) {
			for (int i = 0; i < bitfieldMesPayLoad.length; i++) {
				// use == for byte, basic type
				if (((bitfieldMesPayLoad[i] ^ myBitFields[i]) & bitfieldMesPayLoad[i]) != 0)
					return true;
			}
		}
		return false;
/*			int mes = GeneralUtils.bytesToInt(mesPayLoad);
			int bits = GeneralUtils.bytesToInt(myBitFields);
			if(((mes ^ bits) & mes) != 0){
				// it is interested
				return true;
			}*/

	}
	
	/**
	 * This method returns true if our peer does not contain the coming piece or any other piece
	 * from its neighbor. It returns false only if our peer has all pieces in its neighbor.
	 * @param peerID
	 * @param haveMes	the index of the piece
	 * @return
	 */
	public boolean isInterested (int peerID, byte[] haveMesPayload){
		byte[] peerbytes = peerNeighborBitsInfo.get(peerID);
/*		int peerBits = GeneralUtils.bytesToInt(peerbytes) | (1 << GeneralUtils.bytesToInt(haveMes));
		return isInterested(GeneralUtils.intToBytes(peerBits));*/
//		int peerPosition = GeneralUtils.bytesToInt(haveMes)/8;
//		int peerOffSet = GeneralUtils.bytesToInt(haveMes)%8;
//		if((myBitFields[peerPosition] & (1 << (7 - peerOffSet))) == 0){
//			return true;
//		}
		return isInterested(peerbytes);
		
	}
	
	/**
	 * This method is to determine if our peer contains the coming piece or not.
	 * @param picecIndex
	 * @return
	 */
	public boolean isHave (byte[] pieceIndex){
/*		int piec = GeneralUtils.bytesToInt(picecIndex);
		int bits = GeneralUtils.bytesToInt(myBitFields);
		if(((1 << piec) | bits) == bits){
			return true;
		}*/
		try {
			int index = ConversionUtils.BytesToInt(pieceIndex);
			int piecePosition = index/8;
			int pieceOffSet = index%8;
			if((1 << (7 - pieceOffSet) & myBitFields[piecePosition]) != 0){
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean hasAnyPiece(){
		for(int i = 0; i<myBitFields.length;i++) {
			if(myBitFields[i] != 0)
				return true;
		}
		return false;
	}
	
	/**
	 * When a new connection with new peer sets up, BitManager will store the piece information
	 * for the new peer. 
	 * @param peerID
	 * @param hasFile
	 */
	public void addPeerInfo (int peerID, int hasFile){
		byte[] bitF = new byte[pieceNum];
		if(hasFile == 1){
			for(int i = 0; i < pieceNum; i++){
				bitF[i] = ~0;
			}
			bitF[pieceNum-1] <<= (8-pieceCount%8);
		}
		peerNeighborBitsInfo.put(peerID, bitF);
	}
	
	/**
	 * This method is to update the peer bits index once a new index has been claimed to obtained by a peer.
	 * @param peerID
	 * @param pieceIndex
	 */
	public void updatePeerInfo (int peerID, int pieceIndex){
		int peerPosition = pieceIndex/8;
		int peerOffSet = pieceIndex%8;
		if(peerID == myPeerID){
			myBitFields[peerPosition] |= (1 << (7-peerOffSet));
		}
		else{
			byte[] peerBits;
			if ((peerBits = peerNeighborBitsInfo.get(peerID)) != null){	//warning: peerBits is reference or shadow copy?
				peerBits[peerPosition] |= (1 << (7-peerOffSet));
//				peerNeighborBitsInfo.put(peerID, peerBits);
			}
			else
				System.out.println("No peer ID found when updating its bitsInfo!");
		}
	}
	
	/**
	 * Check if the file has already been downloaded completely.
	 * @return
	 */
	public boolean isDownloadCompleted(int peerID){
		byte[] currBitFields;
		if(peerID == myPeerID){
//			currBitFields = myBitFields.clone();	// why clone here? curBitFields not modified at all
			currBitFields = myBitFields;
		}
		else{
			currBitFields = peerNeighborBitsInfo.get(peerID);
		}
//		byte[] temp = new byte[pieceNum];
		for (int i = 0; i < pieceNum - 1; i++){
			if(currBitFields[i] != ~0){
				return false;
			}
		}
		if(pieceCount%8 != 0 && currBitFields[pieceNum-1] != (byte) (~0 << (8-pieceCount%8))){
			return false;
		}
		isCompleted = true;
		return true;
	}
	
	/**
	 * Check if the file has already been downloaded completely.
	 * @return
	 */
	public boolean isAllDownloadCompleted(){
		for (Iterator<?> it = peerNeighborBitsInfo.keySet().iterator(); it.hasNext();) {
			if(!isDownloadCompleted((Integer) it.next())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method is to store the actual piece of file
	 * @param index
	 * @param actualPiece
	 */
	public boolean setActualPiece(int pieceIndex, byte[] actualPiece){
		int peerPosition = pieceIndex/8;
		int peerOffSet = pieceIndex%8;
		if ((myBitFields[peerPosition] & (0x80 >> peerOffSet)) != 0) {
			return false; //already have
		}
		else {
			myBitFields[peerPosition] |= (0x80 >> peerOffSet);
			try {
				myFile.seek(pieceIndex*pieceSize);
				myFile.write(actualPiece);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

	}
	
	/**
	 * This method writes the current file to the disk.
	 * @throws IOException 
	 */
	public void writeToDisk() throws IOException{
		if(myFile!=null){
			myFile.setLength(FileSize);	//trim the padding of the last piece
//			myFile.close();
		}
	}
	
	/**
	 * This method reads the current file to the memory
	 * @throws IOException
	 */
	public void readFromDisk() throws IOException{
//		FileReader fr = null;
		FileInputStream fs = null;
//		BufferedReader br = null;
//		DataInputStream dos;
		try {
			new File("./GCCQ/peer_" + myPeerID).mkdirs();
			if(isDownloadCompleted(myPeerID)){
//				fr = new FileReader(GeneralUtils.getCommonProperties().FILE_NAME);
				fs = new FileInputStream("./GCCQ/peer_" + myPeerID + "/" + GeneralUtils.getCommonProperties().FILE_NAME);
			}
			else{
//				fr = new FileReader(GeneralUtils.getCommonProperties().FILE_NAME.concat("tmp"));
				fs = new FileInputStream("./GCCQ/peer_" + myPeerID + "/" + GeneralUtils.getCommonProperties().FILE_NAME.concat("tmp"));
			}
//			br = new BufferedReader(fr);
			int index = 0;
			while(true){
				byte[] actualPiece = new byte[pieceSize];
				int actualSize;
				if((actualSize = fs.read(actualPiece)) < pieceSize){
					if (actualSize > 0) {
						byte[] lastPiece = new byte[actualSize];
						System.arraycopy(actualPiece, 0, lastPiece, 0, actualSize);
						setActualPiece(index, lastPiece);
					}
					break;
				}
				setActualPiece(index, actualPiece);
				index++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
//			br.close();
//			fr.close();
			fs.close();
		}
	}
	
	/**
	 * This method randomly select the piece from the input peer that our peer does not have.
	 * If there is no piece we need from the input peer, we will send "Not interested" message and return null.
	 * @param aPeerInfo
	 * @return The index of that piece
	 * @throws IOException
	 */
	public byte[] getInterestedPiece(PeerInfo aPeerInfo) throws IOException{
		//difference list
		ArrayList<Integer> diffList = new ArrayList<Integer>();
		//populate diffList
		for (int i = 0; i < myBitFields.length; i++)
		{
			if ((~myBitFields[i] & peerNeighborBitsInfo.get(aPeerInfo.peerID)[i]) != 0)
				diffList.add(i);
		}
		if (diffList.size() <= 0)
			return null;
		Random random = new Random();
		int pieceNum = diffList.get(random.nextInt(diffList.size()));
		int pieceOffset = 0;
		int interestedBitFlag = ~myBitFields[pieceNum] & peerNeighborBitsInfo.get(aPeerInfo.peerID)[pieceNum];
		int mask = 0x80;
		for (pieceOffset = 0; pieceOffset < 8; pieceOffset++,mask>>=1)
		{
			if ((interestedBitFlag & mask) != 0)
			{
				break;
			}
		}
		int pieceIndex = pieceNum * 8 + pieceOffset;
		return ConversionUtils.IntToBytes(pieceIndex);
	}
	
/*	*//**
	 * For a given integer, this method calculate how many 1s does it have when converting to binary.
	 * @param integer
	 * @return
	 *//*
	public int calculate1(int integer){
		int count = 0;  
	    while(integer != 0){  
	        count++;  
	        integer = integer & (integer - 1);  
	    }  
	    return count;
	}*/
	
	/**
	 * This method select the specified actual piece from our peer.
	 * @param pieceIndex
	 * @return The actual content of that piece
	 * @throws IOException
	 */
	public byte[] getSpecifiedPiece(byte[] pieceIndex) throws IOException{
		if(isHave(pieceIndex)) {
			myFile.seek(pieceSize * ConversionUtils.BytesToInt(pieceIndex));
			byte[] specifiedPiece = new byte[pieceSize];
			myFile.read(specifiedPiece);
			return specifiedPiece;
		}
		return null;
	}
	
	public void markAsComplete(int peerID) {
		byte[] bitField = new byte[pieceNum];
		for (int i = 0; i < pieceNum; i++){
			bitField[i] = ~0;
		}
		if(pieceCount%8 != 0)
			bitField[pieceNum-1] <<= (8 - pieceCount%8); //pad right with 0
		if((peerID == myPeerID))
			myBitFields = bitField;
		else
			peerNeighborBitsInfo.put(peerID, bitField);
	}
	
	public void setNeighborBitFields(int peerID, byte[] bitFields){
		byte[] bf = peerNeighborBitsInfo.get(peerID);
		for (int i = 0; i < pieceNum; i++){
			bf[i] |= bitFields[i];
		}
	}
	
	public void close(){
		if(myFile != null) {
			try {
				myFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
