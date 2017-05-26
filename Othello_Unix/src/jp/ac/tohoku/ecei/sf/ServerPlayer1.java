package jp.ac.tohoku.ecei.sf;

import java.util.*;
import java.net.*;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;
import java.util.concurrent.*;

import jp.ac.tohoku.ecei.sf.ReversiBoard.IllegalMoveException;

/**
   ランダムプレイヤーの実装．このプレイヤーは合法手をランダムに指す．
 */
public class ServerPlayer1 implements Player {
    private final int MAX_THREAD = 20; 
    private final ServerSocket sock; 


    private final Random  rgen;
    private final boolean isQuiet;

    /* スレッドプール */
    private final ExecutorService pool;  

    ServerPlayer1( int port ) throws Exception {
        sock = new ServerSocket( port ); 
        this.pool = Executors.newFixedThreadPool( MAX_THREAD ); 
        this.rgen = new Random();
        this.isQuiet = false;
    }

   
    /**
       ワーカスレッド
     */
    class Worker implements Runnable {
        private final Socket conn; 
        public Worker( Socket s ) {
            conn = s; 
        } 

        private void interact( InputStream is, 
                               OutputStream os ) {
          Scanner s = new Scanner(is).useDelimiter("\\s+");
            try {
                while ( true ) {
                  char tmp = s.next().charAt(0);
                  if(tmp == 'Q') {
                	  System.out.println("Close connection.");
                	  break;
                  }
                  else if (tmp == 'N') {
                	  os.write( 0x4F ); // write 'O'
                  	  os.write( 0x4B ); // write 'K'
                  	  continue;
                  }
                  else {
                	  String bStr = s.next();
                      System.out.println("MOVE \n" + bStr);
                      int[][] bArr = new int[10][10];
                      // Create new Board from input String
                      for ( int i = 1; i <= 8; i++ ) {
                        for ( int j = 1; j <= 8; j++ ) {
                          int index = (j-1)+(i-1)*8;
                          if (bStr.charAt(index) == '-')
                            bArr[j][i] = 0;
                          else if (bStr.charAt(index) == 'O')
                            bArr[j][i] = 2;
                          else if (bStr.charAt(index) == 'X')
                            bArr[j][i] = 1;
                        }
                      }
                      ReversiBoard board = new ReversiBoard(bArr);
                      if (board.isEndGame()) {
                        os.write( 0x58 );
                        break;
                      }
                      String colorStr = s.next();
                      int color = (colorStr=="O") ? 1 : 2;

                      Move m = playAI(board, color);
                      m.writeTo(os);
                      String h = "\r\n";
                      byte[] b = h.getBytes();
                      os.write(b);
                  }
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
            } 
            catch ( NoSuchElementException n) {
            }
        }

        //@Override
        public void run() {
            try {
                final InputStream  is = conn.getInputStream();
                final OutputStream os = conn.getOutputStream();            
                interact( is, os );
                conn.close();
                System.out.println("Game finished");
            } 
            catch ( IOException e ) {
                try { 
                  conn.close(); }
                catch ( IOException ee ) {
                }
            }
        }
    }
    
