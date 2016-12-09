package jp.ac.tohoku.ecei.sf;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;
public class ServerTest {



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


    public static void main( String[] args ) {
          
                startServer( 12345 ); 
        
        System.out.println("-----------------------------------");
        System.out.println("Game finished.");
    }

}
