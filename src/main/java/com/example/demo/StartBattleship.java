package com.example.demo;

public class StartBattleship {
    public static void main(String[] args) {
        String mode = null;
        String port = null;
        String host = null;

        for(int i = 0; i < args.length; ++i){
            switch (args[i]) {
                case "-mode" -> mode = args[++i];
                case "-port" -> port = args[++i];
                case "-server" -> host = args[++i];
            }
        }

        if(mode == null){
            System.err.println("please choose -mode");
            System.exit(1);
        }

        if(mode.equals("serwer")){
            if(port == null){
                System.err.println("please choose -port");
                System.exit(2);
            }
            startSerwer(Integer.parseInt(port));
        }
        if(mode.equals("client")){
            if(port == null){
                System.err.println("please choose -port");
                System.exit(2);
            }
            if(host == null){
                System.err.println("please choose -server");
                System.exit(3);
            }
            startClient(Integer.parseInt(port), host);
        }
    }

    static void startSerwer(int port){
        MyBattleshipServer myBattleshipServer = new MyBattleshipServer(new MyBattleship(new MyBattleshipGenerator()), port);
        myBattleshipServer.start();
    }

    static void startClient(int port, String host){
        MyBattleshipClient myBattleshipClient = new MyBattleshipClient(new MyBattleship(new MyBattleshipGenerator()), port, host);
        myBattleshipClient.start();
    }
}
