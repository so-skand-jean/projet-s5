package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class SocketManager {
    UserInterface ui;
    DBUtility db;

    public void addUIandDBUtility(UserInterface _ui, DBUtility _db) {
        ui = _ui;
        db = _db;
    }

    public static void main(String[] args) {
        SocketManager sm = new SocketManager();
        sm.startServer(8952);
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

    private class LireMsg implements Runnable {
        private Socket socket;

        public LireMsg(Socket s) {
            socket = s;
        }

        public void run() {
            BufferedReader in;
            String[] data = { "" };

            Capteur cpt;
            // db.updateCapteurInDB(cpt);
            while (!data[0].equals("Deconnexion")) {
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    data = in.readLine().split(" ");
                    switch (data[0]) {
                    case "Connexion":
                        String[] newCptData = data[2].split(":");
                        cpt = new Capteur(data[1], TypeCapteur.valueOf(newCptData[0]), newCptData[1], Integer.parseInt(newCptData[2]), newCptData[3]);
                        db.syncCapteurWithDb(cpt);
                        break;
                    case "Donnee":
                        break;
                    default:
                        System.err.println(Arrays.toString(data));
                        break;
                    }
                    System.out.println(Arrays.toString(data));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // cpt.setEstConnecte(false);
            // db.updateCapteurInDB(cpt);
            // deconnexion()
        }
    }

    private class AccepterClients implements Runnable {
        private ServerSocket socketserver;
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
                    new Thread(new LireMsg(socketserver.accept())).start(); // traiter les messages du socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
