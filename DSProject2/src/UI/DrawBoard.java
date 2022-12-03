package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.ScrollPane;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import client.Client;
import model.Message;
import model.Position;
import model.Tool;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.JTextArea;

public class DrawBoard implements MouseMotionListener, MouseListener {
	private Canvas canvas;
	private JFrame frame;
	private Client client;
	private JList userList;
	private String selectUser;
	private JLabel lblKick;
	private Boolean bool;
	private Tool tool = Tool.LINE;
	private ConcurrentHashMap<Position, String> textInfo = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Shape, Color> whiteBoardContentCur = new ConcurrentHashMap<>();
	private Color currentColor = Color.BLACK;
	private Graphics2D g2d;
	private int x1, x2;
	private int y1, y2;
	private int currentImageSize = 0;
	private int currentWhiteBoardSize = 0;
	private int currentWhiteBoardTextSize = 0;
	private JButton btnClear;
	private JTextArea textShow;
	private JButton btnEnter;
	private JLabel lblResponse;
	private JButton btnSave;

	/**
	 * Create the application.
	 */
	public DrawBoard(Client client, boolean bool) {
		this.client = client;
		initialize(bool);
		frame.setVisible(true);
		this.g2d = (Graphics2D) canvas.getGraphics();

		this.bool = bool;
		new Thread(new ClientListener()).start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(boolean bool) {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 1247, 652);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		canvas = new Canvas();

		canvas.setBackground(new Color(255, 255, 255));
		canvas.setBounds(122, 75, 694, 526);
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		frame.getContentPane().add(canvas);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setToolTipText("List");
		scrollPane.setBounds(822, 75, 86, 469);
		frame.getContentPane().add(scrollPane);

		JButton btnKick = new JButton("Kick");
		btnKick.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnKick.setVisible(bool);
		btnKick.setBounds(822, 552, 89, 23);
		frame.getContentPane().add(btnKick);

		userList = new JList();
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				selectUser = (String) userList.getSelectedValue();
				if (selectUser.equals(client.getUserName())) {
					btnKick.setEnabled(false);
				} else {
					btnKick.setEnabled(true);
				}
			}
		});
		scrollPane.setViewportView(userList);

		JLabel lblUser = new JLabel("    User List");
		lblUser.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUser.setBounds(822, 43, 86, 31);
		frame.getContentPane().add(lblUser);

		JButton btnLine = new JButton("Line");
		btnLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tool = Tool.LINE;
			}
		});
		btnLine.setBounds(10, 75, 106, 31);
		frame.getContentPane().add(btnLine);

		JButton btnPen = new JButton("Pen");
		btnPen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tool = Tool.PEN;
			}
		});
		btnPen.setBounds(10, 117, 106, 31);
		frame.getContentPane().add(btnPen);

		JButton btnCircle = new JButton("Circle");
		btnCircle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tool = Tool.CIRCLE;
			}
		});
		btnCircle.setBounds(10, 159, 106, 31);
		frame.getContentPane().add(btnCircle);

		JButton btnTriangle = new JButton("Triangle");
		btnTriangle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tool = Tool.TRIANGLE;
			}
		});
		btnTriangle.setBounds(10, 201, 106, 31);
		frame.getContentPane().add(btnTriangle);

		JButton btnRectangle = new JButton("Rectangle");
		btnRectangle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tool = Tool.RECTANGLE;
			}
		});
		btnRectangle.setBounds(10, 243, 106, 31);
		frame.getContentPane().add(btnRectangle);

		JButton btnTest = new JButton("Text");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tool = Tool.TEXT;
			}
		});
		btnTest.setBounds(10, 285, 109, 31);
		frame.getContentPane().add(btnTest);

		JButton btnColorSet = new JButton("Color set");
		btnColorSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(frame, "Select a color", currentColor);
				if (color != null) {
					currentColor = color;
				}
			}
		});
		btnColorSet.setBounds(10, 327, 106, 31);
		frame.getContentPane().add(btnColorSet);

		JLabel lblWelcome = new JLabel("Welcome to White Board");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 22));
		lblWelcome.setBounds(122, 11, 694, 58);
		frame.getContentPane().add(lblWelcome);

		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D) image.getGraphics();
				g.setBackground(Color.WHITE);
				g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

				try {
					drawImage(g, client.getRMI().getWhiteBoardContent(), client.getRMI().getText(),
							client.getRMI().getPreviousDrawingImage());
					ImageIO.write(image, "png", new File("canvas.png"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				lblResponse.setText("Save Successful!");

			}

			private void drawImage(Graphics2D g, ConcurrentHashMap<Shape, Color> whiteBoardContent,
					ConcurrentHashMap<Position, String> text, byte[] previousDrawingImage) {
				if (previousDrawingImage != null) {
					BufferedImage bi;
					try {
						bi = javax.imageio.ImageIO.read(new ByteArrayInputStream(previousDrawingImage));
						g.drawImage(bi, 0, 0, null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				Iterator<ConcurrentHashMap.Entry<Shape, Color>> itr1 = ((ConcurrentHashMap<Shape, Color>) whiteBoardContentCur)
						.entrySet().iterator();
				while (itr1.hasNext()) {
					ConcurrentHashMap.Entry<Shape, Color> entry = itr1.next();
					g.setColor(entry.getValue());
					g.draw(entry.getKey());
				}
				Iterator<ConcurrentHashMap.Entry<Position, String>> itr3 = ((ConcurrentHashMap<Position, String>) text)
						.entrySet().iterator();
				while (itr3.hasNext()) {
					ConcurrentHashMap.Entry<Position, String> entry = itr3.next();
					g.setColor(currentColor);
					g.drawString(entry.getValue(), entry.getKey().getX(), entry.getKey().getY());

				}
				Iterator<ConcurrentHashMap.Entry<Shape, Color>> itr = ((ConcurrentHashMap<Shape, Color>) whiteBoardContent)
						.entrySet().iterator();
				while (itr.hasNext()) {
					ConcurrentHashMap.Entry<Shape, Color> entry = itr.next();
					g.setColor(entry.getValue());
					g.draw(entry.getKey());

				}
				Iterator<ConcurrentHashMap.Entry<Position, String>> itr2 = ((ConcurrentHashMap<Position, String>) textInfo)
						.entrySet().iterator();
				while (itr2.hasNext()) {
					ConcurrentHashMap.Entry<Position, String> entry = itr2.next();
					g.setColor(currentColor);
					g.drawString(entry.getValue(), entry.getKey().getX(), entry.getKey().getY());
				}

			}
		});
		btnSave.setBounds(10, 545, 109, 36);
		frame.getContentPane().add(btnSave);

		lblKick = new JLabel("");
		lblKick.setHorizontalAlignment(SwingConstants.CENTER);
		lblKick.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblKick.setBounds(724, 11, 184, 36);
		frame.getContentPane().add(lblKick);

		btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					client.getRMI().reset();
					g2d.clearRect(0, 0, 1000, 1000);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnClear.setBounds(7, 454, 109, 36);
		frame.getContentPane().add(btnClear);

		JLabel lblThisIsChat = new JLabel("This is chat room!");
		lblThisIsChat.setHorizontalAlignment(SwingConstants.CENTER);
		lblThisIsChat.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblThisIsChat.setBounds(953, 27, 233, 31);
		frame.getContentPane().add(lblThisIsChat);

		textShow = new JTextArea();
		textShow.setRows(10);
		textShow.setEditable(false);
		textShow.setBounds(938, 75, 272, 358);
		frame.getContentPane().add(textShow);

		JTextArea textEnter = new JTextArea();
		textEnter.setFont(new Font("Monospaced", Font.PLAIN, 13));
		textEnter.setBounds(938, 460, 272, 84);
		frame.getContentPane().add(textEnter);

		btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String newText = textEnter.getText();
					String userName = client.getUserName();
					textEnter.setText("");
					textShow.append(userName + ": " + newText);
					client.getRMI().updateMessages(new Message(userName, newText));
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnEnter.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnEnter.setBounds(1121, 552, 89, 23);
		frame.getContentPane().add(btnEnter);

		JLabel lblTalk = new JLabel("   Let us talk!");
		lblTalk.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTalk.setBounds(927, 434, 89, 31);
		frame.getContentPane().add(lblTalk);

		lblResponse = new JLabel("");
		lblResponse.setBounds(10, 511, 106, 23);
		frame.getContentPane().add(lblResponse);

		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (client.getRMI().kickUser(selectUser)) {
						lblKick.setText("User " + selectUser + " kicked successful!");
					} else {
						lblKick.setText("User " + selectUser + " already leave!");
					}
				} catch (RemoteException e1) {
					lblKick.setText("User " + selectUser + " already leave!");
					e1.printStackTrace();
				}
			}
		});

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					int exit = JOptionPane.showConfirmDialog(frame, String.format("Did you want to leave?"), "Warning",
							JOptionPane.YES_NO_OPTION);
					if (exit == JOptionPane.YES_OPTION) {
						if (bool) {
							client.getRMI().managerDisconnect();
							client.getRMI().reset();

						} else {
							client.getRMI().clientDisconnect(client.getUserName());
						}

						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					} else {
						frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					}

				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private class ClientListener implements Runnable {

		@Override
		public void run() {
			try {
				loadPrev();

				while (true) {

					writeMessages();

					userList.setListData(client.getRMI().getUserList());
					List<String> userWaitList = client.getRMI().getUserWaitList();

					drawContent(client.getRMI().getWhiteBoardContent(), client.getRMI().getText(),
							client.getRMI().getPreviousDrawingImage());
					client.getRMI().drawWhiteBoard(whiteBoardContentCur);
					client.getRMI().drawText(textInfo);

					textInfo = new ConcurrentHashMap<Position, String>();
					whiteBoardContentCur = new ConcurrentHashMap<Shape, Color>();

					loginRequest(userWaitList);

					Thread.sleep(200);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		private void loadPrev() {
			File imagFile = new File("canvas.png");
			boolean exists = imagFile.exists();
			if (exists) {
				try {
					BufferedImage bufferImage = ImageIO.read(new File("canvas.png"));
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					javax.imageio.ImageIO.write(bufferImage, "png", out);
					client.getRMI().uploadPreviousDrawingImage(out.toByteArray());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			try {
				Thread.sleep(1000);
				byte[] image = client.getRMI().getPreviousDrawingImage();
				if (image != null) {
					currentImageSize = image.length;
					BufferedImage bi;
					try {
						bi = javax.imageio.ImageIO.read(new ByteArrayInputStream(image));
						g2d.drawImage(bi, 0, 0, null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			} catch (RemoteException | InterruptedException e) {
				e.printStackTrace();
			}

		}

		private void loginRequest(List<String> userWaitList) throws RemoteException {
			if (bool) {
				if (userWaitList.size() > 0) {
					int allowToJoin = JOptionPane.showConfirmDialog(frame,
							String.format("%s requests to join.", userWaitList.get(0)), "Connection Requests",
							JOptionPane.YES_NO_OPTION);
					if (allowToJoin == JOptionPane.YES_OPTION) {
						client.getRMI().acceptUser(userWaitList.get(0));
					} else {
						client.getRMI().refuseUser(userWaitList.get(0));
					}
				}
			} else {
				if (client.getRMI().isManagerDisconnected()) {
					JOptionPane.showMessageDialog(frame, "Manager disconnected!");
					System.exit(0);
				} else if (client.getRMI().haveKicked(client.getUserName())) {
					JOptionPane.showMessageDialog(frame, "You have been kicked!");
					System.exit(0);
				}
			}

		}

		private void writeMessages() {
			CopyOnWriteArrayList<Message> textMessages;
			try {
				textMessages = client.getRMI().getMessages();
				textShow.setText("");
				for (Message text : textMessages) {
					textShow.append(text.getName() + ": " + text.getText() + "\n");
				}

			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

	}

	private void drawContent(ConcurrentHashMap<Shape, Color> whiteBoardContent,
			ConcurrentHashMap<Position, String> text, byte[] previousDrawingImage) {
		if (this.currentWhiteBoardSize > whiteBoardContent.size() || this.currentWhiteBoardTextSize > text.size()
				|| (previousDrawingImage == null && this.currentImageSize != 0)) {

			this.g2d.clearRect(0, 0, 694, 526);
		}

		this.currentWhiteBoardSize = whiteBoardContent.size();
		this.currentWhiteBoardTextSize = text.size();
		if (previousDrawingImage != null) {
			this.currentImageSize = previousDrawingImage.length;
		} else {
			this.currentImageSize = 0;
		}

		Iterator<ConcurrentHashMap.Entry<Shape, Color>> itr = ((ConcurrentHashMap<Shape, Color>) whiteBoardContent)
				.entrySet().iterator();
		while (itr.hasNext()) {
			ConcurrentHashMap.Entry<Shape, Color> entry = itr.next();
			g2d.setColor(entry.getValue());
			g2d.draw(entry.getKey());
		}

		Iterator<ConcurrentHashMap.Entry<Shape, Color>> itr1 = ((ConcurrentHashMap<Shape, Color>) whiteBoardContentCur)
				.entrySet().iterator();
		while (itr1.hasNext()) {
			ConcurrentHashMap.Entry<Shape, Color> entry = itr1.next();
			g2d.setColor(entry.getValue());
			g2d.draw(entry.getKey());
		}

		Iterator<ConcurrentHashMap.Entry<Position, String>> itr2 = ((ConcurrentHashMap<Position, String>) textInfo)
				.entrySet().iterator();
		while (itr2.hasNext()) {
			ConcurrentHashMap.Entry<Position, String> entry = itr2.next();
			g2d.setColor(currentColor);
			g2d.drawString(entry.getValue(), entry.getKey().getX(), entry.getKey().getY());
		}

		Iterator<ConcurrentHashMap.Entry<Position, String>> itr3 = ((ConcurrentHashMap<Position, String>) text)
				.entrySet().iterator();
		while (itr3.hasNext()) {
			ConcurrentHashMap.Entry<Position, String> entry = itr3.next();
			g2d.setColor(currentColor);
			g2d.drawString(entry.getValue(), entry.getKey().getX(), entry.getKey().getY());
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point position = canvas.getMousePosition();

		if (position != null) {
			x2 = position.x;
			y2 = position.y;

			if (tool == Tool.PEN) {

				Line2D line = new Line2D.Float(x1, y1, x2, y2);
				g2d.setColor(currentColor);
				g2d.draw(line);

				whiteBoardContentCur.put(line, currentColor);

				x1 = x2;
				y1 = y2;
			}

		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point position = canvas.getMousePosition();
		if (position != null) {
			x1 = position.x;
			y1 = position.y;

			if (tool == Tool.TEXT) {
				String text = JOptionPane.showInputDialog(frame, "Input Text");
				if (text != null) {
					g2d.setColor(currentColor);
					g2d.drawString(text, x1, y1);
					textInfo.put(new Position(x1, y1), text);
				}

			}

		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point position = canvas.getMousePosition();
		if (position != null) {

			if (tool == Tool.CIRCLE) {
				float dist1 = position.x - x1;
				float dist2 = position.y - y1;
				if (dist1 < 0) {
					dist1 = (float) 0.0 - dist1;
				}
				if (dist2 < 0) {
					dist2 = (float) 0.0 - dist2;
				}

				float use;
				if (dist1 > dist2) {
					use = dist1;
				} else {
					use = dist2;
				}
				if (x1 > position.x) {
					x1 = position.x;
				}
				if (y1 > position.y) {
					y1 = position.y;
				}
				Ellipse2D circle = new Ellipse2D.Float(x1, y1, use, use);
				g2d.setColor(currentColor);
				g2d.draw(circle);
				whiteBoardContentCur.put(circle, currentColor);
			} else if (tool == Tool.TRIANGLE) {
				float dist1 = position.x - x1;
				float dist2 = position.y - y1;
				if (dist1 < 0) {
					dist1 = (float) (-dist1);
				}
				if (dist2 < 0) {
					dist2 = (float) (-dist2);
				}
				if (x1 > position.x) {
					x1 = position.x;
				}
				if (y1 > position.y) {
					y1 = position.y;
				}
				g2d.setColor(currentColor);
				final GeneralPath triangle = new GeneralPath();
				triangle.setWindingRule(Path2D.WIND_EVEN_ODD);
				triangle.moveTo(x1 - (dist1 / 2.0), y1 - (dist2 / 2));
				triangle.lineTo(x1 + (dist1 / 2.0), y1 - (dist2 / 2));
				triangle.lineTo(x1 + (dist1 / 2.0), y1 + (dist2 / 2));
				triangle.lineTo(x1 - (dist1 / 2.0), y1 - (dist2 / 2));
				triangle.closePath();

				whiteBoardContentCur.put(triangle, currentColor);
			} else if (tool == Tool.RECTANGLE) {
				float dist1 = position.x - x1;
				float dist2 = position.y - y1;
				if (dist1 < 0) {
					dist1 = (float) (-dist1);
				}
				if (dist2 < 0) {
					dist2 = (float) (-dist2);
				}
				Rectangle2D rectangle = new Rectangle2D.Float(x1, y1, dist1, dist2);
				g2d.setColor(currentColor);
				g2d.draw(rectangle);
				whiteBoardContentCur.put(rectangle, currentColor);

			} else if (tool == Tool.LINE) {
				Line2D line = new Line2D.Float(x1, y1, position.x, position.y);
				g2d.setColor(currentColor);

				g2d.draw(line);
				whiteBoardContentCur.put(line, currentColor);
			}
		}

	}

	private void positionDeal(int x, int y) {
		float dist1 = x - x1;
		float dist2 = y - y1;
		if (dist1 < 0) {
			dist1 = (float) 0.0 - dist1;
		}
		if (dist2 < 0) {
			dist2 = (float) 0.0 - dist2;
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
