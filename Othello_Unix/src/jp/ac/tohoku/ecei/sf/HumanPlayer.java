package jp.ac.tohoku.ecei.sf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jp.ac.tohoku.ecei.sf.ReversiBoard.IllegalMoveException;

public class HumanPlayer extends JFrame implements Runnable {
	private ReversiBoard board;
	private final Socket sock;
	private Scanner input;
	private final OutputStream os;
	private boolean myturn;
	
	private UIPanel boardPanel; 
	
	public HumanPlayer( String host, int port ) throws Exception {
		this (new Socket(host, port));
	}
	
	public HumanPlayer( Socket sock ) throws Exception {
		this.sock = sock;
		this.os = sock.getOutputStream();
		
		this.board = new ReversiBoard();
		myturn = true;
		// set up Panel for board
		boardPanel = new UIPanel();
		add( boardPanel, BorderLayout.CENTER );
		
		pack();
		setVisible(true);

		startHumanClient();
	}
	
	public void startHumanClient() {
		try {
			input = new Scanner( this.sock.getInputStream() ).useDelimiter("\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ExecutorService worker = Executors.newFixedThreadPool( 1 );
		worker.execute( this );
	}
	
	public void run() {
		while (true) {
			if (input.hasNext()) {
				String serverMove = input.next();
				processMessage( serverMove );
			}
		}
	}
	
	private void processMessage( String message ) {
        char c0 =  message.charAt(0);
        if (c0 == 'X')
            myturn = true;
        else {
        	char c1 = message.charAt(1);
        	Move move = new Move((c0 - 'A') + 1, (c1 - '1') + 1 );
        	updateFromServer(move);
			myturn = true;
			repaint();
        }
	}
	
	public void sendToServer(ReversiBoard board, int color ) throws IOException {

        String h = "MOVE ";
        byte[] b = h.getBytes();
        os.write(b);
        board.writeTo(this.os);

        h = " ";
        b = h.getBytes();
        os.write(b);

        byte b2 = board.color2Byte(color);

        os.write(b2);

        h = "\r\n";
        b = h.getBytes();
        os.write(b);
        os.flush();
    } 
	
	private void updateBoard(final Move mv) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						try {
							board.move(mv, ReversiBoard.BLACK);
							repaint();
							sendToServer(board, ReversiBoard.WHITE);
						} catch (IllegalMoveException e0) {
							e0.printStackTrace();
						} catch (IOException e1) {
							// for sendToServer
							e1.printStackTrace();
						}
					}
				}
		);	
	}
	
	private void updateFromServer(final Move mv) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						try {
							board.move(mv, ReversiBoard.WHITE);
							repaint();
							if (board.isEndGame())
								try {
									sock.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
						} catch (IllegalMoveException e0) {
							e0.printStackTrace();
						}
					}
				}
		);	
	}
	
	private class UIPanel extends JPanel{
		private double boxW;
		private double boxH;
		private double cx =0, cy = 0;
		private int[][] chessBoard;
		
		public UIPanel() {
			super();
			this.setPreferredSize(new Dimension(440, 440));
			// add mouse listener
			this.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
				}
				public void mouseEntered(MouseEvent e) {
				}			
				public void mouseExited(MouseEvent e) {
				}
				
				public void mousePressed(MouseEvent e) {
					Point mp = e.getPoint();
					Rectangle r = getBounds();
									
					if (r.width != 0 && r.height != 0) {
						cx = (double) mp.x/ (double) r.width;
						cy = (double) mp.y / (double) r.height;
						// translate click position to Move
						int x = (int) (Math.floor((r.width * cx-boxW/2)/boxW)) + 1;
						int y = (int) (Math.floor((r.height * cy-boxH/2)/boxH)) + 1;
						Move mv = new Move (x,y);
						if (myturn) {
							if ( board.isLegalMove(mv, ReversiBoard.BLACK) ) {
								updateBoard(mv);
								myturn = false;
								repaint();
							}
						}
					}
				}
				public void mouseReleased(MouseEvent e) {
				}
			});
		}
		
		public void paint(Graphics g) {
			chessBoard = board.toArray().clone();
			// Clear drawing area
			Rectangle r = this.getBounds();
			boxW = r.width/9;
			boxH = r.height/9;
			g.setColor(Color.green);
			g.fillRect(r.x, r.y, r.width, r.height);
		
			// Draw tables
			g.setColor(Color.black);
			// vertical lines
			for (int i = 0; i< 9; i++) {
				g.drawLine((int) (boxW*(i+0.5)), (int) (boxH*0.5), (int) (boxW*(i+0.5)), (int) (boxH*8.5));
			}
			// horizontal lines
			for (int i = 0; i< 9; i++) {
				g.drawLine((int) (boxW*0.5), (int) (boxH*(i+0.5)), (int) (boxW*8.5), (int) (boxH*(i+0.5)));
			}
			
			// draw all moves */
			for (int i = 1; i<=8; i++) 
				for (int j = 1; j<=8; j++) {
					if (chessBoard[i][j] == 1) {
						g.setColor(Color.black);
						g.fillOval((int)((i-1) * boxW + boxW*0.55), (int)((j-1) * boxH + boxH*0.55),(int) (boxW*0.9),(int) (boxH*0.9));
						g.drawOval((int)((i-1) * boxW + boxW*0.55), (int)((j-1) * boxH + boxH*0.55),(int) (boxW*0.9),(int) (boxH*0.9));
					}
					else if (chessBoard[i][j] == 2) {
						g.setColor(Color.white);
						g.fillOval((int)((i-1) * boxW + boxW*0.55), (int)((j-1) * boxH + boxH*0.55),(int) (boxW*0.9),(int) (boxH*0.9));
						g.setColor(Color.black);
						g.drawOval((int)((i-1) * boxW + boxW*0.55), (int)((j-1) * boxH + boxH*0.55),(int) (boxW*0.9),(int) (boxH*0.9));
					}
				}
		}
	}
}