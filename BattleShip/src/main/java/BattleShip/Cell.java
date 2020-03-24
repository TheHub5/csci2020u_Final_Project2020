package BattleShip;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.File;

public class Cell extends Rectangle {
    public int x, y;
    public Ship ship = null;
    public boolean wasShot = false;
    private Board board;

    private String blast = "src/main/resources/blast.aiff";
    private Media sound = new Media(new File(blast).toURI().toString());
    public MediaPlayer mediaPlayer = new MediaPlayer(sound);

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
        if (this.ship != null && this.ship.type != 0) {
            mediaPlayer.play();
//            if (this.ship.vertical) {
//                for (int i = ship.y; i < ship.y + this.ship.type; i++) {
//                    Cell cell = board.getCell(ship.x, i);
//                    cell.ship.hit();
//                }
//            }
//            else {
//                for (int i = ship.x; i < ship.x + this.ship.type; i++) {
//                    Cell cell = board.getCell(i, ship.y);
//                    cell.ship.hit();
//                }
//            }
            setFill(Color.RED);
            if (!this.ship.isAlive()) {
                setShipSunkColor();
                this.board.ships--;
                /*
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
                */
            }
            return true;
        } else setFill(Color.BLACK);


        return false;
    }

    public void setShipSunkColor(){
        //loop through board, set all ships which have 0 health to dark red color
        for(int x = 0; x < 10; x++){
            for(int y = 0; y < 10; y++){
                Cell c = board.getCell(x,y);
                if(c.ship != null)
                {
                    if(!c.ship.isAlive()){
                        c.setFill(Color.DARKRED);
                    }
                }
            }
        }
    }
}
