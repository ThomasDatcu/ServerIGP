package Server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by thomas on 11/04/16.
 */
public class SocketCommunicationSMTP extends SocketCommunication {
    public SocketCommunicationSMTP(Socket s) {
        super(s);
    }

    @Override
    public void run() {
        System.out.println("Sending Initialization Message");
        this.writeBytes("220 server SMTP ready");
        this.flush();
        boolean exit = false;
        String textFromClient;
        String[] splitTextFromClient;
        while(!exit){

            textFromClient = this.receiveData();
            splitTextFromClient = textFromClient.split(" ");
            System.out.println("Data received from client, processing Data");

            switch (this.state){

                case 0 :
                    switch(splitTextFromClient[1]){
                        case "EHLO":
                            break;
                        case "HELO":
                            break;
                        default:
                            break;
                    }
                    break;

                case 1 :
                    switch(splitTextFromClient[1]){
                        case "RCPT":
                            break;
                        case "DATA":
                            break;
                        default:
                            break;
                    }
                    break;
                case 2 :
                    break;
                case 3 :
                    exit = true;
                    this.close();
                    break;
                default :
                    System.out.println("Invalid state");
                    break;

            }
        }
    }
}
