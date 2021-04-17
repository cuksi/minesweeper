package com.codegym.games.minesweeper;

import com.codegym.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {

    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (!isGameStopped) {
            openTile(x,y);
        }
        else if (isGameStopped) {
            restart();
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x,y);
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x,y,"");
            }
        }



        countFlags = countMinesOnField;

        countMineNeighbors();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors(){

        for(int i = 0; i < SIDE; i++) {
            for(int j = 0; j < SIDE; j++) {
                if(!gameField[i][j].isMine){
                    for( GameObject g : getNeighbors(gameField[i][j])) {
                        if(g.isMine) {
                            gameField[i][j].countMineNeighbors++;
                        }
                    }
                }
            }
        }



    }

    private void openTile(int x, int y) {

        if (gameField[y][x].isOpen || gameField[y][x].isFlag || isGameStopped) {

        } else {

            gameField[y][x].isOpen = true;
            countClosedTiles--;
            setCellColor(x,y,Color.GREEN);

            if (gameField[y][x].isMine) {
                setCellValue(x,y, MINE);
                setCellValueEx(x,y,Color.RED,MINE);
                gameOver();
            } else {
                if(countClosedTiles == countMinesOnField) {
                    win();
                }
                int zeroMines = gameField[y][x].countMineNeighbors;
                if (zeroMines == 0) {
                    score+= 5;
                    setScore(score);
                    setCellValue(x,y,"");
                    setCellColor(x,y,Color.GREEN);
                    for (GameObject g : getNeighbors(gameField[y][x])) {
                        if(!g.isOpen) {
                            openTile(g.x,g.y);
                        }
                    }
                } else{
                    score+= 5;
                    setCellNumber(x,y,gameField[y][x].countMineNeighbors);
                    setScore(score);
                }
            }
        }

    }

    private void markTile(int x, int y) {

        if ((gameField[y][x].isOpen || countFlags == 0) && !gameField[y][x].isFlag
                || isGameStopped) {

        } else if (gameField[y][x].isFlag) {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x,y,"");
            setCellColor(x,y,Color.ORANGE);
        }
        else {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x,y,FLAG);
            setCellColor(x,y,Color.YELLOW);
        }


    }


    private void gameOver() {

        isGameStopped = true;
        showMessageDialog(Color.PURPLE, "GAME OVER",Color.RED,50);


    }

    private void win() {

        isGameStopped = true;
        showMessageDialog(Color.BLUE,"YOU HAVE WON",Color.GREEN, 50);

    }

    private void restart() {

        isGameStopped = false;
        countClosedTiles = SIDE*SIDE;
        this.countMinesOnField = 0;
        score = 0;
        setScore(score);

        createGame();

    }



}
