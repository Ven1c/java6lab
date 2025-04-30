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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import my.integraleditor.RecIntegral;

public class TCPClientReceiver implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;

    public TCPClientReceiver(Socket socket) {
        this.clientSocket = socket;
        
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(TCPClientReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String answer = in.readLine();
                if (answer == null) {
                    break;
                }
              
                System.out.println(answer);
                processAnswer(answer);
            }
        } catch (Exception ex) {
            if (!clientSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                System.err.println(ex.getLocalizedMessage());
            }
        }
    }
    
    private void processAnswer(String answer) {
    String[] parts = answer.split("/");
    String[] data = parts[1].split("\t");
    
    
        
        switch (parts[0]) {
            case "EXIT":
                System.err.println("Server closed connection");
                Thread.currentThread().interrupt();
                break;
            case "CALC":
                
                System.err.println("busy\\");
                try {
                    RecIntegral integral = new RecIntegral(Double.parseDouble(data[0]),
                            Double.parseDouble(data[1]),
                            Double.parseDouble(data[2]),
                            0);
                    
                    double result = 0;
                    double min_value = integral.getMinValue();
                    double max_value = integral.getMaxValue();
                    double step = integral.getStep();
                    double interval = (max_value - min_value) / 6;
                    
                    List<Thread> threads = new LinkedList<>();
                    List<RecIntegral> parts1 = new LinkedList<>();

                    for (int i = 0; i < 6; i++) {
                        double start = min_value + i * interval;
                        double end = start + interval;
                        if (i == 5) {
                            end = max_value;
                        }
                        
                        RecIntegral part = new RecIntegral(start, end, step, 0);
                        parts1.add(part);
                        Thread thread = new Thread(part);
                        threads.add(thread);
                        thread.start();
                    }

                    for (Thread thread : threads) {
                        thread.join();
                    }

                    for (RecIntegral calculator : parts1) {
                        result += calculator.getResult();
                    }
                    
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String sendAnswer = "COMPL/" + data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + result;
                    out.println(sendAnswer);
                    
                    System.out.println("/\nresult: " + result);
                } catch (Exception ex) {
                    Logger.getLogger(TCPClientReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            default:
                System.err.println("Got unknown response");
                break;
        }
    }
}
