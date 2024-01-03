package com.example.demo;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MyBattleshipClient {
    MyBattleship myBattleship;// battleship implementation
    int port;
    String host;
    private Scanner scanner;
    private final long TIMEOUT = 60000;

    MyBattleshipClient(MyBattleship myBattleship, int port, String host){
        this.myBattleship = myBattleship;
        this.port = port;
        this.scanner = new Scanner(System.in);
        this.host = host;
    }

    public void start() {
        try(Socket socket = new Socket(host, port);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream()) )
        {
            System.out.println("Start battle ship");
            String[] command = new String[2];
            String[] answer;

            command[0] = "start";
            command[1] = readCoordinate();// start game

            output.writeUTF(command[0] + ";" + command[1]);
            long startTime = System.currentTimeMillis();
            int communicationError = 0;
            while (true){
                System.out.println("Waiting for opponent");

                answer = input.readUTF().split(";");

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

                myBattleship.attackOpponent(answer[0], command[1]);

                if(answer[0].equals("ostatni zatopiony")){
                    System.out.println("Wygrana");
                    break;
                }

                command[0] = myBattleship.attackMe(command[1]);// command to serwer

                if(command[0].equals("ostatni zatopiony")) {
                    System.out.println("Przegrana");
                    break;
                }

                command[1] = readCoordinate();// read coordinate to attack opponent

                output.writeUTF(command[0] + ";" + command[1]);

                output.flush();// sent answer to serwer

                startTime = System.currentTimeMillis();
            }
        }catch (IOException e){
            System.err.println("error client");
        }
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