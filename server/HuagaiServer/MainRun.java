import java.io.IOException;

import Server.MainServer;

public class MainRun{
    public static void main(String[] args) throws IOException{
        System.out.println("test");
        MainServer mainServer = new MainServer();
        mainServer.startServer();
    }
}