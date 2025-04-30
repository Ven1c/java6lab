/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.integraleditor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    static final int SERVER_PORT = 9876;
    static final String SERVER_ADDRESS = "localhost";
    
    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {
            
            Thread receiverThread = new Thread(new TCPClientReceiver(clientSocket));
            receiverThread.start();
            
            System.out.println("Type 'q' to quit.");
            
            while (receiverThread.isAlive()) {
                String message = consoleInput.readLine();
                
                if (message.equalsIgnoreCase("q")) {
                    out.println("EXIT/0\t0\t0\t0");
                    break;
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