    public void waitConnection() {
        try {
            while ( true ) {
                final Socket conn = sock.accept();
                System.out.println("Get connection: " + sock.getLocalSocketAddress());
                try {
                    pool.execute( new Worker( conn ) );
                }
                catch (Exception e) {
                    System.out.println("Something wrong happens: Shutdown the connection from " + conn + ".");
                    conn.close(); 
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            pool.shutdown();
        }
    }

    //@Override 
    public void close() throws IOException {
        sock.close();
    }

    public void sendToServer( ReversiBoard board, int color ) throws IOException {

    }   
    public Move receiveFromServer() throws IOException {
      return null;
    }
    public String getConnectionStatus() throws IOException {
      return "";
    }


       

    /**
       {@inheritDoc}

       このプレイヤーは合法手をランダムに選択する．
     */    
    public Move play ( ReversiBoard board, int color ) {
        List<Move> moves = board.legalMoves( color );
        if ( moves.isEmpty() ) {
            return new Move();
        }

        final int i = rgen.nextInt( moves.size() );
        final Move mv = moves.get(i);

        if ( !isQuiet ) {
            System.out.println("Random player played " + mv);
        }
        return mv;
    }
    /**
    {@inheritDoc}

    	Minimax
     */    
    public Move playAI ( ReversiBoard board, int color ) {
    	List<Move> moves = board.legalMoves( color );
        if ( moves.isEmpty() ) {
            return new Move();
        }
        Value maxVl = new Value(-10000, new Move());
        
        int stepsAhead = 4;
        int count = board.stoneCounts( ReversiBoard.BLACK ) + board.stoneCounts( ReversiBoard.WHITE );
        if (count>15 && count <47) {
        		stepsAhead = 4;
        }
        else if (count > 50) {
        	if (count > 55)
        		stepsAhead = 9;
        	else
        		stepsAhead = 6;	
        }
        
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			try {
				tBoard.move(mv, color);
				Value temp = minValue(tBoard, mv, stepsAhead, -100000, 100000, count);
				if (temp.getPoint() > maxVl.getPoint()) {
					maxVl = temp;
				}
			} catch (IllegalMoveException e) {
				e.printStackTrace();
			}
		}
		System.out.println(maxVl.getMove().toString());
		return maxVl.getMove();
    }
    public Value minValue (ReversiBoard board, Move move, int steps, int alpha, int beta, int stoneCount) throws IllegalMoveException {
		if (steps == 0) 
			if (stoneCount > 50)
				return new Value(board.utility(), move);
			else
				return new Value(board.utility(), move);
		int a = alpha;
		int b = beta;
		List<Move> moves = board.legalMoves( ReversiBoard.WHITE );
		if ( moves.isEmpty()) {
			if (board.isEndGame())
				if (stoneCount > 50)
					return new Value(board.utility(), move);
				else
					return new Value(board.utility(), move);
			else {
				ReversiBoard tBoard = new ReversiBoard(board.toArray());
				Value temp = maxValue(tBoard, move, steps-1, a, b, stoneCount);
				return temp;
			}
		}
		Value minVl = new Value(100000, move);
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			tBoard.move(mv, ReversiBoard.WHITE);
			Value temp = maxValue(tBoard, move, steps-1, a, b, stoneCount);
			if (temp.getPoint() < minVl.getPoint()) {
				minVl = temp;
				int mPoint = minVl.getPoint();
				if (mPoint <= a) return minVl;
				b = (b <= mPoint) ? b : mPoint;
			}
		}
		return minVl;
	}

	public Value maxValue (ReversiBoard board, Move move, int steps, int alpha, int beta, int stoneCount) throws IllegalMoveException {
		if (steps == 0)
			if (stoneCount > 50)
				return new Value(board.utility(), move);
			else
				return new Value(board.utility(), move);
		int a = alpha;
		int b = beta;
		List<Move> moves = board.legalMoves( ReversiBoard.BLACK );
		if ( moves.isEmpty()) {
			if (board.isEndGame())
				if (stoneCount > 50)
					return new Value(board.utility(), move);
				else
					return new Value(board.utility(), move);
			else {
				ReversiBoard tBoard = new ReversiBoard(board.toArray());
				Value temp = minValue(tBoard, move, steps-1, a, b, stoneCount);
				return temp;
			}
		}
		Value maxVl = new Value(-100000, move);
		for (Move mv : moves) {
			ReversiBoard tBoard = new ReversiBoard(board.toArray());
			tBoard.move(mv, ReversiBoard.BLACK);
			Value temp = minValue(tBoard, move, steps-1, a, b, stoneCount);
			if (temp.getPoint() > maxVl.getPoint()){
				maxVl = temp;
				int mPoint = maxVl.getPoint();
				if (mPoint >= b) return maxVl;
				a = (a >= mPoint) ? a : mPoint;
			}
		}
		return maxVl;
	}
}
