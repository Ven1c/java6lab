/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.integraleditor;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static my.integraleditor.IntegralUI.clients;
import static my.integraleditor.IntegralUI.frame;

public class TCPServer {
    static final int SERVER_PORT = 9876;
    static final int THREADS_LIMIT = 10;
    
    static double Result = 0.0;
    public static ExecutorService threadPool;
    private ServerSocket serverSocket;
    
    public boolean Start() throws InvalidValueException {
        serverSocket = null;
        threadPool = Executors.newFixedThreadPool(THREADS_LIMIT);
        
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getLocalizedMessage());
        }
        System.out.println("TCP Server started on port " + SERVER_PORT);
        return true;
    }
    
    public void Run() throws InvalidValueException {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                int clientPort = clientSocket.getPort();
                
                System.out.println(" + port: " + clientPort + "\t| ");
                
                TCPClientHandler clientHandler = new TCPClientHandler(clientSocket);
                clients.put(clientPort, clientHandler);
                frame.changeCounter();
                threadPool.submit(clientHandler);
            }
        } catch (IOException ex) {
            throw new InvalidValueException(ex.getLocalizedMessage());
        } finally {
            close();
        }
    }
    
    public void close() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            
            
            
            
            try {
                serverSocket.close();
            } catch (IOException ex) {
                System.err.println("Error closing server socket: " + ex.getMessage());
            }
        }

        if (threadPool != null) {
            threadPool.shutdown();
        }
    }

    double CalculateResult(RecIntegral integral) {
        Result = 0.0;
        double length = (integral.getMaxValue() - integral.getMinValue()) / clients.size();
        double start = integral.getMinValue();
        
        for (TCPClientHandler client : clients.values()) {
            client.isBusy = true;
            client.sendAnswer("CALC/" + start + "\t" + (start + length) + "\t" + integral.getStep() + "\t0");
            start += length;
        }
        
        for (TCPClientHandler client : clients.values()) {
            while (client.isBusy) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    System.err.println("Interrupted while waiting for client: " + ex.getMessage());
                }
            }
        }
        
        return Result;
    }
}