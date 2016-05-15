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

    @Override
    public void run() {
        System.out.println("Sending Initialization Message");
        this.writeBytes("220 server SMTP ready");
        this.flush();
        boolean exit = false;
        String textFromClient;
        String[] splitTextFromClient = null;
        String clientUserName;
        ArrayList<User> userArrayList = new ArrayList<>();
        String message = "";
        String[] part2;
        boolean firstStep = false;
        boolean secStep = false;
        while(!exit){
            textFromClient = this.receiveData();
            if(state != 3){
                 splitTextFromClient = textFromClient.split(" ");
            }
            System.out.println("Data received from client, processing Data");

            switch (this.state){

                // Dans cet etat, le client doit s'identifier auprès du server, soit par la commande EHLO ou HELO
                // Dans le cas de notre serveur, aucune méthode de la version améliorée du protocole SMTP a été implémenté
                // L'itnetification par HELO ou EHLO produira donc le même résultat
                // Si la commande reconnu différe de HELO ou EHLO, un message est envoyé au client et le canal de communication est coupé.
                case 0 :
                    System.out.println("Server is in state 0");
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
                        case "QUIT":
                            this.state = 4;
                            this.writeBytes("221 transaction success");
                            this.flush();
                            break;
                        default:
                            exit = true;
                            this.writeBytes("500 command unrecognized shutting down the communication");
                            this.flush();
                            this.close();
                            break;
                    }
                    break;
                // Dans ce cas, le serveur est en attente de l'identité de l'expediteur du message
                // Le message doit être sous la forme MAIL FROM:<toto@hotmail.fr>
                // Si le message reçut ne respecte pas le format, un message est envoyé au client et le canal de communication est coupé.
                case 1 :
                    part2 = splitTextFromClient[1].split(":");

                    if(splitTextFromClient[0].equals("MAIL") && part2[0].equals("FROM")){
                        this.writeBytes("250 OK");
                        this.flush();
                        this.state = 2;
                    }else if(splitTextFromClient[0].equals("QUIT")){
                        this.state = 4;
                        this.writeBytes("221 transaction success");
                        this.flush();
                    }
                    else
                    {
                        exit = true;
                        this.writeBytes("500 command unrecognized shutting down the communication");
                        this.flush();
                        this.close();
                    }
                    break;

                // Dans ce cas, le serveur est en attente des mots clé RCPT TO pour ajouter des destinataires ou DATA pour ajouter le corps du message.
                // Lors de la reception de RCPT TO, le server va vérifier si l'utilisateur est connu, si oui il va l'ajouter à la liste des destinataires
                // Et envoyer 250 OK receiver added au client et 550 no such user here dans le cas contraire.
                // Pour RCPT TO, le message doit être au format : RCPT TO:<toto@server.net>
                // Si le serveur reçoit DATA, il va vérifier si la liste de destinataires est vide.
                // Si oui, il va envoyer 554 transaction failed au client, 354 Waiting for e-mail dans le cas contraire
                // Si dans cet état, la commande reconnut différe de RCPT TO ou DATA, un message d'erreur est evnoyé au client et la communication est coupé
                case 2 :
                    System.out.println("Server is in state 2");
                    System.out.println(splitTextFromClient[0]);
                    switch(splitTextFromClient[0]){
                        case "RCPT":
                            part2 = splitTextFromClient[1].split(":");
                            if(part2[0].equals("TO")){
                                String temp1 = part2[1].replace("<","");
                                String temp2 = temp1.replace(">","");
                                String[] receiverUserName = temp2.split("@");
                                System.out.println(receiverUserName[0]);
                                if(allUsers.checkUser(receiverUserName[0])!=null){
                                    userArrayList.add(allUsers.getUser(receiverUserName[0]));
                                    this.writeBytes("250 OK receiver added");
                                    this.flush();
                                }else{
                                    this.writeBytes("550 no such user here");
                                    this.flush();
                                }
                            }else{
                                exit = true;
                                this.writeBytes("500 command unrecognized shutting down the communication");
                                this.flush();
                                this.close();
                            }
                            break;
                        case "DATA":
                            if(userArrayList.isEmpty()){
                                this.writeBytes("554 transaction failed");
                                this.flush();
                                this.close();
                            }else{
                                this.writeBytes("354 Waiting for e-mail");
                                this.flush();
                                this.state = 3;
                           }
                            break;
                        default:
                            exit = true;
                            this.writeBytes("500 command unrecognized shutting down the communication");
                            this.flush();
                            this.close();
                            break;
                    }
                    break;

                //Dans cet état on reçoit le message qui doit être envoyé au client.
                //Les chaines de caractères reçut sont concaténer afin d'être envoyer d'un seul bloc lorsque la fin du message est arrivé.
                //La fin de l'envoi d'un message est caractérisé par "\r\n"."\r\n"
                // Une fois le signal d'arret détecté, on écrit le message dans la boite mail de chaque utilisateur, on envoie 250 OK au client et on passe à l'état suivant.
                case 3 :
                    System.out.println("Server is in state 3");
                    System.out.println(textFromClient);
                    if(textFromClient.equals("") && !firstStep){
                        System.out.println("firstStep");
                        message = message.concat(textFromClient);
                        firstStep = true;
                    }else if(textFromClient.equals(".") && firstStep){
                        System.out.println("secStep");
                        message = message.concat(textFromClient);
                        secStep = true;
                    }else if(textFromClient.equals("") && secStep){
                        System.out.println("thirdStep");
                        message = message.concat(textFromClient);
                        allUsers.sendMessage(message,userArrayList);
                        this.writeBytes("250 OK");
                        this.flush();
                        this.state = 4;
                    }else{
                        message = message.concat(textFromClient+"\n");
                        firstStep = false;
                        secStep = false;
                    }
                    break;

                //Etat final de l'automate, on est en attente de la commande QUIT de la part du client, une fois reçut on procède à la fermeture de la connection.
                // Si la commande receptionée différe de QUIT, un message d'erreur est envoyé au client et la connection est coupé.
                case 4 :
                    if(splitTextFromClient[0].equals("QUIT")){
                        this.writeBytes("221 transaction success");
                        this.flush();
                        exit = true;
                        this.close();
                    }else{
                        exit = true;
                        this.writeBytes("500 command unrecognized shutting down the communication");
                        this.flush();
                        this.close();
                    }

                    break;
                default :
                    System.out.println("Invalid state");
                    break;

            }
        }
    }
}
