package BattleShip;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;

public class Cell extends Rectangle {
    public int x, y;
    public Ship ship = null;
    public boolean wasShot = false;
    private Board board;

    public Cell(int x, int y, Board board) {
        super(30, 30);
        this.x = x;
        this.y = y;
        this.board = board;
        setFill(Color.TRANSPARENT);
        setStroke(Color.BLACK);
        setOpacity(0.7);
    }

    public boolean shoot() {
        wasShot = true;

        if (ship != null && ship.type != 0) {
            ship.hit();
            setFill(Color.RED);
            if (!ship.isAlive()) {
                board.ships--;
                if (ship.vertical) {
                    for (int i = ship.y; i < ship.y + ship.type; i++) {
                        board.getCell(ship.x, i).setFill(Color.DARKRED);
                        System.out.println(board.getCell(ship.x, i).x + ", " + board.getCell(ship.x, i).y);
                    }
                } else {
                    for (int i = ship.x; i < ship.x + ship.type; i++) {
                        board.getCell(i, ship.y).setFill(Color.DARKRED);
                        System.out.println(board.getCell(i, ship.y).x + ", " + board.getCell(i, ship.y).y);
                    }
                }
            }
            return true;
        } else setFill(Color.BLACK);

        return false;
    }
}
