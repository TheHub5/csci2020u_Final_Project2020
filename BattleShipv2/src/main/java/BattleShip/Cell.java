package BattleShip;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
        setFill(Color.LIGHTGRAY);
        setStroke(Color.BLACK);
    }

    public int getx () {
        return this.x;
    }

    public int gety () {
        return this.y;
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
                    }
                } else {
                    for (int i = ship.x; i < ship.x + ship.type; i++) {
                        board.getCell(i, ship.y).setFill(Color.DARKRED);
                    }
                }
            }
            return true;
        } else setFill(Color.BLACK);

        return false;
    }
}
