package sample.Client02;

/**
 * Created by julieglasdam on 03/04/2017.
 */
public class Player {
    private int playerNumber;
    private String name;
    private boolean isTurn;

    public Player(){}

    public Player(int playerNumber, String name, boolean isTurn) {
        this.playerNumber = playerNumber;
        this.name = name;
        this.isTurn = isTurn;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean getIsTurn(){
        return isTurn;
    }

    public void setIsTurn(boolean isTurn){
        this.isTurn = isTurn;
    }



}
