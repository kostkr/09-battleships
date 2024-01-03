package com.example.demo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class MyBattleshipServer {
    MyBattleship myBattleship;// battleship implementation
    int port;
    Scanner scanner;
    private final long TIMEOUT = 60000;

    MyBattleshipServer(MyBattleship myBattleship, int port){
        this.myBattleship = myBattleship;
        this.port = port;
        this.scanner = new Scanner(System.in);
    }

    public void start(){
        try (ServerSocket server= new ServerSocket(port)){
            System.out.println("Serwer started");
            Socket client = server.accept();
            System.out.println("Connection accepted.");

            DataOutputStream output = new DataOutputStream(client.getOutputStream());// chanel to write

            DataInputStream input = new DataInputStream(client.getInputStream());// chanel to read

            String[] command;
            String[] answer = new String[2];
            System.out.println("start battle ship");
            command = input.readUTF().split(";");
            answer[0] = myBattleship.attackMe(command[1]);
            answer[1] = readCoordinate();
            output.writeUTF(answer[0] + ";" + answer[1]);

            int communicationError = 0;
            long startTime = System.currentTimeMillis();
            while(true){// start dialog with client
                System.out.println("Waiting for opponent");

                command = input.readUTF().split(";");// command[0] - client's answer, command[1] - coordinate

                if(System.currentTimeMillis() - startTime > TIMEOUT){// check run out time
                    ++communicationError;
                    if(communicationError == 3){
                        System.err.println("Błąd komunikacji");
                        break;
                    }
                }else
                    communicationError = 0;

                if(!checkCommand(command)) {
                    System.out.println(command[0] + command[1]);
                    System.err.println("Błąd komunikacji");
                    break;
                }

                myBattleship.attackOpponent(command[0], answer[1]);

                if(command[0].equals("ostatni zatopiony")){
                    System.out.println("Wygrana");
                    break;
                }

                answer[0] = myBattleship.attackMe(command[1]);// answer to client

                if(answer[0].equals("ostatni zatopiony")) {
                    System.out.println("Przegrana");
                    break;
                }

                answer[1] = readCoordinate();// read coordinate to attack

                output.writeUTF(answer[0] + ";" + answer[1]);

                output.flush();// sent answer to client
                startTime = System.currentTimeMillis();
            }

            System.out.println("Client disconnected");

            input.close();
            output.close();

            client.close();

            System.out.println("Closing connections & channels - DONE.");
        } catch (IOException e) {
            System.err.println("Serwer error");
        }
        System.out.println("close serwer");
    }

    private String readCoordinate(){
        myBattleship.showMaps();
        System.out.println("Please, choose coordinate to attack");
        return scanner.next();
    }

    private boolean checkCommand(String[] command){
        return command[1].charAt(0) >= 'A' && command[1].charAt(0) <= 'J'
                && command[1].charAt(1) >= '0' && command[1].charAt(1) <= '9'
                && (command[0].equals("pudło") || command[0].equals("trafiony")
                || command[0].equals("trafiony zatopiony") || command[0].equals("Przegrana")
                || command[0].equals("start"));
    }
}
