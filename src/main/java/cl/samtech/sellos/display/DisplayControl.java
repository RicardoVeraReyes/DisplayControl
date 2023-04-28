package cl.samtech.sellos.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class DisplayControl extends Thread {
	public static final int ESPERA = 0;
	public static final int AUTORIZADO = 1;
	public static final int NEGADO_SELLO = 2;
	public static final int NEGADO_HORARIO = 3;
	public static final int NEGADO_NO_REGISTRO = 4;
	
	private static final Logger logger = LogManager.getLogger(DisplayControl.class);
	
	private int status = ESPERA;
	private long refresh = 30000; //milisegundos para limpieza
	private long last = 0;
	private Color bgColor = Color.lightGray;
	
	
	private JFrame frame;
	
	private JTextArea taTiempo;
	private JTextArea taPatente;
	private JTextArea taEstado;
	private JTextArea taRazon;
	
	private String patente;
	
	private JPanel panelEstado;
	private JPanel panelRazon;
	private JPanel panelPatente;
	
	public DisplayControl() {
		try {
			logger.info("DisplayControl++");
		frame = new JFrame("Sistema de Control de Sellos"); 
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		frame.setLayout(new GridLayout(4, 1));
		}
		catch(Exception e) {
			//e.printStackTrace();
			logger.info("E" +  e);
		}
		/*
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		Rectangle r = frame.getBounds();
		logger.info("height:" + r.height + " width:" + r.width);
		
		float rh = (float)r.height/(float)1053;
		float rw = (float)r.width/(float)1848;
		
		
		logger.info("rh:" + rh + " rw:" + rw);
		
		int fSize1 = (int)(210 * rh);
		int fSize2 = (int)(220 * rh);
		int fSize3 = (int)(180 * rh);
		
		logger.info("fSize1:" + fSize1 + " fSize2: " + fSize2 + " fSize3:" + fSize3);
  
		try {
		taTiempo = new JTextArea();
		taTiempo.setFont(new Font("monospaced", Font.BOLD, fSize1));
		taTiempo.setRows(1);
		taTiempo.setBackground(bgColor);
		taTiempo.setForeground(Color.black);
		taTiempo.setEditable(false);
    
		JPanel panelReloj = new JPanel();
		panelReloj.add(taTiempo);
		panelReloj.setBackground(bgColor);
    
		taPatente = new JTextArea();
		taPatente.setFont(new Font("monospaced", Font.BOLD, fSize1));
		taPatente.setRows(1);
		taPatente.setBackground(bgColor);
		taPatente.setForeground(Color.black);
		taPatente.setEditable(false);
    
		panelPatente = new JPanel();
		panelPatente.setBackground(bgColor);
		panelPatente.add(taPatente);
    
		taEstado = new JTextArea();
		taEstado.setFont(new Font("monospaced", Font.BOLD, fSize2));
		taEstado.setRows(1);
		taEstado.setBackground(bgColor);
		taEstado.setEditable(false);
		
		panelEstado = new JPanel();
		panelEstado.setBackground(bgColor);
		panelEstado.add(taEstado);

		taRazon = new JTextArea();
		taRazon.setFont(new Font("monospaced", Font.BOLD, fSize3));
		taRazon.setRows(1);
		taRazon.setForeground(Color.RED);
		taRazon.setBackground(bgColor);
		taRazon.setEditable(false);

    
		panelRazon = new JPanel();
		panelRazon.setBackground(bgColor);
		panelRazon.add(taRazon);
    
		frame.add(panelReloj);
		frame.add(panelEstado);        
		frame.add(panelPatente);
		frame.add(panelRazon);
            
		frame.setVisible(true);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("" + e);
		}
   	
	}
	
	public void setValues(int status, String patente) {
		Calendar calendar = Calendar.getInstance();
		last = calendar.getTimeInMillis();
		
		this.status = status;
		this.patente = patente;
	}
	
	@Override
	public void run() {
		SimpleDateFormat sdfFecha = new SimpleDateFormat("dd-MM-yy");
		SimpleDateFormat sdfHoraReloj = new SimpleDateFormat("HH:mm");
		
		
        while(true) {
        	Calendar calendar = Calendar.getInstance();
            Date dateObj = calendar.getTime();
            String fecha = sdfFecha.format(dateObj);
            String hora = sdfHoraReloj.format(dateObj);
            
            Rectangle r = frame.getBounds();
    		//logger.info("Height:" + r.height + " Width:" + r.width);
    		
    		float rh = (float)r.height/(float)1053;
    		float rw = (float)r.width/(float)1848;
    		
    		
    		//logger.info("rh:" + rh + " rw:" + rw);
    		
    		int fSize1 = (int)(210 * rh);
    		int fSize2 = (int)(220 * rh);
    		int fSize3 = (int)(180 * rh);
    		
    		//logger.info("FSize1:" + fSize1 + " FSize2: " + fSize2 + " FSize3:" + fSize3);
    		taTiempo.setFont(new Font("monospaced", Font.BOLD, fSize1));
    		taPatente.setFont(new Font("monospaced", Font.BOLD, fSize1));
    		taEstado.setFont(new Font("monospaced", Font.BOLD, fSize2));
    		taRazon.setFont(new Font("monospaced", Font.BOLD, fSize3));
    		
            
        	taTiempo.setText(fecha + " " + hora);
        	
        	long now = calendar.getTimeInMillis();
        	if(now - last > refresh) {
        		status = ESPERA;
        	}
        
        	updateVista();
        	
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	private void updateVista() {
		
		//Rectangle r = frame.getBounds();
		//System.out.println("height:" + r.height + " width:" + r.width);
		
		switch(status) {
			case ESPERA :
				patente = "";
				taPatente.setText("");
				taEstado.setText("");
				taRazon.setText("");
				panelEstado.repaint();
				panelPatente.repaint();
				panelRazon.repaint();
				break;
				
			case AUTORIZADO:
				taEstado.setForeground(Color.GREEN);
				taPatente.setText("Patente:" + patente);
	        	taEstado.setText("Autorizado");
	        	taRazon.setText("");
	        	panelEstado.repaint();
				panelPatente.repaint();
				panelRazon.repaint();
				break;
				
			case NEGADO_SELLO:
				taEstado.setForeground(Color.RED);
				taPatente.setText("Patente:" + patente);
				taEstado.setText("Negado");
				taRazon.setText("");
				panelEstado.repaint();
				panelPatente.repaint();
				panelRazon.repaint();
				break;
				
			case NEGADO_HORARIO:
				taEstado.setForeground(Color.RED);
				taRazon.setText("Fuera de Horario");
	        	taPatente.setText("Patente:" + patente);
	        	taEstado.setText("Negado");
	        	panelEstado.repaint();
				panelPatente.repaint();
				panelRazon.repaint();
				break;
				
			case NEGADO_NO_REGISTRO:
				taEstado.setForeground(Color.RED);
				taRazon.setText("No Registrado");
	        	taPatente.setText("Patente:" + patente);
	        	taEstado.setText("Negado");
	        	panelEstado.repaint();
				panelPatente.repaint();
				panelRazon.repaint();
				break;	
				
		}
	}

}
