package it.polimi.ingsw.model;

/*
13 Biglie Mercato
(4 bianche, 2 blu, 2 grigie, 2 gialle, 2 viola, 1 rossa)
 */

public class Market{

    private MarbleEnum[][] marketBoard;
    private MarbleEnum extraMarble;
    private MarbleEnum temp;
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

        marketBoard = new MarbleEnum[3][4];

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 4; j++) {

                temp = MarbleEnum.getRandomMarble();

                switch (temp) {
                    case WHITE:
                        if (white != 0) {
                            marketBoard[i][j] = temp;
                            white--;
                        }
                        else
                            j--;
                        break;
                    case BLUE:
                        if (blue != 0){
                            marketBoard[i][j] = temp;
                            blue--;
                        }
                        else
                            j--;
                        break;
                    case GRAY:
                        if (gray != 0){
                            marketBoard[i][j] = temp;
                            gray--;
                        }
                        else
                            j--;
                        break;
                    case YELLOW:
                        if (yellow != 0){
                            marketBoard[i][j] = temp;
                            yellow--;
                        }
                        else
                            j--;
                        break;
                    case VIOLET:
                        if (violet != 0){
                            marketBoard[i][j] = temp;
                            violet--;
                        }
                        else
                            j--;
                        break;
                    case RED:
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
            extraMarble = MarbleEnum.WHITE;
        else if (blue != 0)
            extraMarble = MarbleEnum.BLUE;
        else if (gray != 0)
            extraMarble = MarbleEnum.GRAY;
        else if (yellow != 0)
            extraMarble = MarbleEnum.YELLOW;
        else if (violet != 0)
            extraMarble = MarbleEnum.VIOLET;
        else if (red != 0)
            extraMarble = MarbleEnum.RED;
        else
            System.out.println("Consuela says: no...no...no...");
    }

    /**
     * Pick the selected row from the market
     * @return a list of Resource
     */

    public Resource[] chooseRow(int row){

        Resource[] resFromRow = new Resource[4];

        for (int j = 0; j < 4; j++) {

            switch (marketBoard[row][j]) {

                case WHITE:
                    resFromRow[j] = Resource.BLANK;
                    break;
                case BLUE:
                    resFromRow[j] = Resource.SHIELD;
                    break;
                case GRAY:
                    resFromRow[j] = Resource.STONE;
                    break;
                case VIOLET:
                    resFromRow[j] = Resource.SERVANT;
                    break;
                case YELLOW:
                    resFromRow[j] = Resource.COINS;
                    break;
                case RED:
                    resFromRow[j] = null;
                    //Player.moveOnBoard(1);
                    break;

            }
        }
        replaceRow(row);
        return resFromRow;

    }

    /**
     * Pick the selected column from the market
     * @return a list of Resource
     */

    public Resource[] chooseColumn(int col){

        Resource[] resFromCol = new Resource[3];

        for (int j = 0; j < 3; j++) {

            switch (marketBoard[j][col]) {

                case WHITE:
                    resFromCol[j] = Resource.BLANK;
                    break;
                case BLUE:
                    resFromCol[j] = Resource.SHIELD;
                    break;
                case GRAY:
                    resFromCol[j] = Resource.STONE;
                    break;
                case VIOLET:
                    resFromCol[j] = Resource.SERVANT;
                    break;
                case YELLOW:
                    resFromCol[j] = Resource.COINS;
                    break;
                case RED:
                    resFromCol[j] = null;
                    //Player.moveOnBoard(1);
                    break;

            }
        }
        replaceCol(col);
        return resFromCol;

    }

    private void replaceRow(int row){

        MarbleEnum last;

        last = marketBoard[row][3];
        System.arraycopy(marketBoard[row], 0, marketBoard[row], 1, 3);
        marketBoard[row][0] = extraMarble;
        extraMarble = last;
    }

    private void replaceCol(int col){

        MarbleEnum last;

        last = marketBoard[2][col];
        for (int i = 2; i > 0;  i--){
            marketBoard[i][col] = marketBoard[i-1][col];
        }
        marketBoard[0][col] = extraMarble;
        extraMarble = last;
    }


    public MarbleEnum getElem(int row, int col){
        return marketBoard[row][col];
    }

    public void printM() {
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 4; j++)
                System.out.print(marketBoard[i][j]+" ");

            System.out.println();}
        System.out.println(extraMarble);
    }
}
