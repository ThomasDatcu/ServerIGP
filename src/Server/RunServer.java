package Server;

import java.io.IOException;

/**
 * Cette classe est la classe mère, elle crée et lance une instance de Serveur
 */
public class RunServer {
    public static void main(String[] args) throws IOException {
        String test = "Je test la séparation de string";
        String[] tab = test.split(" ");
        System.out.println(test);
 //       ServerPop3 s1 = new ServerPop3();
 //       s1.run();
//        ServerSMTP s2 = new ServerSMTP();
//        s2.run();;
    }

}
