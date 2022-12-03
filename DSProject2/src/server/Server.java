package server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import remote.IRemoteWhiteBoard;

public class Server {

    private int port;

    public Server(){
        this.port = 3000; //default port
        while(true){
            try{
                System.out.println("Open a port to start game ");
                Scanner input = new Scanner(System.in);
                port = Integer.parseInt(input.nextLine().strip().trim());

                while(port>65535 || port <=0){
                    System.out.println("Please enter a port between 0 and 65535 please! ");
                    port = Integer.parseInt(input.nextLine().strip().trim());
                    break;
                }

                System.out.println("Create Server Successful on port " + port);
                System.out.println("Please login White Board!");
    			IRemoteWhiteBoard remoteWhiteBoard = new IRemoteWhiteBoardlmpl();
    			Registry registry = LocateRegistry.createRegistry(port);
                registry.bind("firstBoard", remoteWhiteBoard);
                return;
            }catch (NumberFormatException e){
                System.out.println("Please enter a valid port please! ");
            } catch (RemoteException e) {
            	System.out.println(e.toString());
			} catch (AlreadyBoundException e) {
				System.out.println(e.toString());
			}
        }
    }

}
