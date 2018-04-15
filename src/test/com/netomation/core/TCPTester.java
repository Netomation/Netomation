package test.com.netomation.core;

import main.com.netomation.data.Globals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCPTester {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", Globals.TCP_SERVER_PORT);
            new DataOutputStream(socket.getOutputStream()).writeUTF("KILL!");
            String from = new DataInputStream(socket.getInputStream()).readUTF();
            System.out.println(from);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

}
