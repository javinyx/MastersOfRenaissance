package it.polimi.ingsw.model.market;

/*
13 Market Marbles
(4 white, 2 blue, 2 grey, 2 yellow, 2 purple, 1 red)
 */

import java.util.ArrayList;
import java.util.List;

public class Market{

    private Resource[][] marketBoard;
    private Resource extraMarble;

    public Market() {
        int white = 4;
        int blue = 2;
        int gray = 2;
        int yellow = 2;
        int violet = 2;
        int red = 1;
        Resource temp;

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
                    case COIN:
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
            extraMarble = Resource.COIN;
        else if (violet != 0)
            extraMarble = Resource.SERVANT;
        else if (red != 0)
            extraMarble = Resource.FAITH;
        else
            System.out.println("Consuela says: no...no...no...");
    }

    /**
     * Pick the selected row from the market (0~2)
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
     * Pick the selected column from the market (0~3)
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

        /*last = marketBoard[row][3];
        System.arraycopy(marketBoard[row], 0, marketBoard[row], 1, 3);
        marketBoard[row][0] = extraMarble;
        extraMarble = last;*/

        last = marketBoard[row][0];
        for(int j = 1; j<4; j++){
            marketBoard[row][j-1] = marketBoard[row][j];
        }
        marketBoard[row][3] = extraMarble;
        extraMarble = last;
    }

    /**
     * Replace the marble in the MarketBoard column
     */
    private void replaceCol(int col){

        Resource last;

        /*last = marketBoard[2][col];
        for (int i = 2; i > 0;  i--){
            marketBoard[i][col] = marketBoard[i-1][col];
        }
        marketBoard[0][col] = extraMarble;

        extraMarble = last;*/

        last = marketBoard[0][col];
        //shifting
        for(int i=1 ; i<3; i++){
            marketBoard[i-1][col] = marketBoard[i][col];
        }
        marketBoard[2][col] = extraMarble;
        extraMarble = last;


        /*
        marketBoard[0][col] = marketBoard[1][col];
        marketBoard[1][col] = marketBoard[2][col];

        for (int i = 1; i > 1;  i--){
            marketBoard[i][col] = marketBoard[i-1][col];
        }
        marketBoard[2][col] = extraMarble;
         */
    }

    /**
     * @return The element in the cell of the MarketBoard
     */
    public Resource getElem(int row, int col){
        return marketBoard[row][col];
    }

    public Resource getExtraMarble() {
        return extraMarble;
    }

    public Resource[][] getMarketBoard() {
        return marketBoard;
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
