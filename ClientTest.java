package jp.ac.tohoku.ecei.sf;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;

public class ClientTest {
	private int countWin ;
	private int countDraw;
	private int countLose;
	
	public void increWin() {
		this.countWin++;
	}
	public void increDraw() {
		this.countDraw++;
	}
	public void increLose() {
		this.countLose++;
	}
	
	public void printCount() {
		System.out.println("Win: " + this.countWin +
							" - Draw: " + this.countDraw +
							" - Lose: " + this.countLose);
	}
	
	public ClientTest() {
		this.countWin  =0;
		this.countDraw =0;
		this.countLose =0;
	}

    private static void startClient( String host, int port, ClientTest client ) {
        AIPlayer c = null;
        try {
            c = new AIPlayer( host, port ); 
            ReversiBoard b;
            b = Game.game( c );
            b.print();
            int result = b.stoneCounts( ReversiBoard.BLACK ) - b.stoneCounts( ReversiBoard.WHITE );
            if (result > 0)
            	client.increWin();
            else if (result == 0)
            	client.increDraw();
            else 
            	client.increLose();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            System.out.println( "Connection failed." );
        }
        try {
            if (c != null) c.close();
        }
        catch ( Exception e ) {}
    }

    public static void main( String[] args ) {
    	ClientTest test = new ClientTest();
    	for(int i=0; i<10;i++)
            startClient( "127.0.0.1", 12345, test);
    	test.printCount();
       
        
        System.out.println("-----------------------------------");
        System.out.println("Game finished.");
    }

}
