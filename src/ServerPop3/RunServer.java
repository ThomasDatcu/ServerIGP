package ServerPop3;

import java.io.IOException;

/**
 * Cette classe est la classe mère, elle crée et lance une instance de Serveur
 */
public class RunServer {
    public static void main(String[] args) throws IOException {
        Server s = new Server();
        s.run();
    }

}
