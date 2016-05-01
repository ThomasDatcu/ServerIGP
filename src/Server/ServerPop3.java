package Server;

import java.net.Socket;

/**
 * Created by thomas on 11/04/16.
 */
public class ServerPop3 extends Server {

    public ServerPop3(){
        super(2048);
    }

    @Override
    protected void initCommunication(Socket s) {
        {
            SocketCommunication socketCom = new SocketCommunicationPOP3(s);
            socketCom.start();
            System.out.println("Socket communication start");

        }
    }
}
