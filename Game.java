package jp.ac.tohoku.ecei.sf;

/**
   リバーシのゲームロジックを実装するクラス．

   このクラスはリバーシの1ゲームを行うstatic
   メソッド{@link #game(Player, Player)}を提供する．
 */
public class Game {

    /**
       リバーシのゲームを行う

       @param   p1 黒番プレイヤー (must not be {@code null}).
       @param   p2 白番プレイヤー (must not be {@code null}).
       @return  ゲームが終了した時点における盤面
     */
    public static ReversiBoard game( Player p1) {
        ReversiBoard board = new ReversiBoard();
        Player q1 = p1;
        int    c  = ReversiBoard.BLACK;
        // loop to process each move until EndGame
        while ( !board.isEndGame() && !board.getPlayerQuited()) {
                try {
                    Move m = q1.play( board, c );
                    if (m == null) {    // handle NOOP/QUIT message
                        if (board.getPlayerQuited() == true)
                            break;
                        else {
                            System.out.println(q1.getConnectionStatus());
                            continue;
                        }
                    }
                    else {
                        if (!m._isPassed)
                            board.move(m, c);

                        c = board.flipColor(c);
                        q1.sendToServer(board, c);

                        Move m2 = q1.receiveFromServer();
                        System.out.println("Opponent's move: " + m2.toString());
                        if (!m2._isPassed)
                            board.move(m2, c);

                        c = board.flipColor(c);
                    }
                }
                catch (ReversiBoard.IllegalMoveException e) {
                    board.print( c );
                    System.out.println( "Illegal move: " + e.getMove() );
                    return board;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return board;
                }
        }
        return board;
    }
}
