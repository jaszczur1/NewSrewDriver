package com.advantech.screwDriver;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;

public class Okno extends JFrame {

	private static final long serialVersionUID = 1L;

	// main handle
	Okno okno = this;
	HandleFile file;
	// connect for data
	DB_toWindow db = new DB_toWindow();
	
	
	Button calibrate;
	Button test;
	JTextArea main_area = new JTextArea("");
	JTextArea Input_barcode = new JTextArea();
	JTextArea[] areas = new JTextArea[10];
	JFrame o;
	Rxtx r = new Rxtx();
	Thread thread;
	Okno.innerClass watek = null;
	// haandle whether calibrate or test exist in function calibrate, test, runnable
	int function;
	boolean checkConnectDB = true;

	double[] result_array_calib = new double[10];
	double[] result_array_test = new double[5];
	java.util.List<Driver> listForGUI = null;
	private JPasswordField password;

	void purge_mainArea() throws BadLocationException {

		int len = main_area.getLineCount();
		System.err.println(len);
		// int end = main_area.getLineEndOffset(0);
		// input.replaceRange("", 0, end);

	
           if(len >20)
			for (int i = 0; i < len; i++) {
				try {

					int end = main_area.getLineEndOffset(i);
					main_area.replaceRange("", 0, end);
				} catch (Exception e) {
					// TODO: handle exception
				}
				}

		
		main_area.append("\n");
	}

	void purge_areas() {
		for (int i = 0; i < areas.length; i++) {
			JTextArea area = areas[i];
			if (area != null) {
				o.remove(area);
			}
		}
	}

	void create_Areas(int amount) {
		int position = 80;
		for (int i = 0; i < amount; i++) {

			JTextArea jTextAreaTest = new JTextArea(i + 1 + " : ");
			areas[i] = jTextAreaTest;

			jTextAreaTest.setBounds(245, position, 150, 20);
			o.getContentPane().add(jTextAreaTest);
			jTextAreaTest.setCaretPosition(0);
			position += 25;
		}
		o.repaint();
	}

	@SuppressWarnings("deprecation")
	void calibrate() throws IOException, InterruptedException, SQLException, BadLocationException {

		if (!new String(password.getText()).equals("k") || new String(password.getText()).equals("")) {
			main_area.append("\nWprowadzono bledne haslo");
			return;
		} else {
			purge_mainArea();
		}

		purge_mainArea();
		purge_areas();
		create_Areas(10);
		
		if (watek == null) {
			function = 30;
			watek = okno.new innerClass(r.get_inputStream());
			thread = new Thread(watek);
			thread.start();
		} else {
			System.out.println("wznow watek");
			thread.resume();
		}

		if (okno.db.get_patern(okno.Input_barcode.getText()).size() > 0) {
			okno.main_area.append("Tryb poprawy kalibracji");
			function = 30;
		} else {
			// okno.main_area.append("kalibracja nowej wkretarki\n");
			function = 30;
		}
		o.repaint();
	}

	@SuppressWarnings("deprecation")
	void test() throws SQLException, IOException, InterruptedException, BadLocationException {

		if (watek == null) {
			watek = okno.new innerClass(r.get_inputStream());
			thread = new Thread(watek);
			thread.start();
		} else {
			System.out.println("wznow watek");
			thread.resume();
			purge_mainArea();
		}

		if (!checkConnectDB) {
			file.crateFile(Input_barcode.getText());
			function = 10;

			purge_areas();
			create_Areas(10);

		} else if (listForGUI.isEmpty()) {
			calibrate();
			function = 30;

		}
	}

	public Okno() throws SQLException, IOException {
		o = new JFrame("Test wkretarki");

		// TextArea textArea = new TextArea("text", 300 , 600 , );
		// o.add(textArea);
		JComboBox box = new JComboBox();
		for (int i = 0; i < r.getPortList().size(); i++) {
			box.addItem(r.getPortList().get(i));
		}

		box.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String Com = (String) cb.getSelectedItem();

				try {
					r.connect(Com);
					main_area.append("\nWybrano :" + Com);
				} catch (NoSuchPortException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (PortInUseException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (UnsupportedCommOperationException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				}
				okno.main_area.append("\nZeskanuj barcode");
				okno.Input_barcode.requestFocus();
				okno.Input_barcode.setCaretPosition(0);
				box.setEnabled(false);
			}
		});

