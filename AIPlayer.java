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

import jp.ac.tohoku.ecei.sf.ReversiBoard.IllegalMoveException;

/**
   人間プレイヤーの実装．このプレイヤーは標準入出力を利用して手をユーザに問い合わせる．  
 */
public class AIPlayer implements Player {

    private final Socket sock; 
    private final InputStream  is; 
    private final OutputStream os; 

    // RandomPlayer Test
    private final Random  rgen;
    private final boolean isQuiet;

    public AIPlayer( String host, int port ) throws Exception {
        this ( new Socket( host, port ) );
    }
    
    public AIPlayer( Socket sock ) throws Exception {
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
        //board.closeCanvas();
        if ( !board.isPlayable( color ) ) {
            System.out.println( "Your turn is skipped because you have no move to play." );
            return new Move();
        }
        //board.draw();
        //Move clicked = board.draw();
        //System.out.print(clicked.toString());
        //return clicked;
        Value minVl = new Value(10000, new Move());
        Move minMove = new Move();
        List<Move> moves = board.legalMoves( color );
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			try {
				tBoard.move(mv, color);
				Value temp = maxValue(tBoard, mv, 3);
				if (temp.getPoint() < minVl.getPoint()) {
					minVl = temp;
				}
			} catch (IllegalMoveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(minVl.getMove().toString());
		return minVl.getMove();
    }

	public Value minValue (ReversiBoard board, Move move, int steps) throws IllegalMoveException {

		List<Move> moves = board.legalMoves( ReversiBoard.WHITE );
		if ( moves.isEmpty() || steps == 0) {
			return new Value(board.utility(), move);
		}
		Value minVl = new Value(10000, move);
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			tBoard.move(mv, ReversiBoard.WHITE);
			Value temp = maxValue(tBoard, move, steps-1);
			if (temp.getPoint() < minVl.getPoint())
				minVl = temp;
		}
		return minVl;
	}

	public Value maxValue (ReversiBoard board, Move move, int steps) throws IllegalMoveException {
		List<Move> moves = board.legalMoves( ReversiBoard.BLACK );
		if ( moves.isEmpty() || steps == 0) {
			return new Value(board.utility(), move);
		}
		Value maxVl = new Value(-10000, move);
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			tBoard.move(mv, ReversiBoard.BLACK);
			Value temp = minValue(tBoard, move, steps-1);
			if (temp.getPoint() > maxVl.getPoint())
				maxVl = temp;
		}
		return maxVl;
	}

    public void close() throws IOException {
        sock.close();
    }
}
