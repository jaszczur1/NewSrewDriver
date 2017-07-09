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

public class Okno extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// main handle
	Okno okno = this;

	HandleFile file;
	// connect for data
	DB db = new DB(this);
	
	TextArea main_area = new TextArea();
	JTextArea Input_barcode = new JTextArea();

	JTextArea[] areas = new JTextArea[10];
	JFrame o;
	Rxtx r = new Rxtx();

	Thread thread;
	Okno.innerClass watek = null;

	// haandle whether calibrate or test exist in function calibrate, test, runnable
	int function;

	double[] result_array_calib = new double[10];
	double[] result_array_test = new double[5];
	java.util.List<Driver> listForGUI = null;

	@SuppressWarnings("deprecation")
	void calibrate() throws IOException, InterruptedException, SQLException {

		System.err.println("calibrate");

		if (areas[0] == null) {
			int position = 40;
			for (int i = 0; i < 10; i++) {

				JTextArea jTextAreaTest = new JTextArea(i + 1 + " : ");
				areas[i] = jTextAreaTest;

				jTextAreaTest.setBounds(210, position, 150, 20);
				o.add(jTextAreaTest);
				jTextAreaTest.setCaretPosition(0);
				o.repaint();
				position += 25;
			}
		} else {

			int position = 40;

			for (int i = 0; i < areas.length; i++) {
				JTextArea area = areas[i];
				if (area != null) {
					o.remove(area);
				}
				JTextArea jTextAreaTest = new JTextArea(i + 1 + " : ");
				areas[i] = jTextAreaTest;

				// System.err.println(areas[i]);
				jTextAreaTest.setBounds(210, position, 150, 20);
				o.add(jTextAreaTest);
				jTextAreaTest.setCaretPosition(0);
				o.repaint();
				position += 25;
				okno.repaint();
			}
		}

		if (watek == null) {
			function = 30;
			watek = okno.new innerClass(r.get_inputStream());
			thread = new Thread(watek);
			thread.start();
		} else {
			System.out.println("wznów watek");
			thread.resume();
		}

		if (okno.db.get_patern(okno.Input_barcode.getText()).size() > 0) {
			okno.main_area.append("Tryb poprawy kalibracji\n");
			function = 30;
		} else {

			okno.main_area.append("Tryb poprawy kalibracji do pliku\n");
			function = 30;

		}
		// o.getContentPane().remove(areas[0]);
		o.repaint();

	}

	@SuppressWarnings("deprecation")
	void test() throws SQLException, IOException, InterruptedException {

		if (listForGUI.isEmpty()) {
			okno.main_area.append("Wkretarka nieskalibrowana\nTryb kalibracji\n");
			calibrate();
			function = 30;
		} else {

			if (watek == null) {
				watek = okno.new innerClass(r.get_inputStream());
				thread = new Thread(watek);
				thread.start();
			} else {
				System.out.println("wznów watek");
				thread.resume();
			}
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
				} catch (NoSuchPortException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (PortInUseException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (UnsupportedCommOperationException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				}
				okno.main_area.append("Zeskanuj barcode i testuj lub kalibruj\n");
				okno.Input_barcode.requestFocus();
				okno.Input_barcode.setCaretPosition(0);
			}
		});

		box.setBounds(400, 250, 80, 20);
		
		Button calibrate = new Button("KALIBRUJ");
		calibrate.setBounds(400, 5, 60, 30);

		Button test = new Button("TESTUJ");
		test.setBounds(400, 45, 60, 30);

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
				}

			}
		};

		ActionListener actionListener_test = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// fetch list patern
				if (listForGUI == null || listForGUI.size() == 0) {

					try {
						listForGUI = okno.db.get_patern(okno.Input_barcode.getText());

					} catch (SQLException ex) {
						Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
					}
					if (listForGUI.size() == 0) {
						try {
							// okno.main_area.append("Wkretarka nieskalibrowana\n Uruchomiono tryb
							// kalibracji");
							calibrate();
						} catch (IOException ex) {
							Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
						} catch (InterruptedException ex) {
							Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
						} catch (SQLException ex) {
							Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
						}
						return;
					}

				}

				function = listForGUI.size();
				if (areas[0] != null) {

					for (int i = 0; i < areas.length; i++) {
						JTextArea area = areas[i];
						if (area != null) {
							o.remove(area);
						}
					}

					int position = 40;
					for (int i = 0; i < listForGUI.size(); i++) {

						JTextArea jTextAreaTest = new JTextArea(listForGUI.get(i).gear + " : ");
						areas[i] = jTextAreaTest;

						System.err.println(areas[i]);

						jTextAreaTest.setBounds(210, position, 150, 20);
						o.add(jTextAreaTest);
						jTextAreaTest.setCaretPosition(0);
						o.repaint();
						position += 25;
					}
				} else {

					// position for row gui
					int position = 40;
					for (int i = 0; i < listForGUI.size(); i++) {

						function = listForGUI.size();
						JTextArea jTextAreaTest = new JTextArea(listForGUI.get(i).gear + " : ");
						areas[i] = jTextAreaTest;
						jTextAreaTest.setBounds(210, position, 150, 20);
						o.add(jTextAreaTest);
						jTextAreaTest.setCaretPosition(0);
						position += 25;
					}
					o.repaint();
				}
				try {
					test();
				} catch (SQLException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				} catch (InterruptedException ex) {
					Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		};

		test.addActionListener(actionListener_test);
		calibrate.addActionListener(actionListener_kalibruj);

		main_area.setSize(100, 400);
		Input_barcode.setBounds(210, 10, 150, 20);

	//	o.add(calibrate);
	//	o.add(test);
		o.add(main_area);
	//	o.add(Input_barcode);
	//	o.add(box);

		o.setLayout(new BorderLayout());
		o.setSize(550, 400);
		o.setVisible(true);
		o.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		o.setLocationRelativeTo(null); // centrowanie okna w obszarze pulpitu
		

		// laczenie z baza i plikiem
		file = new HandleFile();
		db.connect();
		//o.setLayout(null);
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

		public innerClass(InputStream inputStream) {
			this.in = inputStream;
		}

		@Override
		public void run() {

			int gear = 0;
			int quantity = 0;

			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while ((len = this.in.read(buffer)) > -1) {

					if (quantity == function) {
						System.out.println("end test create file");
						file.crateFile(okno.Input_barcode.getText());
						if (function == 30) {

							db.set_patern(result_array_calib, okno.Input_barcode.getText());

							// main_area.append("zapis wynikow kalibracji do bazy danych\n");
							// quantity = 0;

							file.set_patern(result_array_calib);
						} else {
							db.test(listForGUI, okno.Input_barcode.getText());

							file.set_test(listForGUI);
							// main_area.append("zapis wynikow testu do bazy danych\n");
						}
						quantity = 0;
						gear = 0;
					}
					if (len > 10) {

						int dot = 0;

						value_driver = (new String(buffer, 0, len));
						int cute = value_driver.indexOf('+');
						dot = value_driver.indexOf('.');

						try {
							value_driver = value_driver.substring(cute + 1);
						} catch (Exception e) {
						}
						if (dot > 1) {

							double d = Double.parseDouble(value_driver.substring(0, 4));

							if (function == 30) {

								result_array_calib[gear] = result_array_calib[gear] + d;

								areas[gear++].append(d + " , ");
								quantity++;

							} // for test
							else {
								double ex_patern = listForGUI.get(gear).value;
								System.err.println(ex_patern);

								double min = ex_patern - ex_patern * 0.09;
								double max = ex_patern + ex_patern * 0.09;

								System.out.println(min + " " + max);

								if (d < max && d > min) {
									// o.getContentPane().setBackground(Color.WHITE);
									listForGUI.get(gear).value = d;
									areas[gear].setBackground(Color.WHITE);
									areas[gear++].append(d + " , ");
									quantity++;
								} else {
									// o.getContentPane().setBackground(Color.red);
									areas[gear].setBackground(Color.RED);
									areas[gear].append(d + " , ");
								}
							}
						}
						if (gear == 10) {
							gear = 0;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();

			} catch (SQLException ex) {
				Logger.getLogger(Okno.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
