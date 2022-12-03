package client;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import remote.IRemoteWhiteBoard;

public class Client {
	private String host;
	private int port;
	private String name;
	private Registry registry;
	private IRemoteWhiteBoard iremoteWhiteBoard;
	

	public Client(String host, int port, String name, Registry registry, IRemoteWhiteBoard remoteWhiteBoard) throws RemoteException {
		this.host = host;
		this.port = port;
		this.name = name;
		this.registry = registry;
		this.iremoteWhiteBoard = remoteWhiteBoard;
	}
	
	public IRemoteWhiteBoard getRMI() {
		return iremoteWhiteBoard;
	}
	
	public String getUserName() {
		return name;
	}
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public void setIremoteWhiteBoard(IRemoteWhiteBoard iremoteWhiteBoard) {
		this.iremoteWhiteBoard = iremoteWhiteBoard;
	}


}
