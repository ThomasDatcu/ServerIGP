package ServerPop3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


	
public class Server {
	ServerSocket socket;

	public Server(){
		try {
			this.socket = new ServerSocket(2048);
			System.out.println("Server Starting");
			
		} catch (IOException e) {
			System.out.println("Erreur impossible d'ouvrir le socket sur le port 2048");
			e.printStackTrace();
		}
	}
	
	
	
	public void run(){
		
            boolean running = true;
            while(running){
                Socket s = null;
                try {
                    System.out.println("Server awaiting connection");
                    s = socket.accept();
                    this.initCommunication(s);
                } catch (IOException e) {
                    e.printStackTrace();
					running = false;
                }
            }
	}



	private void initCommunication(Socket s){
            SocketCommunication socketCom = new SocketCommunication(s);
            socketCom.start();
            System.out.println("Socket communication start");
		
	}
	
}
