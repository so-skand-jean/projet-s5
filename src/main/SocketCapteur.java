package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketCapteur {

    public static void runSocket() {

        ServerSocket socketserver;
        Socket socketduserveur;

        try {

            socketserver = new ServerSocket(8952);
            System.out.println("Le serveur est à l'écoute du port " + socketserver.getLocalPort());
            socketduserveur = socketserver.accept();
            System.out.println("Nouveau capteur detecté");

            socketduserveur.close();
            socketserver.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
