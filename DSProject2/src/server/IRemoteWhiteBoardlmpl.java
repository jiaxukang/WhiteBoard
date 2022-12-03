package server;

import java.awt.Color;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import model.Message;
import model.Position;
import remote.IRemoteWhiteBoard;

public class IRemoteWhiteBoardlmpl extends UnicastRemoteObject implements IRemoteWhiteBoard {

	private List<String> clients = Collections.synchronizedList(new ArrayList<String>());
	private String manager;
	private Boolean managerCheck = false;
	private List<String> waitList = Collections.synchronizedList(new ArrayList<String>());
	private List<String> refuseList = Collections.synchronizedList(new ArrayList<String>());
	private boolean isManagerConnected = true;
	private ConcurrentHashMap<Position, String> textInfo = new ConcurrentHashMap<>();
	private CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<>();
	private BufferedImage preDrawImage = null;
	private ConcurrentHashMap<Shape, Color> whiteBoardContent = new ConcurrentHashMap<>();

	protected IRemoteWhiteBoardlmpl() throws RemoteException {
		super();
	}

	@Override
	public Boolean hasManager() throws RemoteException {
		return !(clients.size() == 0);
	}

	@Override
	public Boolean userNameAlreadyExist(String userName) throws RemoteException {
		if (clients.size() == 0) {
			return false;
		} else {
			if (clients.contains(userName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addNewUser(String userName) throws RemoteException {
		if (!hasManager()) {
			manager = userName;
			managerCheck = true;
			isManagerConnected = true;
		}
		clients.add(userName);
	}

	@Override
	public Boolean isManager(String userName) throws RemoteException {
		return manager.equals(userName);
	}

	@Override
	public String[] getUserList() throws RemoteException {
		return clients.toArray(new String[0]);
	}

	@Override
	public Boolean haveKicked(String userName) throws RemoteException {
		return !clients.contains(userName);
	}

	@Override
	public Boolean kickUser(String userName) throws RemoteException {
		return clients.remove(userName);
	}

	@Override
	public void managerDisconnect() throws RemoteException {
		isManagerConnected = false;
		managerCheck = false;
		manager = "";
		clients = Collections.synchronizedList(new ArrayList<String>());
		messages = new CopyOnWriteArrayList<Message>();
		reset();
	}

	@Override
	public Boolean isManagerDisconnected() throws RemoteException {
		return !isManagerConnected;
	}

	@Override
	public void clientDisconnect(String userName) throws RemoteException {
		clients.remove(userName);

	}

	@Override
	public void reset() throws RemoteException {
		whiteBoardContent = new ConcurrentHashMap<Shape, Color>();
		textInfo = new ConcurrentHashMap<Position, String>();
		preDrawImage = null;
	}

	@Override
	public ConcurrentHashMap<Shape, Color> getWhiteBoardContent() throws RemoteException {
		return whiteBoardContent;
	}

	@Override
	public List<String> getUserWaitList() throws RemoteException {
		return waitList;
	}

	@Override
	public void acceptUser(String name) throws RemoteException {
		clients.add(name);
		waitList.remove(name);
	}

	@Override
	public void refuseUser(String name) throws RemoteException {
		refuseList.add(name);
		waitList.remove(name);
	}

	@Override
	public void uploadPreviousDrawingImage(byte[] imageByte) throws RemoteException {
		try {
			this.preDrawImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(imageByte));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] getPreviousDrawingImage() throws RemoteException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		if (preDrawImage != null) {
			try {
				javax.imageio.ImageIO.write(preDrawImage, "png", out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return out.toByteArray();
		}
		return null;
	}

	@Override
	public Boolean isDeclinedList(String clientName) throws RemoteException {
		if (refuseList.contains(clientName)) {
			refuseList.remove(clientName);
			return true;
		}
		return false;
	}

	@Override
	public Boolean isWaitList(String clientName) throws RemoteException {

		return waitList.contains(clientName);
	}

	@Override
	public void addWaitList(String clientName) throws RemoteException {
		waitList.add(clientName);

	}

	@Override
	public String getManager() throws RemoteException {
		return manager;
	}

	@Override
	public Boolean checkManager() throws RemoteException {

		return managerCheck;
	}

	@Override
	public ConcurrentHashMap<Position, String> getText() throws RemoteException {
		return textInfo;
	}

	@Override
	public void drawText(ConcurrentHashMap<Position, String> textList) throws RemoteException {

		textInfo.putAll((ConcurrentHashMap<Position, String>) textList);
	}

	@Override
	public void drawWhiteBoard(ConcurrentHashMap<Shape, Color> whiteBoardContent) throws RemoteException {
		this.whiteBoardContent.putAll(whiteBoardContent);

	}

	@Override
	public CopyOnWriteArrayList<Message> getMessages() throws RemoteException {
		return messages;
	}

	@Override
	public void updateMessages(Message message) throws RemoteException {
		messages.add(message);
	}

}