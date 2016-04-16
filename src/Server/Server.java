package Server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Cette classe correspond au serveur principal.
 * Les clients vont d'abord se connecter à ce SSLSocket ( à condition qu'il posséde les bons ciphers.
 * Ensuite, un socketCommunication va être créer et via ce socket que se dérouleront les transactions.
 * Ainsi on s'assure que plusieurs client peuvent se connecter en même temps au serveur
  */
public abstract class Server extends Thread {
	SSLServerSocketFactory socketFactory;
	SSLServerSocket sslServerSocket;
	protected int port;

	public Server(int port){
		try {
			this.port = port;
			// Initialisation du SSLServerSocket sur le port 110
			socketFactory= (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			sslServerSocket = (SSLServerSocket) socketFactory.createServerSocket(this.port);

			// On initialise la liste des ciphers autorisés sur la connection ( ici ceux contenant anon car on ne gére pas les certificats )
			String[] ciphers = this.sslServerSocket.getSupportedCipherSuites();
			ArrayList<String> ciphersAnon = new ArrayList<String>();
			for(String cipher : ciphers){
				if(cipher.contains("anon")){
					ciphersAnon.add(cipher);
				}
			}

			// Une fois la liste initialisée, on autorise les ciphers de la liste a être utilisé sur le SSLServerSocket
			String[] pickedCiphers = new String[ciphersAnon.size()];
			pickedCiphers = ciphersAnon.toArray(pickedCiphers);
			this.sslServerSocket.setEnabledCipherSuites(pickedCiphers);
		} catch (IOException e1) {
			e1.printStackTrace();
		} ;
			System.out.println("Server Starting");
	}


	/**
	 * Méthode d'éxécution, tant que le server est en fonctionnement, on se met en attente d'un accept.
	 * Une fois reçut on instancie un nouveau socketCommunication via la méthode initCommunication
	 */
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




	/**
	 * On instancie un nouveau socketCommuncation qui correspond à un nouveau thread.
	 * Ensuite, on lance l'éxécution de notre thread	 *
     */
	protected abstract void initCommunication(Socket s);

}
