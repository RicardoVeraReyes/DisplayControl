package cl.samtech.sellos.display;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class Main {
	private static final Logger logger = LogManager.getLogger(Main.class.getName());

	private static final int MAX_UDP_MSG = 50;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Inicio Display Control");
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("etc/displayControl.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		String dBHost = properties.getProperty("DBHost");
		String dBName = properties.getProperty("DBName");
		String dBUser = properties.getProperty("DBUser");
		String dBPass = properties.getProperty("DBPass");
		String sPort = properties.getProperty("port");
		
		String logLevel = properties.getProperty("logLevel");
		
		
		Level level = Level.INFO;
		if(logLevel.equalsIgnoreCase("debug"))
			level = Level.DEBUG;
		
		if(logLevel.equalsIgnoreCase("warning"))
			level = Level.WARN;	
		
		if(logLevel.equalsIgnoreCase("error"))
			level = Level.ERROR;
				
		Configurator.setRootLevel(level);
		
		
		int port = 0;
		try {
			port = Integer.parseInt(sPort);
		}
		catch(Exception e) {}
		
		boolean out = false;
		DBConn dBConn = new DBConn(dBHost, dBUser, dBPass, dBName);
		out = dBConn.connect();
		
		if(out)
			logger.info("Conectado a DB");
		else
			logger.info("Falla DB");
		
		
		DisplayControl displayControl = new DisplayControl();
		displayControl.start();

		ServerUDP serverUDP = new ServerUDP(port, MAX_UDP_MSG, dBConn, displayControl);
		serverUDP.start();
		
	}

}