		box.setBounds(444, 316, 80, 20);

		calibrate = new Button("KALIBRUJ");
		calibrate.setBounds(464, 9, 60, 30);

		test = new Button("TESTUJ");
		test.setBounds(464, 45, 60, 30);

		ActionListener actionListener_kalibruj = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					calibrate();
				} catch (IOException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (InterruptedException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (SQLException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		};

		ActionListener actionListener_test = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (checkConnectDB == false) {
					try {
						test();
					} catch (SQLException | IOException | InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;
				}

				// fetch list patern
				if (listForGUI == null || listForGUI.size() == 0) {

					try {
						listForGUI = okno.db.get_patern(okno.Input_barcode.getText());
						if (password.getText().equals(""))
							okno.main_area.append("Wprowadz haslo do kalibracji\n");
					} catch (SQLException ex) {
						Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
					}
					if (listForGUI.size() == 0) {
						try {
							okno.main_area.append("Wkretarka nieskalibrowana\nUruchomiono tryb kalibracji");
							calibrate();
						} catch (IOException ex) {
							Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
						} catch (InterruptedException ex) {
							Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
						} catch (SQLException ex) {
							Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						return;
					}

				}

				function = listForGUI.size();
				purge_areas();
				int position = 80;
				for (int i = 0; i < listForGUI.size(); i++) {

					JTextArea jTextAreaTest = new JTextArea(listForGUI.get(i).gear + " : ");
					areas[i] = jTextAreaTest;
					jTextAreaTest.setBounds(245, position, 150, 20);
					o.getContentPane().add(jTextAreaTest);
					jTextAreaTest.setCaretPosition(0);
					o.repaint();
					position += 25;
				}

				try {
					test();
				} catch (SQLException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (InterruptedException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		};
		
		AncestorListener listener = new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {
				// TODO Auto-generated method stub
//				System.err.println(main_area.getLineCount()+"d³ugosc okna");
				if( main_area.getLineCount() > 15) {
					
					try {
						purge_mainArea();
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		test.addActionListener(actionListener_test);
		calibrate.addActionListener(actionListener_kalibruj);
		main_area.addAncestorListener(listener);

		main_area.setSize(240, 400);
		Input_barcode.setBounds(308, 55, 150, 20);

		Button table = new Button("TABELA");
		table.setBounds(464, 81, 60, 20);
		table.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					PatternTable patternTable = new PatternTable();

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		o.getContentPane().add(calibrate);
		o.getContentPane().add(test);
		o.getContentPane().add(main_area);
		o.getContentPane().add(Input_barcode);
		o.getContentPane().add(box);
		o.getContentPane().add(table);

		o.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("haslo");
		lblNewLabel.setBounds(252, 22, 46, 14);
		o.getContentPane().add(lblNewLabel);

		JLabel lblBarcode = new JLabel("barcode");
		lblBarcode.setBounds(250, 55, 46, 14);
		o.getContentPane().add(lblBarcode);

		Label label = new Label("Port Rs");
		label.setBounds(376, 316, 62, 22);
		o.getContentPane().add(label);

		password = new JPasswordField();

		password.setBounds(308, 19, 150, 20);
		o.getContentPane().add(password);
		o.setSize(550, 400);
		o.setVisible(true);
		o.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		o.setLocationRelativeTo(null); // centrowanie okna w obszarze pulpitu

		// laczenie z baza i plikiem
		file = new HandleFile();
		checkConnectDB = db.connect(this);
		if (checkConnectDB == false) {
			calibrate.setEnabled(false);
			function = 10;
			password.setEnabled(false);
		}
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new Okno();

				} catch (SQLException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});

	}

	class innerClass implements Runnable {

		InputStream in;
		String value_driver;
		int bad_val = 0;
		

		public innerClass(InputStream inputStream) {
			this.in = inputStream;
		}

		@Override
		public void run() {

			int gear = 0;
			int quantity = 0;
			boolean b = true;

			try {
				file.crateFile(okno.Input_barcode.getText());
				System.out.println("create file :" + okno.Input_barcode.getText());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while ((len = this.in.read(buffer)) > -1) {

					if (b) {						
						// init function view tested gear
						purge_mainArea();
						if (function == 30 || function == 10)	main_area.append("\nKalibruj bieg :" + 1+"\n");
						if(function != 30 && function != 10 )   main_area.append("\nTestuj bieg :" + 1 +"\n");
						b = false;
					}

					if (quantity == function) {

						if (function == 30) {

							db.set_patern(result_array_calib, okno.Input_barcode.getText());
							file.set_patern(result_array_calib);
							new PatternTable();

						}

						else if (checkConnectDB) {
							db.test(listForGUI, okno.Input_barcode.getText());
							file.set_test(listForGUI);

						}

						if (!checkConnectDB) {
							System.err.println("Zapis do pliku");
							main_area.append("\nZapis do pliku " + Input_barcode.getText());
						}
						file.close_file(); // close file
						quantity = 0; // return functiton to init
						gear = 0;
						file.crateFile(Input_barcode.getText());
						b = true;
						thread.suspend();
					}

					if (len > 10) {
						int dot = 0; // check position dot in String from RS

						value_driver = (new String(buffer, 0, len));
						int cute = value_driver.indexOf('+'); // cut String +
						dot = value_driver.indexOf('.'); // cut String +

						try {
							value_driver = value_driver.substring(cute + 1);
						} catch (Exception e) {
						}
						if (dot > 1) {

							double d = Double.parseDouble(value_driver.substring(0, 4));

							if (function == 30 || function == 10) {
								result_array_calib[gear] = result_array_calib[gear] + d;

								areas[gear++].append(d + " , ");
								if (!checkConnectDB)
									file.set_test_unEstabiltyContodb(new Driver(gear, d));

								quantity++;
								System.err.println(quantity);
								int pom = gear;
								
								if (!(quantity == 10) && function == 10) {
									main_area.append("Kalibruj bieg :" + (gear+1) + "\n");
//								} else {
//									main_area.append("Kalibruj bieg :" + (gear + 1) + "\n else");
//									gear = pom;
								}
								
								if (!(quantity == 30) && function == 30) {
									main_area.append("Kalibruj bieg :" + (gear+1) + "\n");
//								} else {
//									main_area.append("Kalibruj bieg :" + (gear + 1) + "\n else");
//									gear = pom;
								}
								
								
								

							} // for test
							else if (checkConnectDB) {
								double ex_patern = listForGUI.get(gear).value;
								System.err.println(ex_patern);

								double min = ex_patern - ex_patern * 0.09;
								double max = ex_patern + ex_patern * 0.09;

								System.out.println(min + " " + max);

								if (d < max && d > min) {
									bad_val= 0;
									// o.getContentPane().setBackground(Color.WHITE);
									listForGUI.get(gear).value = d;
									areas[gear].setBackground(Color.GREEN);
									// System.err.println(line);
									// main_area.replaceRange("Testuj bieg
									// :"+String.valueOf(listForGUI.get(gear).gear), line, 150);
									areas[gear++].append(d + " , ");
//									main_area.append("\n");
									try { // handle for exeption gear > list listForGUI.get(gear)
										main_area.append("Testuj bieg :" + String.valueOf(listForGUI.get(gear).gear)+"\n");
									} catch (Exception e) {
										// TODO: handle exception
									}

									quantity++;
								} else {
									// o.getContentPane().setBackground(Color.red);
									areas[gear].setBackground(Color.RED);
									areas[gear].append(d + " , ");
									if (bad_val == 2) {main_area.append("\nSkontaktuj sie z kordynatorem");
									calibrate.setEnabled(false);
									test.setEnabled(false);
									return; }
									bad_val++;
								}
							}
						}
						if (gear == 10 && function == 30) {
							gear = 0;
							purge_mainArea();
							b = true;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException ex) {
				Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
