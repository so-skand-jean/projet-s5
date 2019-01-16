package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class SocketManager {
    private UserInterface ui;
    private DBUtility db;
    private AccepterClients act;
    private Thread actThread;

    public void addUIandDBUtility(UserInterface _ui, DBUtility _db) {
        ui = _ui;
        db = _db;
    }

    public void startServer(int port) {
        ServerSocket server;

        try {
            server = new ServerSocket(port);
            System.out.println("Le serveur ecoute le port " + server.getLocalPort());

            act = new AccepterClients(db, ui, server);
            actThread = new Thread(act);
            actThread.start(); // traiter les nouvelles connections
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            act.stop();
            actThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class LireMsg implements Runnable {
        private Socket socket;
        private DBUtility db;
        private UserInterface ui;
        private boolean keepListening = true;

        public LireMsg(DBUtility db, UserInterface ui, Socket s) {
            this.db = db;
            this.ui = ui;
            socket = s;
        }

        public void stop() {
            keepListening = false;
        }

        public void run() {
            BufferedReader in;
            String[] data = { "" };

            Capteur cpt = new Capteur();
            try {
                while (!data[0].equals("Deconnexion") && keepListening) {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    data = in.readLine().split(" ");
                    switch (data[0]) {
                    case "Connexion":
                        String[] newCptData = data[2].split(":");
                        cpt = new Capteur(data[1], TypeCapteur.valueOf(newCptData[0]), newCptData[1],
                                Integer.parseInt(newCptData[2]), newCptData[3]);
                        db.handleNewCapteurFromSocketManager(ui, cpt);
                        break;
                    case "Donnee":
                        cpt.updateValeurCourante(db, ui, new Date(), Double.parseDouble(data[2]));
                        break;
                    case "Deconnexion":
                        cpt.setEstConnecte(db, ui, false);
                        ui.handleDBUpdatedEvent(cpt);
                        break;
                    default:
                        System.err.println(Arrays.toString(data));
                        break;
                    }
                    System.out.println(Arrays.toString(data));
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class AccepterClients implements Runnable {
        private ServerSocket socketserver;
        private boolean shouldKeepListening = true;
        private DBUtility db;
        private UserInterface ui;
        private Thread readMsgThread;
        private LireMsg readMsg;

        public AccepterClients(DBUtility db, UserInterface ui, ServerSocket s) {
            this.db = db;
            this.ui = ui;
            socketserver = s;
        }

        public void stop() {
            try {
                shouldKeepListening = false;
                readMsg.stop();
                readMsgThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void run() {
            while (shouldKeepListening) {
                try {
                    readMsg = new LireMsg(db, ui, socketserver.accept());
                    readMsgThread = new Thread();
                    readMsgThread.start(); // traiter les messages du socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
