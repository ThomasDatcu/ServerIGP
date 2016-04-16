package Server;

import java.net.Socket;

/**
 * Created by thomas on 11/04/16.
 */
public class ServerSMTP extends Server {
    public ServerSMTP(int port) {
        super(port);
    }

    @Override
    protected void initCommunication(Socket s) {

    }
}
