package com.example.demo;

public interface Battleship {
    String attackMe(String coordinate);
    void attackOpponent(String command, String coordinate);
    void showMaps();
}
