package BattleShip;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;

public class Cell extends Rectangle {
    public int x, y;
    public Ship ship;
    public boolean wasShot = false;
    private Board board;

    private String blast = "src/main/resources/blast.aiff";
    private Media sound = new Media(new File(blast).toURI().toString());
    public MediaPlayer mediaPlayer = new MediaPlayer(sound);

    public Cell(int x, int y, Board board, Ship ship) {
        super(30, 30);
        this.x = x;
        this.y = y;
        this.board = board;
        this.ship = ship;
        setFill(Color.TRANSPARENT);
        setStroke(Color.BLACK);
        setOpacity(0.7);
        mediaPlayer.setVolume(0.01);
    }

    public boolean shoot(boolean isEnemy) {
        wasShot = true;
        if (ship != null && ship.type != 0) {
            mediaPlayer.play();
            setFill(Color.RED);
            ship.hit();
            if (!ship.isAlive()) {
                setShipSunkColor();
                this.board.ships--;
            }
            return true;
        } else setFill(Color.BLACK);
        return false;
    }

    public void setShipSunkColor(){
        //loop through board, set all ships which have 0 health to dark red color
        for(int y = 0; y < 10; y++){
            for(int x = 0; x < 10; x++){
                Cell c = board.getCell(x,y);
                if(c.ship != null)
                {
                    if(c.ship.health == 0){
                        c.setFill(Color.DARKRED);
                    }
                }
            }
        }
    }

    public void hit(Cell cell) {
        if (cell.ship != null && cell.ship.type != 0) {
            if (cell.ship.vertical) {
                int i = cell.y;
                while (i < 10 && board.getCell(cell.x, i).ship != null) {
                    Cell c = board.getCell(cell.x, i);
                    System.out.println(c.x + ", " + c.y);
                    c.ship.hit();
                    i++;
                }
                i = cell.y - 1;
                while (i > 0 && board.getCell(cell.x, i).ship != null) {
                    Cell c = board.getCell(cell.x, i);
                    System.out.println(c.x + ", " + c.y);
                    c.ship.hit();
                    i--;
                }
            } else {
                int i = cell.x;
                while (board.getCell(i, cell.y).ship != null) {
                    Cell c = board.getCell(i, cell.y);
                    c.ship.hit();
                    i++;
                }
                i = cell.x - 1;
                while (board.getCell(i, cell.y).ship != null) {
                    Cell c = board.getCell(i, cell.y);
                    c.ship.hit();
                    i--;
                }
            }
        }
    }
}
