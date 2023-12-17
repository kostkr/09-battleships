package com.example.demo;


import java.util.*;

public interface BattleshipGenerator {

    public static void main(String[] args) {

    }

    String generateMap();

    static BattleshipGenerator defaultInstance() {
        return new BattleshipGenerator() {
            final int fieldSize = 10;
            final int fieldArea = fieldSize * fieldSize;
            static final Random random = new Random();
            char[][] field = emptyField();

            private char[][] emptyField() {
                char[][] field = new char[fieldSize][fieldSize];
                for (int i = 0; i < fieldSize; ++i) {
                    for (int j = 0; j < fieldSize; ++j) {
                        field[i][j] = '.';
                    }
                }
                return field;
            }

            @Override
            public String generateMap() {
                createShip(1); createShip(1); createShip(1); createShip(1);
                createShip(2); createShip(2); createShip(2);
                createShip(3); createShip(3);
                createShip(4);

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < fieldSize; i++) {
                    for (int j = 0; j < fieldSize; j++) {
                        stringBuilder.append(field[i][j]);
                    }
                }

                return stringBuilder.toString();
            }

            private void createShip(int shipSize){
                Stack<Integer> freeCoordinates = getFreeCoordinates(shipSize);
                for(Integer coordinate : freeCoordinates){
                    field[coordinate / fieldSize][coordinate % fieldSize] = '#';
                }
            }

            private int getRandomFreeCoordinate() {
                int coordinate;
                do {
                    coordinate = random.nextInt(fieldArea);
                }
                while (!checkCoordinateForFree(coordinate / fieldSize, coordinate % fieldSize));
                return coordinate;
            }// coordinate from 0 to 99, row = floor(coordinate / 10), column = coordinate % 10

            private Stack<Integer> getFreeCoordinates(int shipSize) {
                Stack<Integer> freeCoordinates = new Stack<>();
                do {
                    freeCoordinates.clear();
                    freeCoordinates.push(getRandomFreeCoordinate());
                    creatStackOfFreeCoordinates(freeCoordinates, shipSize);
                } while (freeCoordinates.size() < shipSize);

                return freeCoordinates;
            }

            private void creatStackOfFreeCoordinates(Stack<Integer> stack, final int shipSize) {
                for (Integer coordinate : getFreeCoordinatesAroundRandomOrder(stack.peek())) {// try to find coordinate
                    if (stack.size() < shipSize) {// look for next coordinate if required
                        if(!stack.contains(coordinate)) {
                            stack.push(coordinate);// try coordinate
                            creatStackOfFreeCoordinates(stack, shipSize);
                        }
                    }else {
                        break;
                    }
                }
            }

            private ArrayList<Integer> getFreeCoordinatesAroundRandomOrder(int coordinate) {
                int row = coordinate / fieldSize;
                int column = coordinate % fieldSize;

                ArrayList<Integer> freeCoordinate = new ArrayList<>();
                if (checkCoordinateForFree(row + 1, column))
                    freeCoordinate.add(((row + 1) * fieldSize) + column); // top
                if (checkCoordinateForFree(row - 1, column))
                    freeCoordinate.add(((row - 1) * fieldSize) + column); // bottom
                if (checkCoordinateForFree(row, column + 1))
                    freeCoordinate.add((row * fieldSize) + column + 1); // right
                if (checkCoordinateForFree(row, column - 1)) freeCoordinate.add((row * fieldSize) + column - 1); // left
                Collections.shuffle(freeCoordinate);// change order of list
                return freeCoordinate;
            }

            private boolean checkCoordinateForFree(int row, int column) {// free coordinate - true else false
                if (!inField(row, column)) return false; // check fieldSize
                else if( field[row][column] != '.' ) return false; //

                if(inField(row + 1, column))
                    if(field[row + 1][column] != '.') return false;
                if(inField(row + 1, column - 1))
                    if(field[row + 1][column - 1] != '.') return false;
                if(inField(row + 1, column + 1))
                    if(field[row + 1][column + 1] != '.') return false;
                if(inField(row, column - 1))
                    if(field[row][column - 1] != '.') return false;
                if(inField(row, column + 1))
                    if(field[row][column + 1] != '.') return false;
                if(inField(row - 1, column - 1))
                    if(field[row - 1][column - 1] != '.') return false;
                if(inField(row - 1, column))
                    if(field[row - 1][column] != '.') return false;
                if(inField(row - 1, column + 1))
                    if(field[row - 1][column + 1] != '.') return false;
                return true;
            }

            private boolean inField(int row, int column){
                return row >= 0 && row < fieldSize && column >= 0 && column < fieldSize;
            }
        };
    }
}