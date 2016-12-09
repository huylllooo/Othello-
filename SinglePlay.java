package jp.ac.tohoku.ecei.sf;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;
/**
   メインクラス
 */
public class SinglePlay {

    /**
        Usage - under construction
    */
    private static String usage() {
        return ("Usage: java -jar echo.jar (client HOST PORT|server PORT|single PORT)");
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

    private static void startClient( String host, int port ) {
        RemotePlayer c = null;
        try {
            c = new RemotePlayer( host, port ); 
            ReversiBoard b;
            b = Game.game( c );
            b.print();
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
        if ( args.length == 0 ) {
            System.out.println( usage() ); 
            return; 
        }
        if ( args[0].startsWith("c") ) {
            if (args.length < 3 ) {
                System.out.println( usage() );
                return;
            }
            String host = args[1];
            int    port = Integer.parseInt( args[2] ); 

            startClient( host, port);
        }
        else {
            if ( args.length < 2 ) {
                System.out.println( usage() );
            }
            int    port = Integer.parseInt( args[1] ); 
          
                startServer( port ); 
        }
        
        System.out.println("-----------------------------------");
        System.out.println("Game finished.");
    }
}
