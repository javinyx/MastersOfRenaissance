package it.polimi.ingsw.client.model;

import it.polimi.ingsw.model.market.Resource;

public class Market {
    private Resource[][] marketBoard;
    private Resource extra;

    public Market(Resource[][] marketBoard, Resource extra){
        this.marketBoard = marketBoard;
        this.extra = extra;
    }
    public void setMarketBoard(Resource[][] marketBoard){
        this.marketBoard = marketBoard;
    }
    public void setExtra(Resource res){
        extra = res;
    }
    public Resource getResource(int rowIndex, int columnIndex){
        return marketBoard[rowIndex][columnIndex];
    }
    public Resource getExtraResource(){return extra;}

    public void printM() {
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 4; j++)
                System.out.print(marketBoard[i][j]+" ");

            System.out.println();}
        System.out.println(extra);
    }

    public Resource[][] getMarketBoard() {
        return marketBoard;
    }

    public Resource getExtra() {
        return extra;
    }
}
