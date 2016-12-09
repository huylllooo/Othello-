package jp.ac.tohoku.ecei.sf;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;

public class ClientTest {


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

            startClient( "127.0.0.1", 12345);
       
        
        System.out.println("-----------------------------------");
        System.out.println("Game finished.");
    }

}
