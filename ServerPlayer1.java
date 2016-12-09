package jp.ac.tohoku.ecei.sf;

import java.util.*;
import java.net.*;
import java.io.OutputStream; 
import java.io.InputStream;
import java.io.Closeable; 
import java.io.IOException;
import java.util.concurrent.*;

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
       （うるさい）ランダムプレイヤーを作成する．

       うるさいランダムプレイヤーは{@link #play(ReversiBoard,int)}時に
       着手後の版面と選んだ手の情報を標準出力に出力する．
     
    public ServerPlayer1() {
        this( false, new Random() );
    };

    /**
       静けさフラグを与えて，ランダムプレイヤーを作成する

       静かなランダムプレイヤーは{@link #play(ReversiBoard,int)}時に標準出力に出力を行わない．
       @param isQuiet もし{@code true}ならばメッセージ出力を抑制する
     
    public ServerPlayer1( boolean isQuiet ) {
        this( isQuiet, new Random() );
    }

    /**
       乱数生成器を与えて，（うるさい）ランダムプレイヤーを作成する．
       @param rgen 乱数生成器
     
    public ServerPlayer1( Random rgen ) {
        this( false, rgen );
    }
    
    /**
       より一般的な{@link RandomPlayer}のコンストラクタ
       @param isQuiet もし{@code true}ならばメッセージ出力を抑制する
       @param rgen    乱数生成器
    
    public ServerPlayer1( boolean isQuiet, Random rgen ) {
        this.isQuiet = isQuiet;
        this.rgen    = rgen;
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
                  
                  if(s.next().charAt(0) == 'Q')
                    break;

                  String bStr = s.next();
                  System.out.println(bStr);
                  int[][] bArr = new int[10][10];
                  // Create new Board from input String
                  for ( int i = 1; i <= 8; i++ ) {
                    for ( int j = 1; j <= 8; j++ ) {
                      int index = (i-1)+(j-1)*8;
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

                  Move m = play(board, color);
                  m.writeTo(os);
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

     final int i = rgen.nextInt( moves.size() );
     final Move mv = moves.get(i);

     if ( !isQuiet ) {
         System.out.println("Random player played " + mv);
     }
     return mv;
 }
}
