 package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private int id;
    private String name;
    private String password;
    private ArrayList<Message> mails;
    private final int nbMessages;
    private Boolean isConnected = false;

    public User(int id, String name, String password){
        this.id = id;
        this.name = name;
        this.password = password;
        this.mails = new ArrayList<>();
        //TO DO handle mails arraylist
        FileInputStream fis;
        try{
            File repertoire = new File("ressources/mails/"+name);
            File[] listFileMail = repertoire.listFiles();
            if(listFileMail != null) {
                int x = 0;
                for (File f : listFileMail) {
                    if (f.isFile()) {
                        fis = new FileInputStream(f);
                        InputStreamReader lecteur = new InputStreamReader(fis);
                        BufferedReader buff = new BufferedReader(lecteur);

                        String ligne;
                        String ligneMail;
                        String message = "";
                        if((ligne=buff.readLine())!=null) {
                            do {
                                ligneMail = buff.readLine();
                                message += ligneMail + "\r\n";
                            } while (ligneMail.compareTo(".") != 0);
                            mails.add(
                                    new Message(x,
                                            Boolean.getBoolean(ligne),
                                            message,
                                            f.getName()));
                            x++;
                        }
                        fis.close();
                    }
                }
            }
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        nbMessages = mails.size();
    }

    String getName() {
        return this.name;
    }

    public boolean connect(String password) {
        if(this.password.compareTo(password)==0){
            isConnected = true;
            return true;
        }
        return false;
    }

    public int getNumberOfMessageInMaildrop() {
        return this.nbMessages;
    }

    public Message getMessage(int id) {
        if (isConnected){
            for (Message mail : this.mails) {
                if (mail.getId() == id) {
                    mail.read();
                    return mail;
                }
            }
        }
        return null;
    }
    
    public int getLengthOfMailDrop() {
        int res = 0;
        for(Message mail : this.mails){
            res += mail.size;
        }
        return res;
    }
    
    /**
     * 
     * @param i
     * @return length of message or 
     * -1: message marked as deleted
     * -2: message not found
     */
    public int getMessageLength(int i) {
        if(isConnected) {
            Message mail = getMessage(i);
            if (mail == null)
                return -2;
            if (mail.isToDelete())
                return -1;
            return mail.getSize();
        }
        return 0;
    }
    
    public String getMessageText(int i){
        if(isConnected) {
            Message mail = getMessage(i);
            if (mail == null)
                return "-2";// : mail not found !
            if (mail.isToDelete())
                return "-1";// : mail will be destroyed !
            mail.isNewMessage = false;
            return mail.getText();
        }
        return "";
    }

    /**
     * 
     * @param i
     * @return 0 if worked well
     * -1 if message marked as delete
     * -2 if message not found
     */
    public int setMarkDeleted(int i) {
        if(isConnected) {
            Message mail = getMessage(i);
            if (mail == null)
                return -2;
            if (mail.isToDelete())
                return -1;
            return mail.setToDelete();
        }
        return 0;
    }
    
    public void unmarkAllMessages(){
        if(isConnected)
            for(Message mail : this.mails)
                mail.setNotToDelete();
    }
    
    public int disconnect(){

        if(saveUser()==0){
            isConnected = false;
            return 0;
        }
        return -1;
    }

    public int saveUser(){
        try{
            FileOutputStream fos;
            File fileTemp;
            for(Message mail : this.mails){
                fileTemp = new File(mail.fileName);
                fileTemp.delete();
                if(!mail.toBeDeleted) {
                    FileWriter writer = new FileWriter(fileTemp,true);
                    BufferedWriter buff = new BufferedWriter(writer);
                    buff.write(mail.isNewMessage + "\n");
                    buff.write(mail.text);
                    buff.write("\r\n");
                    buff.close();
                }
            }
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
            return -1;
        } catch (IOException ex) {
            Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
            return -2;
        }
        return 0;
    }

    public void addMessage(Message m) {
        this.mails.add(m);
    }
}
