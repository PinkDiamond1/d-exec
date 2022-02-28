/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package graalvm_tcp_server;

import java.io.*;
import java.net.*;

import smartcontract.*;

public class App {
    public static void main(String args[]) throws Exception {
        final int portNumber = 12347;
        ServerSocket ss = new ServerSocket(portNumber);
        System.out.println("TCP server started on port " + portNumber);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutdown Hook called");
            }
        });

        SmartContract sc;
        int arraySize = 0;

        try {
            switch (args[0]) {
                case "inc" -> {
                    sc = new SmartIncrement();
                    arraySize = 8;
                }
                case "mul" -> {
                    sc = new SmartScalarMult();
                    arraySize = 32;
                }
                default -> throw new Exception("wrong arguments on CLI");
            }
        } catch (Exception e) {
            System.out.println("Unknown smart contract type: please use 'inc' or 'mul'.");
            ss.close();
            return;
        }

        try {
            while (true) {
                // Waiting for socket connection
                Socket s = ss.accept();
                System.out.println("\nNew connection accepted");

                // DataInputStream to read data from TCP input stream
                DataInputStream inp = new DataInputStream(s.getInputStream());

                // DataOutputStream to write data on TCP output stream
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                byte input_data[] = new byte[arraySize];
                inp.read(input_data, 0, arraySize);

                byte output_data[] = sc.Execute(input_data);

                out.write(output_data);

                s.close();
                System.out.println("connection closed");
            }
        } catch (Exception e) {
            System.out.println("TCP server caught generic exception: " + e);
        } finally {
            ss.close();
        }
    }
}
