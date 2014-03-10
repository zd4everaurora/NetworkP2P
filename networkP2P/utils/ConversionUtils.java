package networkP2P.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ConversionUtils {
	public static byte[] IntToBytes(int intval) throws IOException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		new DataOutputStream(os).writeInt(intval);
		return os.toByteArray();
	}
	
	// Join two byte array into a new byte array
	public static byte[] JoinBytes(byte[] left, byte[] right) throws IOException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream(); 
		os.write(left);
		os.write(right);
		return os.toByteArray();
	}
	
	public static String BytesToString(byte[] bytesval) throws IOException
	{
		return new String(bytesval,"UTF8");
	}
	
	public static int BytesToInt(byte[] bytesval) throws IOException {
		return new DataInputStream(new ByteArrayInputStream(bytesval)).readInt();
	}
	
	public static byte BytesToByte(byte[] bytesval) throws IOException {
		return bytesval[0];
	}
}
