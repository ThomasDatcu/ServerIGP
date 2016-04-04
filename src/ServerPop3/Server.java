package ServerPop3;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


	
public class Server {
	SSLServerSocketFactory socketFactory;
	SSLServerSocket sslServerSocket;

	public Server(){
		try {
			socketFactory= (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			sslServerSocket = (SSLServerSocket) socketFactory.createServerSocket(2048);
		} catch (IOException e1) {
			e1.printStackTrace();
		} ;
			System.out.println("Server Starting");
	}
	
	
	
	public void run(){
		
            boolean running = true;
            while(running){
                SSLSocket s = null;
                try {
                    System.out.println("Server awaiting connection");
                    s = (SSLSocket)sslServerSocket.accept();
					String[] cipherSuite = s.getSupportedCipherSuites();
					ArrayList<String> res = new ArrayList<>();
					for(String s1 : cipherSuite){
						if(s1.contains("anon")){
							res.add(s1);
						}
					}
					s.setEnabledCipherSuites((String[]) res.toArray());
                    this.initCommunication(s);
                } catch (IOException e) {
                    e.printStackTrace();
					running = false;
                }
            }
	}



	private void initCommunication(SSLSocket s){
            SocketCommunication socketCom = new SocketCommunication(s);
            socketCom.start();
            System.out.println("Socket communication start");
		
	}
	
}
