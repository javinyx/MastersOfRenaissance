package it.polimi.ingsw.model;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        System.out.println("Hi!");
        System.out.println("Stonks");

        Market market = new Market();

        market.printM();
        market.chooseRow(1);
        market.printM();
        market.chooseColumn(1);
        market.printM();
    }
}
