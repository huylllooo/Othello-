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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
   人間プレイヤーの実装．このプレイヤーは標準入出力を利用して手をユーザに問い合わせる．
 */
public class RandomPlayer implements Player {

    private final Socket sock;
    private final InputStream  is;
    private final OutputStream os;

    // GUI test
    private JTextField idField; // textfield to display player's mark
    private JTextArea displayArea; // JTextArea to display output
    private JPanel boardPanel; // panel for tic-tac-toe board
    private JPanel panel2; // panel to hold board
    //private Square board[][]; // tic-tac-toe board
    //private Square currentSquare; // current square
    private Socket connection; // connection to server
    private Scanner input; // input from server
    private Formatter output; // output to server
    private String ticTacToeHost; // host name for server
    private String myMark; // this client's mark


    // RandomPlayer Test
    private final Random  rgen;
    private final boolean isQuiet;

    public RandomPlayer( String host, int port ) throws Exception {
        this ( new Socket( host, port ) );
    }

    public RandomPlayer( Socket sock ) throws Exception {
        this.sock = sock;
        this.is   = sock.getInputStream();
        this.os   = sock.getOutputStream();
        //this.gui  = new BoardGUI();
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

    public Move receiveFromServer()  {
    	Scanner input = new Scanner( this.is ).useDelimiter("\r\n");
			String serverMove = input.next();
			char c0 =  serverMove.charAt(0);
	        if (c0 == 'X')
	            return new Move();
	        else {
	        	char c1 = serverMove.charAt(1);
	        	Move move = new Move((c0 - 'A') + 1, (c1 - '1') + 1 );
	            return move;
	        }
    }

    public String getConnectionStatus()  throws IOException{
        byte[] a = new byte[10];
        this.is.read(a);
        char c0 = (char) a[0];
        this.is.read(a);
        char c1 = (char) a[0];
        return String.valueOf(c0)+String.valueOf(c1);
    }

    public synchronized Move play ( ReversiBoard board, int color ) throws IOException {
        board.print( color );
        if ( !board.isPlayable( color ) ) {
            System.out.println( "Your turn is skipped because you have no move to play." );
            return new Move();
        }
        if (!board.isDrawn())
        	board.draw();
        else
        	board.updateCanvas();

        List<Move> moves = board.legalMoves( color );
        if ( moves.isEmpty() ) {
            return new Move();
        }
        final int i = rgen.nextInt( moves.size() );
        final Move mv = moves.get(i);

        if ( !isQuiet ) {
            System.out.println("P1 player played " + mv);
        }
        return mv;
    }

    public void close() throws IOException {
        sock.close();
    }
}
