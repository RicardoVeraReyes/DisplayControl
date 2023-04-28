package cl.samtech.sellos.display;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DBConn {
	private static final Logger logger = LogManager.getLogger(DBConn.class);
	
	private static final String TAB_SELLO = "sello_control";	
	private static final String PATENTE = "patente";	
	private static final String ID_TAG = "id_tag";
	private static final String PERMISO = "permiso";
	private static final String RESTRICCION = "restriccion";
	
	private static final String TAB_RESTRICCION = "restriccion";
	private static final String INICIO = "inicio";
	private static final String FIN = "fin";
	
	private int port = 0;
	private String host;
	private String user;
	private String pass;
	private String databaseName;
	
	
	private Connection conn;
	
	public DBConn(int port, String host, String user, String pass, String databaseName) {
		this.port = port;
		this.host = host;
		this.user = user;
		this.pass = pass;
		this.databaseName = databaseName;
	
	}
	public DBConn(String host, String user, String pass, String databaseName) {
		this.host = host;
		this.user = user;
		this.pass = pass;
		this.databaseName = databaseName;
	}
	
	public boolean connect() {
		boolean ret = false;
		String connString = null;
		 
		if(port == 0)
			port = 3306;
		
			connString = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		
		try {
			logger.info("DB String:" + connString);
			conn = DriverManager.getConnection(connString, user, pass);
			ret = true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] queryPermisoPpu(String ppu) {
		String[] out = new String[3];
		
		String sql = "SELECT " + PERMISO + "," + RESTRICCION + " FROM " + TAB_SELLO + " WHERE "
				+ PATENTE + " ='" + ppu + "'";
		
		logger.debug("queryPermisoPpu(" + ppu + ")");
		
		Statement stmt = null;
		ResultSet rs =  null;
		try {
			if(conn.isClosed())
				connect();
			
			stmt = conn.createStatement();
			rs =  stmt.executeQuery(sql);
			
			rs.next();
			
			out[0] = "" + rs.getBoolean(PERMISO);
			out[1] = "" + rs.getBoolean(RESTRICCION);
			out[2] = ppu;
	
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			out[2] = ppu;
			try {
				rs.close();
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
			try {
				stmt.close();
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
		}
		return out;
	}
	
	public String[] queryPermisoRfid(String rfid) {
		String out[] = new String[3];
		
		String sql = "SELECT " + PERMISO + "," + RESTRICCION + "," + PATENTE + " FROM " + TAB_SELLO + " WHERE "
				+ ID_TAG + " = " + rfid;
		
		logger.debug("queryPermisoRfid(" + rfid + ")");
		
		Statement stmt = null;
		ResultSet rs =  null;
		try {
			if(conn.isClosed()) 
				connect();
			
			stmt = conn.createStatement();
			rs =  stmt.executeQuery(sql);
			
			rs.next();
			
			out[0] = "" + rs.getBoolean(PERMISO);
			out[1] = "" + rs.getBoolean(RESTRICCION);
			out[2] = rs.getString(PATENTE);
	
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			try {
				rs.close();
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
			try {
				stmt.close();
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
		}
		return out;
	}
	
	
	public boolean restriccionHoraria(String patente) {
		boolean out = false;
		
		String sql = "SELECT " + PATENTE + " FROM " + TAB_RESTRICCION + 
							" WHERE " + PATENTE + " = '" + patente + 
							"' AND (TIME(NOW()) BETWEEN " + INICIO + " AND " + FIN +")";
		
		logger.debug("restriccionHoraria(" + patente + ")");
		
		Statement stmt = null;
		ResultSet rs =  null;
		try {
			if(conn.isClosed())
				connect();
			
			stmt = conn.createStatement();
			rs =  stmt.executeQuery(sql);
			
			rs.next();
			
			if(rs.getString(PATENTE) != null)
				out = true;
	
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			try {
				rs.close();
			}
			catch(Exception e1) {}
			try {
				stmt.close();
			}
			catch(Exception e1) {}
		}
		
		return out;
	}
}
