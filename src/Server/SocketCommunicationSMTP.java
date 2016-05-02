package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by thomas on 11/04/16.
 */
public class SocketCommunicationSMTP extends SocketCommunication {
    public SocketCommunicationSMTP(Socket s) {
        super(s);
    }

    public void sendGreetings(String s){

    }

    @Override
    public void run() {
        System.out.println("Sending Initialization Message");
        this.writeBytes("220 server SMTP ready");
        this.flush();
        boolean exit = false;
        String textFromClient;
        String[] splitTextFromClient;
        String clientUserName;
        ArrayList<User> userArrayList = new ArrayList<>();
        while(!exit){

            textFromClient = this.receiveData();
            splitTextFromClient = textFromClient.split(" ");
            System.out.println("Data received from client, processing Data");

            switch (this.state){

                case 0 :
                    switch(splitTextFromClient[0]){
                        case "EHLO":
                            this.writeBytes("250 Server SMTP greets" + splitTextFromClient[1] + ". Our server doesn't have any ESMTP functionality implemented yet");
                            this.flush();
                            this.state = 1;
                            break;
                        case "HELO":
                            this.writeBytes("250 Server SMTP greets" + splitTextFromClient[1]);
                            this.flush();
                            this.state = 1;
                            break;
                        default:
                            break;
                    }
                    break;
                case 1 :
                    if(splitTextFromClient[0].equals("MAIL") && splitTextFromClient[1].equals("FROM")){
                        clientUserName = splitTextFromClient[2];
                        if(this.allUsers.checkUser(clientUserName)!=null){
                            this.writeBytes("250 OK");
                            this.flush();
                            this.state = 2;
                        }else{
                            this.writeBytes("501 unknow username");
                            this.flush();
                            this.state = 2;
                        }
                    }

                case 2 :
                    switch(splitTextFromClient[0]){
                        case "RCPT":
                            if(splitTextFromClient[1].equals("TO")){
                                String receiverUserName = splitTextFromClient[2];
                                if(allUsers.checkUser(receiverUserName)!=null){
                                    userArrayList.add(allUsers.getUser(receiverUserName));
                                    this.writeBytes("250 OK receiver added");
                                    this.flush();
                                }else{
                                    this.writeBytes("550 no such user here");
                                    this.flush();
                                }
                            }
                            break;
                        case "DATA":
                            this.writeBytes("354 Waiting for e-mail");
                            this.flush();
                            this.state = 3;
                            break;
                        default:
                            break;
                    }
                    break;
                case 3 :

                    break;
                case 4 :
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
