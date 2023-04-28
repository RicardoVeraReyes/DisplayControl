package cl.samtech.sellos.display;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerUDP extends Thread {
	private static final Logger logger = LogManager.getLogger(ServerUDP.class.getName());
	
	private int port;
	private int size;
	private DBConn dbConn;
	private DisplayControl displayControl;

	
	public ServerUDP(int port, int msgSize, DBConn dbConn, DisplayControl displayControl) {
		this.port = port;
		this.size = msgSize;
		this.dbConn = dbConn;
		this.displayControl = displayControl;
	}
	

	@Override
	public void run() {

			byte msg[] = new byte[size];

			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket(port);
			} catch (SocketException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			logger.info("Servidor UDP activo ...");

			while (true) {
				DatagramPacket dataIn = new DatagramPacket(new byte[size], size);

				try {
					socket.receive(dataIn);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String msgIn = new String(dataIn.getData()).trim();
			
				
				String rfid = null;
				String ppu = null;
		        try {
		        	//{"rfid":"<valrfid>", "ppu": "<valppu>"}
		        	//{"rfid":"12345678", "ppu": "hp6911"}
		        	
		        	ObjectMapper objectMapper = new ObjectMapper();
		        	ControlMsg controlMsg = objectMapper.readValue(msgIn, ControlMsg.class);
		        	rfid = controlMsg.getRfid();
		        	ppu = controlMsg.getPpu();
		                
		        	logger.info("rfid:" + rfid);
		        	logger.info("ppu:" + ppu);
		                
		        	String data[] = null;
		        	if(rfid != null) {
		        		logger.debug("PPU es NULL");
		        		if(rfid != null)
		        			data = dbConn.queryPermisoRfid(rfid);
		        	}
		        	else {    
		        		if(ppu != null) {
		        			logger.debug("RFID es NULL");
		        			if(ppu != null)
		        				data = dbConn.queryPermisoPpu(ppu);
		        		}
		        	}
		        	
		        	if(data != null) {
		        		int estado = DisplayControl.ESPERA;
		        		
		        		logger.debug("permiso:" + data[0]); 
 		        		logger.debug("restriccion:" + data[1]);
		        		logger.debug("patente:" + data[2]);
		        		
		        		if(data[0] == null) { // no esta en la DB
		        			displayControl.setValues(DisplayControl.NEGADO_NO_REGISTRO, data[2]);
		        			logger.debug("No registrado en la DB");
		        		}
		        		
		        		else { // está en la DB
		        		
		        			if(data[0].equals("true")) {
		        				if(data[1].equals("false"))
		        					estado = DisplayControl.AUTORIZADO;
		        				else { // consulta restricción horaria
		        					if(dbConn.restriccionHoraria(data[2]))
		        						estado = DisplayControl.NEGADO_HORARIO;
		        					else
		        						estado = DisplayControl.AUTORIZADO;	
		        				
		        				}
		        				displayControl.setValues(estado, data[2]);
		        			}
		        		}	
		        	}
		        
		        	else
		        		logger.debug("data es null");
		        	
		        }
		        catch (JsonMappingException e) {
		                e.printStackTrace();
		        }
		        catch (JsonProcessingException e) {
		                e.printStackTrace();
		        }
			}	
		} 
}
