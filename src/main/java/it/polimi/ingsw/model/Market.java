package it.polimi.ingsw.model;

/*
13 Biglie Mercato
(4 bianche, 2 blu, 2 grigie, 2 gialle, 2 viola, 1 rossa)
 */

import java.util.ArrayList;
import java.util.List;

public class Market{

    private Resource[][] marketBoard;
    private Resource extraMarble;
    private Resource temp;
    private int white;
    private int blue;
    private int gray;
    private int yellow;
    private int violet;
    private int red;


    public Market() {
        white = 4;
        blue = 2;
        gray = 2;
        yellow = 2;
        violet = 2;
        red = 1;

        marketBoard = new Resource[3][4];

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++) {

                temp = Resource.getRandomResource();

                switch (temp) {
                    case BLANK:
                        if (white != 0) {
                            marketBoard[i][j] = temp;
                            white--;
                        }
                        else
                            j--;
                        break;
                    case SHIELD:
                        if (blue != 0){
                            marketBoard[i][j] = temp;
                            blue--;
                        }
                        else
                            j--;
                        break;
                    case STONE:
                        if (gray != 0){
                            marketBoard[i][j] = temp;
                            gray--;
                        }
                        else
                            j--;
                        break;
                    case COINS:
                        if (yellow != 0){
                            marketBoard[i][j] = temp;
                            yellow--;
                        }
                        else
                            j--;
                        break;
                    case SERVANT:
                        if (violet != 0){
                            marketBoard[i][j] = temp;
                            violet--;
                        }
                        else
                            j--;
                        break;
                    case FAITH:
                        if (red != 0){
                            marketBoard[i][j] = temp;
                            red--;
                        }
                        else
                            j--;
                        break;
                }
            }

        if (white != 0)
            extraMarble = Resource.BLANK;
        else if (blue != 0)
            extraMarble = Resource.SHIELD;
        else if (gray != 0)
            extraMarble = Resource.STONE;
        else if (yellow != 0)
            extraMarble = Resource.COINS;
        else if (violet != 0)
            extraMarble = Resource.SERVANT;
        else if (red != 0)
            extraMarble = Resource.FAITH;
        else
            System.out.println("Consuela says: no...no...no...");
    }

    /**
     * Pick the selected row from the market
     * @return a list of Resource
     */

    public List<Resource> chooseRow(int row){

        List<Resource> resFromRow = new ArrayList<>();

        for (int j = 0; j < 4; j++)
            resFromRow.add(marketBoard[row][j]);

        replaceRow(row);
        return resFromRow;

    }

    /**
     * Pick the selected column from the market
     * @return a list of Resource
     */

    public List<Resource> chooseColumn(int col){

        List<Resource> resFromCol = new ArrayList<>();

        for (int j = 0; j < 3; j++)
            resFromCol.add(marketBoard[j][col]);

        replaceCol(col);
        return resFromCol;

    }

    /**
     * Replace the marble in the MarketBoard row
     */

    private void replaceRow(int row){

        Resource last;

        last = marketBoard[row][3];
        System.arraycopy(marketBoard[row], 0, marketBoard[row], 1, 3);
        marketBoard[row][0] = extraMarble;
        extraMarble = last;
    }

    /**
     * Replace the marble in the MarketBoard column
     */
    private void replaceCol(int col){

        Resource last;

        last = marketBoard[2][col];
        for (int i = 2; i > 0;  i--){
            marketBoard[i][col] = marketBoard[i-1][col];
        }
        marketBoard[0][col] = extraMarble;
        extraMarble = last;
    }

    /**
     * @return The element in the cell of the MarketBoard
     */
    public Resource getElem(int row, int col){
        return marketBoard[row][col];
    }

    /**
     * Print the MarketBoard
     */
    public void printM() {
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 4; j++)
                System.out.print(marketBoard[i][j]+" ");

            System.out.println();}
        System.out.println(extraMarble);
    }
}
