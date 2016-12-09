package jp.ac.tohoku.ecei.sf;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;

import java.util.*;
import java.util.concurrent.*;

/**
   人間プレイヤーの実装．このプレイヤーは標準入出力を利用して手をユーザに問い合わせる．  
 */
public class RemotePlayer implements Player {

    private final Socket sock; 
    private final InputStream  is; 
    private final OutputStream os; 

    // RandomPlayer Test
    private final Random  rgen;
    private final boolean isQuiet;

    public RemotePlayer( String host, int port ) throws Exception {
        this ( new Socket( host, port ) );
    }
    
    public RemotePlayer( Socket sock ) throws Exception {
        this.sock = sock;
        this.is   = sock.getInputStream();
        this.os   = sock.getOutputStream();
        // RandomPlayer Test
        this.rgen = new Random();
        this.isQuiet = false;

    }
    
    public void sendToServer(ReversiBoard board, int color ) throws IOException {
        
        String h = "MOVE ";
        byte[] b = h.getBytes();
        this.os.write(b); 
        board.writeTo(this.os);

        h = " ";
        b = h.getBytes();
        this.os.write(b); 

        byte b2 = board.color2Byte(color);

        this.os.write(b2); 

        h = "\r\n";
        b = h.getBytes();
        this.os.write(b); 
        this.os.flush();
    }

    public Move receiveFromServer()  throws IOException{
        byte[] a = new byte[10];
        this.is.read(a);
        char c0 = (char) a[0];
        if (c0 == 'X')
            return new Move();
        
        char c1 = (char) a[1];
        if (c1 == 0) {
            this.is.read(a);
            c1 = (char) a[0];
        }
        Move move = new Move( (c1 - '1') + 1, (c0 - 'A') + 1);
        return move;
    }

    public String getConnectionStatus()  throws IOException{
        byte[] a = new byte[10];
        int n = this.is.read(a);
        char c0 = (char) a[0];
        char c1 = (char) a[1];
        return String.valueOf(c0)+String.valueOf(c1);
    }

    public synchronized Move play ( ReversiBoard board, int color ) throws IOException {
        board.print( color );
        board.closeCanvas();
        board.draw();
        if ( !board.isPlayable( color ) ) {
            System.out.println( "Your turn is skipped because you have no move to play." );
            return new Move();
        }

        List<Move> moves = board.legalMoves( color );
        if ( moves.isEmpty() ) {
            return new Move();
        }
        final int i = rgen.nextInt( moves.size() );
        final Move mv = moves.get(i);

        if ( !isQuiet ) {
            System.out.println("P1 player played " + mv);
        }
        return mv; // */
       
        // Read Move from client
        /*final BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );

        System.out.println( "Your turn." );
        
        String line;
        try {
            while ( true ) {
                System.out.print( (color == ReversiBoard.WHITE ) ? "White?> " : "Black?> " );
                System.out.flush();

                line = br.readLine();
                if ( line == null ) {
                    System.exit(1); 
                }
                if ( line.length() < 2 ) {
                    System.out.println( "Error: input two characters" );                    
                    System.out.println( "Syntax: [A-H][1-8]" );
                    continue;
                } else if (line.length() == 4) { 
                    byte[] b = line.getBytes();
                    this.os.write(b); 
                    String h = "\r\n";
                    b = h.getBytes();
                    this.os.write(b); 
                    this.os.flush();
                    if (line.charAt(0) == 'Q')
                        board.setPlayerQuited();
                    return null;
                }

                char c0 = line.charAt(0);
                char c1 = line.charAt(1);
                Move move = new Move( (c1 - '1') + 1, (c0 - 'A') + 1);
                if ( board.isLegalMove( move, color ) ) {
                    return move;
                }
                else {
                    System.out.println( "Invalid move." );
                    System.out.println( "Syntax: [A-H][1-8] | QUIT | NOOP" );
                    continue;
                }
            }            
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(1);
        }
        return null; // unreachable */
    }

    public void close() throws IOException {
        sock.close();
    }
}
