package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * my map
 *      # ship
 *      ~ opponent hit
 *      @ destroyed ship
 *      . water
 *
 * opponent map
 *      ? unknown field
 *      # destroyed ship
 *      . water
 *
 * every map consists 10 ships
 *      4 box - 1
 *      3 box - 2
 *      2 box - 3
 *      1 box - 4
 */
public class MyBattleship implements Battleship{
    final int mapSize = 10;

    char[][] myMap;
    char[][] opponentMap;

    MyBattleship(BattleshipGenerator battleshipGenerator){
        myMap = convertTo2DCharArray(battleshipGenerator.generateMyMap());
        opponentMap = convertTo2DCharArray(battleshipGenerator.generateOpponentMap());
    }

    private char[][] convertTo2DCharArray(String input) {
        char[][] output = new char[mapSize][mapSize];
        for(int i = 0; i < mapSize; ++i){
            for(int j = 0; j < mapSize; ++j){
                output[i][j] = input.charAt((10 * i) + j);
            }
        }
        return output;
    }

    @Override
    public String attackMe(String coordinate){
        int row = coordinate.charAt(0) - 'A';
        int column = coordinate.charAt(1) - '0';

        if(myMap[row][column] == '.'){// miss ship
            myMap[row][column] = '~';
            return "pudło";
        }

        if(myMap[row][column] == '#' || myMap[row][column] == '@'){// hit or destroy ship
            myMap[row][column] = '@';

            if(!MyShipIsDestroyed(row, column))
                return "trafiony zatopiony";
            else
                return "trafiony";
        }

        if(lose()) return "ostatni zatopiony";

        return "error";
    }

    private boolean lose(){
        for(int i = 0; i < mapSize; ++i){
            for(int j = 0; j < mapSize; ++j){
                if(myMap[i][j] == '#') return false;
            }
        }
        return true;
    }

    @Override
    public void attackOpponent(String command, String coordinate){
        int row = coordinate.charAt(0) - 'A';
        int column = coordinate.charAt(1) - '0';

        if(command.equals("pudło"))
            opponentMap[row][column] = '.';

        if(command.equals("trafiony")){
            opponentMap[row][column] = '#';
        }

        if(command.equals("trafiony zatopiony")){// unlock box around hit ship
            opponentMap[row][column] = '#';
            ArrayList<int[]> shipCoordinate = getShipCoordinate(opponentMap, row, column);
            for(int[] coor : shipCoordinate){
                for (int i = coor[0] - 1; i <= coor[0] + 1; i++) {
                    for (int j = coor[1] - 1; j <= coor[1] + 1; j++) {
                        if (i >= 0 && i < opponentMap.length && j >= 0 && j < opponentMap[i].length) {
                            if(opponentMap[i][j] == '?') opponentMap[i][j] = '.';
                        }
                    }
                }
            }
        }
    }

    @Override
    public void showMaps() {
        StringBuilder sb = new StringBuilder();
        sb.append("My map        Opponent map\n");
        sb.append("  0123456789\n");
        for(int i = 0; i < myMap.length; ++i){
            switch (i) {
                case 0 -> sb.append("A ");
                case 1 -> sb.append("B ");
                case 2 -> sb.append("C ");
                case 3 -> sb.append("D ");
                case 4 -> sb.append("E ");
                case 5 -> sb.append("F ");
                case 6 -> sb.append("G ");
                case 7 -> sb.append("H ");
                case 8 -> sb.append("I ");
                case 9 -> sb.append("J ");
                default -> sb.append("? ");
            }

            for(int j = 0; j < myMap[i].length; ++j){
                sb.append(myMap[i][j]);
            }
            sb.append("    ");
            for(int j = 0; j < opponentMap[i].length; ++j){
                sb.append(opponentMap[i][j]);
            }
            sb.append("\n");
        }

        System.out.println(sb);
    }

    private boolean MyShipIsDestroyed(int row, int column){
        ArrayList<int[]> myShipCoordinate = getShipCoordinate(myMap, row, column);

        for(int[] coordinate : myShipCoordinate){
            if( myMap[coordinate[0]][coordinate[1]] == '#' )
                return false;
        }
        return true;
    }

    private ArrayList<int[]> getShipCoordinate(char[][] map, int row, int column) {
        ArrayList<int[]> shipCoordinates = new ArrayList<>();

        dfs(map, row, column, shipCoordinates);// looking for ship coordinates

        return shipCoordinates;
    }

    private void dfs(char[][] map,int row, int column, ArrayList<int[]> shipCoordinates) {
        // check if ship exists at coordinate
        if (row >= 0 && row < map.length && column >= 0 && column < map[row].length &&
                (map[row][column] == '#' || map[row][column] == '@')) {

            shipCoordinates.add(new int[]{row, column});// add new coordinate

            char symbol = map[row][column];// save symbol
            map[row][column] = '.';

            dfs(map, row - 1, column - 1, shipCoordinates);// check up
            dfs(map, row - 1, column, shipCoordinates);
            dfs(map, row - 1, column + 1, shipCoordinates);

            dfs(map, row, column - 1, shipCoordinates);// check at
            dfs(map, row, column + 1, shipCoordinates);

            dfs(map, row + 1, column - 1, shipCoordinates);// check under
            dfs(map, row + 1, column, shipCoordinates);
            dfs(map, row + 1, column + 1, shipCoordinates);

            map[row][column] = symbol;// return symbol
        }
    }
}
