package main.com.netomation.core;

import main.com.netomation.data.Globals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WebsiteListener extends Thread {

    private ServerSocket serverSocket;

    public void run() {
        openUpConnection();
        synchronized (this) {
            try{wait();}catch (Exception ignore){ignore.printStackTrace();}
        }
    }

    private boolean openUpConnection() {
        System.out.println("Starting website listener on port " + Globals.TCP_SERVER_PORT + "...");
        try {
            if(serverSocket != null) {
                try{serverSocket.close();} catch (Exception ignore){}
            }
            serverSocket = new ServerSocket(Globals.TCP_SERVER_PORT);
            System.out.println("Listener setup completed.");
            new InnerListener().start();
            return true;
        } catch(Exception exp) {
            exp.printStackTrace();
        }
        return false;
    }

    private boolean pauseWorker() {
        if(Globals.WORKER_SHOULD_WORK) {
            Globals.WORKER_SHOULD_WORK = false;
            return true;
        }
        return false;
    }

    private boolean pauseListener() {
        if(Globals.LISTENER_SHOULD_WORK) {
            Globals.LISTENER_SHOULD_WORK = false;
            return true;
        }
        return false;
    }

    private boolean continueWorker() {
        if(!Globals.WORKER_SHOULD_WORK) {
            Globals.WORKER_SHOULD_WORK = true;
            return true;
        }
        return false;
    }

    private boolean continueListener() {
        if(Globals.LISTENER_SHOULD_WORK) {
            Globals.LISTENER_SHOULD_WORK = true;
            return true;
        }
        return false;
    }

    private boolean pauseProgram() {
        return pauseWorker() && pauseListener();
    }

    private boolean continueProgram() {
        return continueWorker() && continueListener();
    }

    private class InnerListener extends Thread {
        public void run() {
            Socket userSocket = null;
            try {
                System.out.println("Waiting for client to connect...");
                userSocket = serverSocket.accept();
                System.out.println("Client " + userSocket.getInetAddress().getHostAddress() + " Connected.");
                userSocket.setSoTimeout(4000);
                DataInputStream inputStream = new DataInputStream(userSocket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(userSocket.getOutputStream());
                String commandFromClient = inputStream.readUTF();
                System.out.println("Command got from client: " + commandFromClient);
                respondToUserCommand(commandFromClient, outputStream);
            } catch (Exception exp) {
                System.out.println("Exception was thrown by client listener (" + exp.getMessage() + ")");
                try{sleep(1000);} catch (Exception ignore){}
            } finally {
                try{userSocket.close();} catch (Exception ignore){}
                new InnerListener().start();
            }
        }

        private void respondToUserCommand(String commandFromClient, DataOutputStream outputStream) throws Exception {
            if(commandFromClient.equals(Globals.PAUSE_PROGRAM_COMMAND)) {
                outputStream.writeUTF(pauseProgram() ? "SUCCESS" : "FAILED");
            } else if(commandFromClient.equals(Globals.PAUSE_WORKER_COMMAND)) {
                outputStream.writeUTF(pauseWorker() ? "SUCCESS" : "FAILED");
            } else if(commandFromClient.equals(Globals.PAUSE_LISTENER_COMMAND)) {
                outputStream.writeUTF(pauseListener() ? "SUCCESS" : "FAILED");
            } else if(commandFromClient.equals(Globals.CONTINUE_PROGRAM_COMMAND)) {
                outputStream.writeUTF(continueProgram() ? "SUCCESS" : "FAILED");
            } else if(commandFromClient.equals(Globals.CONTINUE_LISTENER_COMMAND)) {
                outputStream.writeUTF(continueListener() ? "SUCCESS" : "FAILED");
            } else if(commandFromClient.equals(Globals.CONTINUE_WORKER_COMMAND)) {
                outputStream.writeUTF(continueWorker() ? "SUCCESS" : "FAILED");
            } else {
                outputStream.writeUTF("UNKNOWN_COMMAND");
            }
        }

    }

}
