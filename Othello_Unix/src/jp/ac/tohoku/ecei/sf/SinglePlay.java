package jp.ac.tohoku.ecei.sf;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;

import javax.swing.JFrame;
/**
   メインクラス
 */
public class SinglePlay {
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
	
	public SinglePlay() {
		this.countWin  =0;
		this.countDraw =0;
		this.countLose =0;
	}

    /**
        Usage - under construction
    */
    private static String usage() {
        return ("Usage: java -jar echo.jar (client HOST PORT|server PORT)");
    }

    private static void startServer( int port ) {
        ServerPlayer1 s = null;
        try {
            s = new ServerPlayer1( port );
            s.waitConnection();
            s.close(); 
        }
        catch ( Exception e ) {
            System.out.println( "Starting server failed." );
        }

        try {
            if ( s != null ) s.close(); 
        } 
        catch ( Exception e ) {}
    }

    // Remote Player
    private static void startClient( String host, int port, SinglePlay client ) {
        RemotePlayer c = null;
        try {
            c = new RemotePlayer( host, port ); 
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

    // Random Player
    private static void startRandomClient( String host, int port, SinglePlay client ) {
        RandomPlayer c = null;
        try {
            c = new RandomPlayer( host, port ); 
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

    // Human Player
    private static void startHumanClient (String host, int port) {
    	HumanPlayer h;
		try {
			h = new HumanPlayer(host, port);
	    	h.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public static void main( String[] args ) {
    	SinglePlay test = new SinglePlay();
        if ( args.length == 0 ) {
            System.out.println( usage() ); 
            return; 
        }
        if ( args[0].startsWith("c") ) {  // Remote Player
            if (args.length < 3 ) {
                System.out.println( usage() );
                return;
            }
            String host = args[1];
            int    port = Integer.parseInt( args[2] ); 

            startClient( host, port, test);

	        System.out.println("-----------------------------------");
	        System.out.println("Game finished.");
        }
        else if ( args[0].startsWith("r") ) {  // Random Player
            if (args.length < 3 ) {
                System.out.println( usage() );
                return;
            }
            String host = args[1];
            int    port = Integer.parseInt( args[2] ); 

            startRandomClient( host, port, test);
            
	        System.out.println("-----------------------------------");
	        System.out.println("Game finished.");
        }
        else if ( args[0].startsWith("h") ) {  // Human Player
            if (args.length < 3 ) {
                System.out.println( usage() );
                return;
            }
            String host = args[1];
            int    port = Integer.parseInt( args[2] ); 

            startHumanClient( host, port);
            
	        System.out.println("-----------------------------------");
	        System.out.println("Game finished.");
        }
        else {									// Server
            if ( args.length < 2 ) {
                System.out.println( usage() );
            }
            int    port = Integer.parseInt( args[1] ); 
          
                startServer( port ); 
        }
        
    }
}
