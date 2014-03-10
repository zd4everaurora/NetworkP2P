package networkP2P.mesHandler;

import networkP2P.utils.GeneralUtils;

/**
 * This class returns corresponding object for the message to handle.
 * 
 * @author DAZ ZIJ YUS
 *
 */
public class MesHandlerFactory {
	
	public static MesHandlerInterface getMesHandler(int mesType) throws Exception{
		if(mesType == GeneralUtils.BITFIELD){
			return new BitfieldMesHandler();
		}
		else if(mesType == GeneralUtils.CHOKE){
			return new ChokeMesHandler();
		}
		else if(mesType == GeneralUtils.HAVE){
			return new HaveMesHandler();
		}
		else if(mesType == GeneralUtils.INTERESTED){
			return new InterestedMesHandler();
		}
		else if(mesType == GeneralUtils.NOT_INTERESTED){
			return new NotInterestedMesHandler();
		}
		else if(mesType == GeneralUtils.PIECE){
			return new PieceMesHandler();
		}
		else if(mesType == GeneralUtils.REQUEST){
			return new RequestMesHandler();
		}
		else if(mesType == GeneralUtils.UNCHOKE){
			return new UnChokeMesHandler();
		}
		else if(mesType == GeneralUtils.HANDSHAKE){
			return new HandShakeMesHandler();
		}
		else {
			System.out.println("Message type not found");
			throw new Exception("Message type not found");
		}
	}
}
