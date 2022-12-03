package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import client.Client;
import remote.IRemoteWhiteBoard;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class LoginPage {

	public JFrame frmWhiteBoardLogin;
	private JTextField textAddress;
	private JTextField textPort;
	private JTextField textName;

	/**
	 * Create the application.
	 */
	public LoginPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWhiteBoardLogin = new JFrame();
		frmWhiteBoardLogin.setTitle("White Board Login");
		frmWhiteBoardLogin.setResizable(false);
		frmWhiteBoardLogin.setBounds(100, 100, 573, 496);
		frmWhiteBoardLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmWhiteBoardLogin.getContentPane().setLayout(null);

		JLabel lblTitle = new JLabel("White Board Login");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblTitle.setBounds(184, 39, 233, 48);
		frmWhiteBoardLogin.getContentPane().add(lblTitle);

		textAddress = new JTextField();
		textAddress.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textAddress.setText("Enter IP Address");
		textAddress.setBounds(154, 123, 263, 48);
		frmWhiteBoardLogin.getContentPane().add(textAddress);
		textAddress.setColumns(10);

		textPort = new JTextField();
		textPort.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textPort.setText("Enter Port");
		textPort.setBounds(154, 198, 263, 48);
		frmWhiteBoardLogin.getContentPane().add(textPort);
		textPort.setColumns(10);

		textName = new JTextField();
		textName.setFont(new Font("Tahoma", Font.PLAIN, 18));
		textName.setText("Enter Your Name");
		textName.setBounds(154, 280, 263, 48);
		frmWhiteBoardLogin.getContentPane().add(textName);
		textName.setColumns(10);

		JLabel lblError = new JLabel("");
		lblError.setHorizontalAlignment(SwingConstants.CENTER);
		lblError.setBounds(66, 418, 430, 14);
		frmWhiteBoardLogin.getContentPane().add(lblError);

		JButton btnLogin = new JButton("Login");

		btnLogin.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnLogin.setBounds(184, 359, 176, 48);
		frmWhiteBoardLogin.getContentPane().add(btnLogin);

		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblError.setVisible(false);
				String host = textAddress.getText().trim();
				String portString = textPort.getText().trim();
				String name = textName.getText().trim();
				try {
					if (host.equals("Enter IP Address") || host.equals("")) {
						lblError.setVisible(true);
						lblError.setText("Please enter a valid host like localhost.");
						return;
					}
					int port = Integer.parseInt(portString);
					Registry registry = LocateRegistry.getRegistry(host, port);
					IRemoteWhiteBoard remoteWhiteBoard = (IRemoteWhiteBoard) registry.lookup("firstBoard");
					Client client = new Client(host, port, name, registry, remoteWhiteBoard);

					if (name.equals("Enter Your Name")) {
						lblError.setVisible(true);
						lblError.setText("Please enter your name.");
						return;
					}

					if (client.getRMI().userNameAlreadyExist(name)) {
						lblError.setVisible(true);
						lblError.setText("User name already existed, Please try another name.");
						return;
					}

					if (!client.getRMI().hasManager()) {
						client.getRMI().addNewUser(name);
						new DrawBoard(client, true);
						frmWhiteBoardLogin.dispose();
						return;
					}
					textAddress.setEnabled(false);
					textPort.setEnabled(false);
					textName.setEnabled(false);
					btnLogin.setEnabled(false);
					client.getRMI().addWaitList(name);
					JOptionPane.showMessageDialog(frmWhiteBoardLogin, "Please waiting for the manager reply ...");
					System.out.println(client.getRMI().isWaitList(name));
					while (client.getRMI().isWaitList(name)) {
						TimeUnit.SECONDS.sleep(1);
					}

					if (!client.getRMI().isDeclinedList(name)) {
						new DrawBoard(client, false);
						frmWhiteBoardLogin.dispose();
					}

					lblError.setText("You are not allowed to enter the white board");
					lblError.setVisible(true);
					textAddress.setEnabled(true);
					textPort.setEnabled(true);
					textName.setEnabled(true);
					btnLogin.setEnabled(true);

				} catch (NumberFormatException e1) {
					lblError.setVisible(true);
					lblError.setText("Please enter a valid port.");
					e1.printStackTrace();
					return;
				} catch (RemoteException e1) {
					lblError.setVisible(true);
					lblError.setText("Server can not found.");
					e1.printStackTrace();
					return;
				} catch (Exception e1) {
					lblError.setVisible(true);
					lblError.setText("Connection failed, please try again !");
					e1.printStackTrace();
					return;
				}
			}

		});
	}

}
