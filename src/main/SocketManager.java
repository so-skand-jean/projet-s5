package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class SocketManager {
    UserInterface ui;
    DBUtility log;
    
    public void addUIandDBUtility(UserInterface _ui, DBUtility _l){
        ui = _ui;
        log = _l;
    }

    public void startServer(int port) {
        ServerSocket server;

        try {
            server = new ServerSocket(port);
            System.out.println("Le serveur ecoute le port " + server.getLocalPort());

            new Thread(new AccepterClients(server)).start(); // traiter les nouvelles connections
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * public void connexion(String nomCapteur, String description){ }
     * 
     * public void donnee(String nomCapteur, double valeur){
     * 
     * @Skander Je voudrais avoir un tableau bidimensionnel comportant :
     * nom,type,batiment,etage,lieu,delais,valeur du capteur }
     * 
     * public void deconnexion(String nomCapteur){ }
     * 
     * public void ecouter(){ }
     * 
     */

    private class LireMsg implements Runnable {
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

    private class AccepterClients implements Runnable {
        private ServerSocket socketserver;
        private Socket socket;
        private boolean shouldKeepListening = true;

        public AccepterClients(ServerSocket s) {
            socketserver = s;
        }

        public void stop() {
            shouldKeepListening = false;
        }

        public void run() {
            while (shouldKeepListening) {
                try {
                    socket = socketserver.accept(); // Un client se connecte --> on l'accepte
                    new Thread(new LireMsg(socket)).start(); // traiter les messages du socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
