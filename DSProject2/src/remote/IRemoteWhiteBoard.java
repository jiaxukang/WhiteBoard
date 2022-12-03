package remote;

import java.awt.Color;
import java.awt.Shape;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import model.Message;
import model.Position;



public interface IRemoteWhiteBoard extends Remote{
	public String getManager() throws RemoteException;
	public Boolean checkManager() throws RemoteException;
	public Boolean hasManager() throws RemoteException;
	public Boolean userNameAlreadyExist(String userName) throws RemoteException;
	public void addNewUser(String userName) throws RemoteException;
	public Boolean isManager(String userName) throws RemoteException;
	public String[] getUserList() throws RemoteException;
	public Boolean haveKicked(String userName) throws RemoteException;
	public Boolean kickUser(String userName) throws RemoteException;
	public void managerDisconnect() throws RemoteException;
	public Boolean isManagerDisconnected() throws RemoteException;
	public void clientDisconnect(String userName) throws RemoteException;
	public void reset() throws RemoteException;
	public void drawWhiteBoard(ConcurrentHashMap<Shape, Color> whiteBoardContent) throws RemoteException;
	public void drawText(ConcurrentHashMap<Position, String> textList) throws RemoteException;
	public CopyOnWriteArrayList<Message> getMessages() throws RemoteException;
	public ConcurrentHashMap<Shape, Color> getWhiteBoardContent() throws RemoteException;
	public ConcurrentHashMap<Position, String> getText() throws RemoteException;
	public List<String> getUserWaitList() throws RemoteException;
	public void addWaitList(String clientName) throws RemoteException;
	public Boolean isDeclinedList(String clientName) throws RemoteException;
	public Boolean isWaitList(String clientName) throws RemoteException;
	public void acceptUser(String clientName) throws RemoteException;
	public void refuseUser(String clientName) throws RemoteException;
	public void uploadPreviousDrawingImage(byte[] imageByte) throws RemoteException;
	public byte[] getPreviousDrawingImage() throws RemoteException;
	public void updateMessages(Message message)  throws RemoteException;

}
