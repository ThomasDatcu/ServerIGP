package Server;

import java.net.Socket;

/**
 * Created by thomas on 11/04/16.
 */
public class ServerSMTP extends Server {
    public ServerSMTP() {
        super(2049);
    }

    @Override
    protected void initCommunication(Socket s) {
        SocketCommunication socketCom = new SocketCommunicationSMTP(s);
        socketCom.start();
        System.out.println("Socket communication start");

    }
}
