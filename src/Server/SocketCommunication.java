package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public abstract class SocketCommunication extends Thread {

    int state;
    int msgInMailDrop;
    int mailDropLength;
    Socket s;
    BufferedReader inputFromClient;
    DataOutputStream outputToClient;
    UserList allUsers;
    User mailUser;	


    public SocketCommunication(Socket s){
        try {
            this.allUsers = new UserList();
            System.out.println("Creating Communication Socket");
            this.state = 0;
            this.s = s;
            this.inputFromClient = new BufferedReader(new InputStreamReader(s.getInputStream()));
            this.outputToClient = new DataOutputStream(s.getOutputStream());
            this.mailUser = null;
        } catch (IOException ex) {
            System.out.println("Connection closed :/");
        }

    }

    @Override
    public abstract void run();

    protected void close() {
        try {
            System.out.println("Closing all communication and shutting down the socket");
            outputToClient.close();
            inputFromClient.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    protected String receiveData(){
        String textFromClient = null;
        try {
            System.out.println("Awaiting for customer request");
            textFromClient = inputFromClient.readLine();
            System.out.println("Client request : " + textFromClient);
            } catch (IOException e) {
            e.printStackTrace();
        }
        return textFromClient;
    }

    protected int writeBytes(String s){
        try {
            s += "\r\n";
            System.out.println("Message sent to the client : " + s);
            outputToClient.write(s.getBytes());
                return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected int flush(){
        try {
            outputToClient.flush();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }

    protected int tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
