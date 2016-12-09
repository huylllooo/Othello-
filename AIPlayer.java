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
        //board.draw();
        //Move clicked = board.draw();
        //System.out.print(clicked.toString());
        //return clicked;
        int stepsAhead = 5;
        int count = board.stoneCounts( ReversiBoard.BLACK ) + board.stoneCounts( ReversiBoard.WHITE );
        if (count>13 && count <47) {
        		stepsAhead = 3;
        }
        Value minVl = new Value(10000, new Move());
        List<Move> moves = board.legalMoves( color );
        if ( moves.isEmpty() ) {
            return new Move();
        }
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			try {
				tBoard.move(mv, color);
				Value temp = maxValue(tBoard, mv, stepsAhead, -100000, 100000);
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

	public Value minValue (ReversiBoard board, Move move, int steps, int alpha, int beta) throws IllegalMoveException {
		if (steps == 0)
			return new Value(board.utility(), move);
		List<Move> moves = board.legalMoves( ReversiBoard.WHITE );
		if ( moves.isEmpty()) {
			return new Value(board.utility(), move);
		}
		Value minVl = new Value(100000, move);
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			tBoard.move(mv, ReversiBoard.WHITE);
			Value temp = maxValue(tBoard, move, steps-1, alpha, beta);
			if (temp.getPoint() < minVl.getPoint()) {
				minVl = temp;
				int mPoint = minVl.getPoint();
				if (mPoint <= alpha) return minVl;
				beta = (beta <= mPoint) ? beta : mPoint;
			}
		}
		return minVl;
	}

	public Value maxValue (ReversiBoard board, Move move, int steps, int alpha, int beta) throws IllegalMoveException {
		if (steps == 0)
			return new Value(board.utility(), move);
		List<Move> moves = board.legalMoves( ReversiBoard.BLACK );
		if ( moves.isEmpty()) {
			return new Value(board.utility(), move);
		}
		Value maxVl = new Value(-100000, move);
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			tBoard.move(mv, ReversiBoard.BLACK);
			Value temp = minValue(tBoard, move, steps-1, alpha, beta);
			if (temp.getPoint() > maxVl.getPoint()){
				maxVl = temp;
				int mPoint = maxVl.getPoint();
				if (mPoint >= beta) return maxVl;
				alpha = (alpha >= mPoint) ? alpha : mPoint;
			}
		}
		return maxVl;
	}

    public void close() throws IOException {
        sock.close();
    }
}
