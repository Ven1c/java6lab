package my.integraleditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static my.integraleditor.IntegralUI.clients;
import static my.integraleditor.IntegralUI.frame;
import static my.integraleditor.TCPServer.Result;

class TCPClientHandler implements Runnable {
    public boolean isRunning = true, isBusy = false;
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public TCPClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error setting up streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println(" v port: " + clientSocket.getPort() + "\t| ");
        

        while (isRunning) {
            try {
                String request = in.readLine();
                if (request == null) {
                    disconnect();
                    break;
                }
                System.out.println("<- port: " + clientSocket.getPort() + "\t| " + request);
                processRequest(request);
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("Error reading from client: " + e.getMessage());
                    disconnect();
                }
                break;
            }
        }
        
        System.out.println(" x port: " + clientSocket.getPort() + "\t| ");
    }
    
    public void stop() {
        sendAnswer("EXIT/0\t0\t0\t0");
    }
    
    public void processRequest(String request) {
        String[] part = request.split("/");
        String[] data = part[1].split("\t");
        
        switch (part[0]) {
            case "EXIT":
                disconnect();
                break;
            case "COMPL":
                Result += Double.parseDouble(data[3]);
                isBusy = false;
                break;
            default:
                System.err.println(" ? port: " + clientSocket.getPort() + "\t| " + request);
                break;
        }
    }
    
    public void sendAnswer(String answer) {
        try {
            out.println(answer);
            System.out.println("-> port: " + clientSocket.getPort() + "\t| " + answer);
        } catch (Exception e) {
            System.err.println("Error sending response to " + clientSocket.getPort() + ": " + e.getMessage());
        }
    }
    
    private void disconnect() {
        System.out.println(" - port: " + clientSocket.getPort() + "\t| ");
        clients.remove(clientSocket.getPort());
        
        isRunning = false;
        try {
            clientSocket.close();
            frame.changeCounter();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
        
    }
}