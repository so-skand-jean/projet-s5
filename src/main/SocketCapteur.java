package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class SocketCapteur {
    UserInterface ui;
    public SocketCapteur(UserInterface _ui) {
        ui = _ui;
    }

    public void startServer(int port) {
        ServerSocket server;

        try {
            server = new ServerSocket(port);
            System.out.println("Le serveur ecoute le port " + server.getLocalPort());

            Thread t = new Thread(new Accepter_clients(server)); // traiter les nouvelles connections
            t.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /* 
     * public void connexion(String nomCapteur, String description){
     * }
     * 
     * public void donnee(String nomCapteur, double valeur){
     * @Skander
     * Je voudrais avoir un tableau bidimensionnel comportant :
     * nom,type,batiment,etage,lieu,delais,valeur du capteur
     * }
     * 
     * public void deconnexion(String nomCapteur){
     * }
     * 
     * public void ecouter(){
     * }
     * 
     * */
}

class LireMsg implements Runnable {
    private Socket socket;

    public LireMsg(Socket s) {
        socket = s;
    }

    public void run() {
        BufferedReader in;
        String[] data = { "" };
        
        // connexion()
        while (!data[0].equals("Deconnexion")) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                data = in.readLine().split(" ");
                System.out.println(Arrays.toString(data));
                // donnees(data)
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // deconnexion();
    }
}

class Accepter_clients implements Runnable {
    private ServerSocket socketserver;
    private Socket socket;

    public Accepter_clients(ServerSocket s) {
        socketserver = s;
    }

    public void run() {
        while (true) {
            try {
                socket = socketserver.accept(); // Un client se connecte --> on l'accepte
                Thread t = new Thread(new LireMsg(socket)); // traiter les messages du socket
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
