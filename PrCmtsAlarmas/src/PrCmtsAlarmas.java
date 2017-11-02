import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultCaret;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

public class PrCmtsAlarmas {

	private JFrame frmPuertoRicoCmts;
	private final JProgressBar progressBar = new JProgressBar();

	private JLabel lblTiempo;
	private JTextArea txtStatus;

	private String[][] CMTSinfo;
	private String[][] CMTSinfoCasa;
	private String[][] listaServidores;
	private static Calendar cal;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	private String prompt = null;
	private Socket socket = null;
	private PrintStream out = null;
	private InputStream in = null;

	// private JTable table;
	private DefaultTableModel model;

	private Boolean SweepNow = true;
	private JTextField txtUsername;
	private JPasswordField txtPassword;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrCmtsAlarmas window = new PrCmtsAlarmas();
					window.frmPuertoRicoCmts.setVisible(true);
					cal = Calendar.getInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PrCmtsAlarmas() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPuertoRicoCmts = new JFrame();
		frmPuertoRicoCmts.setIconImage(Toolkit.getDefaultToolkit().getImage(PrCmtsAlarmas.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
		frmPuertoRicoCmts.setTitle("Puerto Rico CMTS Alarmas");
		frmPuertoRicoCmts.setBounds(100, 100, 904, 910);
		frmPuertoRicoCmts.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPuertoRicoCmts.setLocationRelativeTo(null);
		JMenuBar menuBar = new JMenuBar();
		frmPuertoRicoCmts.setJMenuBar(menuBar);

		//JMenu mnServidores = new JMenu("Servidores");
		//menuBar.add(mnServidores);

		/*
		JMenuItem mntmLista = new JMenuItem("Lista");
		mntmLista.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listaServidores newWindow = new listaServidores();
				newWindow.setVisible(true);
			}
		});
		mnServidores.add(mntmLista);
		*/

		JMenu mnMonitor = new JMenu("Monitor");
		menuBar.add(mnMonitor);

		JMenuItem mntmIniciar = new JMenuItem("Iniciar");
		mntmIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listaServidores = leerServidores();
				Worker1 worker = new Worker1();
				worker.execute();
			}
		});
		mnMonitor.add(mntmIniciar);

		/*
		JMenu mnSalir = new JMenu("Salir");
		mnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		menuBar.add(mnSalir);
		*/

		//frmPuertoRicoCmts.setExtendedState(JFrame.MAXIMIZED_BOTH); //Full Screen
		frmPuertoRicoCmts.setResizable(false);
		frmPuertoRicoCmts.getContentPane().setLayout(null);
		progressBar.setBounds(10, 716, 872, 25);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		frmPuertoRicoCmts.getContentPane().add(progressBar);

		lblTiempo = new JLabel("Tiempo:");
		lblTiempo.setBounds(740, 11, 137, 14);
		frmPuertoRicoCmts.getContentPane().add(lblTiempo);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 872, 670);
		frmPuertoRicoCmts.getContentPane().add(scrollPane);
		//                                                              0       1         2       3           4       5       6        7       8          9       10         11      12      13    14
		JTable table = new JTable(new DefaultTableModel(new Object[] { "Hora", "Sector", "CMTS", "Interfaz", "Mac", "Conn", "Total", "Oper", "Disable", "Init", "Offline", "%Oper", "Nodo", "IP", "Tipo" }, 0));
		table.setAutoCreateRowSorter(true);
		
		miRender ft = new miRender(11);
		table.setDefaultRenderer(Object.class, ft);

		TableColumnModel tcm = table.getColumnModel();
		tcm.removeColumn(tcm.getColumn(13)); //saca el IP
		tcm.removeColumn(tcm.getColumn(13)); //saca el TIPO

		model = (DefaultTableModel) table.getModel();

		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(80);
		table.getColumnModel().getColumn(3).setPreferredWidth(50);

		table.getColumnModel().getColumn(4).setPreferredWidth(50);
		table.getColumnModel().getColumn(5).setPreferredWidth(50);
		table.getColumnModel().getColumn(6).setPreferredWidth(50);
		table.getColumnModel().getColumn(7).setPreferredWidth(50);
		table.getColumnModel().getColumn(8).setPreferredWidth(50);
		table.getColumnModel().getColumn(9).setPreferredWidth(50);
		table.getColumnModel().getColumn(10).setPreferredWidth(50);
		table.getColumnModel().getColumn(11).setPreferredWidth(50);

		table.getColumnModel().getColumn(12).setPreferredWidth(180);

		scrollPane.setViewportView(table);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 752, 872, 98);
		frmPuertoRicoCmts.getContentPane().add(scrollPane_1);

		txtStatus = new JTextArea();
		DefaultCaret caret = (DefaultCaret) txtStatus.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane_1.setViewportView(txtStatus);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(10, 11, 71, 14);
		frmPuertoRicoCmts.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(170, 11, 71, 14);
		frmPuertoRicoCmts.getContentPane().add(lblPassword);
		
		txtUsername = new JTextField();
		txtUsername.setBounds(74, 8, 86, 20);
		frmPuertoRicoCmts.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);
		
		txtPassword = new JPasswordField();
		txtPassword.setBounds(233, 8, 86, 20);
		frmPuertoRicoCmts.getContentPane().add(txtPassword);
	}

	public class Worker1 extends SwingWorker<String, String> {
		@Override
		protected String doInBackground() throws Exception {
			
			long lastTime = System.currentTimeMillis();
			while (true) {

				String CountDown = String.format("%02d min, %02d seg", TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - lastTime)), TimeUnit.MILLISECONDS.toSeconds((System.currentTimeMillis() - lastTime)) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((System.currentTimeMillis() - lastTime))));

				lblTiempo.setText("Tiempo: " + CountDown);

				if (SweepNow) {
					coreSweep(listaServidores);
					
					SweepNow = false;
					lastTime = System.currentTimeMillis();

				} else if (System.currentTimeMillis() - lastTime > (1000 * 60 * 15)) {
					System.out.println("Auto Start by Time");
					coreSweep(listaServidores);
					
					lastTime = System.currentTimeMillis(); //Sacar para repetir!
				}
			}
		}
	}

	public String[][] leerServidores() {
		File archivo = new File("servidores.lst");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(archivo));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String s = null;
		try {
			int lines = 0;
			int newlines = 0;
			while (br.readLine() != null)
				lines++;

			String[][] lstServ = new String[lines][5];
			br = new BufferedReader(new FileReader(archivo));
			while ((s = br.readLine()) != null) {
				lstServ[newlines] = s.split("\\s+");
				newlines++;
			}

			br.close();
			return lstServ;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public void coreSweep(String[][] listaServidores) {

		txtStatus.setText(null);
		model.setRowCount(0);
		//table.revalidate();

		long temp = System.currentTimeMillis();

		progressBar.setValue(0);
		progressBar.setMaximum(listaServidores.length);

		for (int i = 0; i < (listaServidores.length); i++) { //(listaServidores.length-1)
			//int valorProgress = (100/(listaServidores.length)); 
			txtStatus.append("conectando a " + listaServidores[i][1].toString() + ":");
			progressBar.setValue(i + 1);
			progressBar.setString("Servidor " + (i + 1) + " de " + listaServidores.length + " Servidor Actual: " + listaServidores[i][1].toString());

			//System.out.println(valorProgress* (i+1) + " " + listaServidores.length + " " +valorProgress);

			if (listaServidores[i][4].equals("6K") || listaServidores[i][4].equals("6#") || listaServidores[i][4].equals("C4")) {
				//listaServidores		
				//Sector(0) - Nombre(1) - Url(2) - Puerto(3) - Tipo(4)
				//  2/6/U11(0)  114(1)  20(2)  14(3)  11(4)  0(5)  3(6)  0(7)  78%(8)  LP007C(9)

				//System.out.println(txtUsername.getText() + " " +  txtPassword.getText());
				CMTSinfo = getStatusCMTS(listaServidores[i][2], txtUsername.getText(), txtPassword.getText(), listaServidores[i][4]);
				if (CMTSinfo != null) {
					for (int j = 0; j < (CMTSinfo.length); j++) { // -1
						if (CMTSinfo[j].length == 10) { //debe tener nombre
							if (Integer.parseInt(CMTSinfo[j][7]) > 9) {//clientes offline > 9
								model.addRow(new Object[] { sdf.format(cal.getTime()), listaServidores[i][0], listaServidores[i][1], CMTSinfo[j][0], CMTSinfo[j][1], CMTSinfo[j][2], CMTSinfo[j][3], CMTSinfo[j][4], CMTSinfo[j][5], CMTSinfo[j][6], CMTSinfo[j][7], CMTSinfo[j][8], CMTSinfo[j][9], listaServidores[i][2], listaServidores[i][4] });

							}
						}
					}
				}
			} else if (listaServidores[i][4].equals("CS")) {
				//listaServidores		
				//Sector(0) - Nombre(1) - Url(2) - Puerto(3) - Tipo(4)
				//  2/6/U11(0)  114(1)  20(2)  14(3)  11(4)  0(5)  3(6)  0(7)  78%(8)  LP007C(9)

				CMTSinfoCasa = getStatusCMTSCasa(listaServidores[i][2], txtUsername.getText(), txtPassword.getText());
				if (CMTSinfoCasa != null) {
					for (int j = 0; j < (CMTSinfoCasa.length); j++) { // -1
						if (CMTSinfoCasa[j].length == 7) { //debe tener nombre
							if (Integer.parseInt(CMTSinfoCasa[j][5]) > 9) {//clientes offline > 9
								int percent = (int) ((Float.valueOf(CMTSinfoCasa[j][5]) * 100.0f) / Float.valueOf(CMTSinfoCasa[j][1]));
								model.addRow(new Object[] { sdf.format(cal.getTime()), listaServidores[i][0], listaServidores[i][1], CMTSinfoCasa[j][0], "----", "----", CMTSinfoCasa[j][1], CMTSinfoCasa[j][2], CMTSinfoCasa[j][3], CMTSinfoCasa[j][4], CMTSinfoCasa[j][5], ((100-percent)+"%"), CMTSinfoCasa[j][6] });

							}
						}
					}
				}

			}
			txtStatus.append(" terminado.\n");
		}
		txtStatus.append("Barrido terminado en: " + String.valueOf((System.currentTimeMillis() - temp) / 1000) + " segundos.");
		progressBar.setValue(0);
		progressBar.setString("Esperando...");
	}

	public String[][] getStatusCMTSCasa(String server, String user, String password) {
		Connection conn = new Connection(server);
		Boolean desconectado = true;
		int retrys = 0;
		while (desconectado) {
			try {
				conn.connect();
				boolean isAuthenticated = conn.authenticateWithPassword(user, password);

				if (conn.isAuthenticationComplete()) {
					txtStatus.append(" connectado! ");
					desconectado = false;
				}

			} catch (IOException e) {
				txtStatus.append("\nError: " + e.getMessage() + " Reintentando! " + (retrys + 1) + " de 10.\n");
				desconectado = true;
				retrys++;
				if (retrys >= 10) {
					desconectado = false;
					return null;
				}
			}
		}

		Session sess;
		try {
			sess = conn.openSession();
			txtStatus.append("obteniendo datos...");
			sess.execCommand("show cable modem summary");

			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			ArrayList<String> output = new ArrayList<String>();

			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				output.add(line.trim());
			}

			sess.close();
			conn.close();
			br.close();

			String[][] resToArray = new String[(output.size() - 1)][7]; //Saca 1 Lineas del nuevo array (ultima)  

			for (int i = 0; i < (output.size() - 1); i++) { // se salta 1 linea al final
				resToArray[i] = output.get(i).split("\\s+");
			}

			return resToArray;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String[][] getStatusCMTS(String server, String user, String password, String Tipo) {
		Boolean desconectado = true;
		int retrys = 0;
		while (desconectado) {
			try {
				socket = new Socket();
				//socket.setSoTimeout(10000);
				socket.connect(new InetSocketAddress(server, 23), 2000);
				out = new PrintStream(socket.getOutputStream());
				in = socket.getInputStream();

				if (socket.isConnected()) {
					txtStatus.append(" connectado! ");
					desconectado = false;
				}

			} catch (IOException e) {
				txtStatus.append("\nError: " + e.getMessage() + ", reintentando! " + (retrys + 1) + " de 10\n");
				desconectado = true;
				retrys++;
				if (retrys >= 10) {
					desconectado = false;
					return null;
				}

			}
		}

		String ShowCableModemSummary = null;

		if (Tipo.equals("6K")) {
			ShowCableModemSummary = "show cable mode summary port | exclude 100% | 99% | 98% | 97% | 96% | 95% | 94% | 93% | 92% | Total | --|/D";
			prompt = "6K>";
		}
		if (Tipo.equals("6#")) {
			ShowCableModemSummary = "show cable mode summary port | exclude 100% | 99% | 98% | 97% | 96% | 95% | 94% | 93% | 92% | Total | --|/D";
			prompt = "6K#";
		}
		if (Tipo.equals("C4")) {
			ShowCableModemSummary = "show cable mode summary | exclude 100% | 99% | 98% | 97% | 96% | 95% | 94% | 93% | 92% | Total | --|/D";
			prompt = "#";
		}

		/*if (Tipo.equals("6#")) { // servidor nuevo autentifica mas lento.
			readUntil("username:");
			write(user);
			readUntil("password:");
			write(password);
		} else { 
			write(user);
			write(password);
		}*/		

		write(user);
		write(password);
		readUntil(prompt);

		txtStatus.append("obteniendo datos...");
		write(ShowCableModemSummary);
		String response = readUntil(prompt);
			
		if (response==null)
			return null;
		
		BufferedReader reader = new BufferedReader(new StringReader(response));
		String subString;

		ArrayList<String> output = new ArrayList<String>();
		try {
			while ((subString = reader.readLine()) != null) {
				if (!subString.trim().equals("")) {
					//output.append(subString + "\n"); // Solo agrega las lineas que tienen contenido
					output.add(subString.trim());
					//System.out.println(subString);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			txtStatus.setText("Error: " + e.getMessage());
			return null;
		}

		if (output.size() < 2) {
			return null;
		}
		String[][] resToArray = new String[(output.size() - 2)][9]; //Saca 2 Lineas del nuevo array  
		for (int i = 1; i < (output.size() - 1); i++) { // Se salta 1 linea al principio, y 1 linea al final
			resToArray[i - 1] = output.get(i).split("\\s+"); // Sube en 1 las lineas del indice (por la linea que se removió al principio)
		}
		return resToArray;

	}


	public String readUntil(String pattern) {
        int red = -1;
        byte[] buffer = new byte[1]; 
        byte[] redData;
        String redDataText = null;
        StringBuffer sb = new StringBuffer();
        try {
               while ((red = in.read(buffer)) > -1) {
                      redData = new byte[red];
                      System.arraycopy(buffer, 0, redData, 0, red);
                      redDataText = new String(redData, "UTF-8");
                      sb.append(redDataText);
                      //System.out.print(redDataText); sacar para ver consola
                      if (sb.toString().endsWith(pattern)) {
                    	  //System.out.println(sb.toString().endsWith(pattern));                  	  
						  return sb.toString().replaceAll("\r", "");
					  }				      
               }

        } catch (Exception e) {
               txtStatus.append("\nError: " + e.getMessage() + "\n");
               return null;
        }
        return null;
	}
	
	public Boolean waitForPrompt() {

		try {
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String line = "";
			String response = "";
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				response = response + line + "\n";
				if (in.ready() == false) {
					return true;
				}
			}
			return null;
		} catch (IOException e) {
			txtStatus.append("\nError: " + e.getMessage());
			return false;
		}

	}

	public void write(String value) {
		try {
			out.println(value);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void imprimirDatos(String[][] datos) {
		for (int row = 0; row < datos.length; ++row) {
			for (int column = 0; column < datos[row].length; ++column) {
				System.out.print(datos[row][column] + "  ");
			}
			System.out.print("\r");
		}

	}

	public class miRender extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		private int columna_patron;

		public miRender(int Colpatron) {
			this.columna_patron = Colpatron;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
			setBackground(Color.white);//color de fondo
			table.setForeground(Color.black);//color de texto
			//Si la celda corresponde a una fila con estado FALSE, se cambia el color de fondo a rojo
			if (Integer.parseInt(table.getValueAt(row, columna_patron).toString().replace("%", "")) >= 51) {
				setBackground(Color.yellow);
			}
			if (Integer.parseInt(table.getValueAt(row, columna_patron).toString().replace("%", "")) <= 50) {
				setBackground(Color.orange);
			}
			if (Integer.parseInt(table.getValueAt(row, columna_patron).toString().replace("%", "")) <= 20) {
				setBackground(Color.red);
			}
			if (table.getValueAt(row, columna_patron).toString().equals("----")) {
				setBackground(Color.cyan);
			}
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			return this;
		}

	}
}
