package Server;


import java.io.BufferedReader;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserList {
    ArrayList<User> userList;

    public UserList(){
        //TO DO open file and load all users.
        FileInputStream fis;
        this.userList = new ArrayList<>();
        try{
            System.out.println("ouverture du fichier");
            fis = new FileInputStream(new File("users.txt"));
            InputStreamReader lecteur = new InputStreamReader(fis);
            BufferedReader buff = new BufferedReader(lecteur);
            String ligne;
            System.out.println("chargement des utilisateurs");
            while((ligne=buff.readLine())!=null){
            	System.out.println("ajout de l'utilisateur : " + ligne);
                String[] userIds = ligne.split(" ");
                userList.add(new User(Integer.parseInt(userIds[0]), userIds[1], userIds[2]));
            }
            buff.close();
        }catch(FileNotFoundException e){
        	System.out.println("File user not found");
        } catch (IOException ex) {
        	System.out.println("Error IO");
            Logger.getLogger(UserList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public User connect(String name, String password){
        for(User mailUser : this.userList){
            if(mailUser.getName().compareTo(name) == 0){
                if(mailUser.connect(password))
                    return mailUser;
            }
        }
        return null;
    }

    public String checkUser(String userName) {
        for(User mailUser : this.userList){
            if(mailUser.getName().compareTo(userName) == 0)
                return userName;
        }
        return null;
    }

    public int sendMessage(String messageText, ArrayList<User> users){
        int i = 0;
        for(User u : users){
            Message m = new Message(u.getNumberOfMessageInMaildrop(), true, messageText, u.getName() + "/" + new Date().toString());
            u.addMessage(m);
            u.saveUser();
            i++;
        }
        return i;
    }

    public User getUser(String name){
        for(User u : this.userList)
            if(u.getName().compareTo(name) == 0)
                return u;
        return null;
    }
	
}
