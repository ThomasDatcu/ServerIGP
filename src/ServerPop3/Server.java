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

			String[] ciphers = this.sslServerSocket.getSupportedCipherSuites();

			ArrayList<String> ciphersAnon = new ArrayList<String>();
			for(String cipher : ciphers){
				if(cipher.contains("anon")){
					ciphersAnon.add(cipher);
				}
			}

			String[] pickedCiphers = new String[ciphersAnon.size()];
			pickedCiphers = ciphersAnon.toArray(pickedCiphers);
			this.sslServerSocket.setEnabledCipherSuites(pickedCiphers);
		} catch (IOException e1) {
			e1.printStackTrace();
		} ;
			System.out.println("Server Starting");
	}
	
	
	
	public void run(){
		
            boolean running = true;
            while(running){
                Socket s = null;
                try {
                    System.out.println("Server awaiting connection");
                    s = this.sslServerSocket.accept();
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
