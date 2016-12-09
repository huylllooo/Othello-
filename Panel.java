package jp.ac.tohoku.ecei.sf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

public class Panel extends JPanel{
	private double boxW;
	private double boxH;
	private double cx =0, cy = 0;
	boolean black = true;
	int[][] chessBoard = new int[10][10];
	public Panel(int[][] board) {
		super();
		this.chessBoard = board;
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
					repaint();
				}
			}
			public void mouseReleased(MouseEvent e) {
			}
		});
	}
	
	public void paint(Graphics g) {
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
		
		// Draw moves
		g.setColor(Color.black);
		// calculate click position
		int x = (int) (Math.floor((r.width * cx-boxW/2)/boxW));
		int y = (int) (Math.floor((r.height * cy-boxH/2)/boxH));
		// save click position
		if ( x<8 && y<8
		   && x>=0 && y >=0) 
			if (chessBoard[x][y] == 0)
				if (black == true) {
					chessBoard[x][y] = 1;
					black = false;
				} else {
					chessBoard[x][y] = 2;
					black = true;
				}
		// draw all moves */
		for (int i = 0; i<10; i++) 
			for (int j = 0; j<10; j++) {
				if (chessBoard[i][j] == 1)
					g.fillOval((int)(i * boxW + boxW/2), (int)(j * boxH + boxH/2),(int) (boxW),(int) (boxH));
				else if (chessBoard[i][j] == 2)
					g.drawOval((int)(i * boxW + boxW/2), (int)(j * boxH + boxH/2),(int) (boxW),(int) (boxH));
			}
	}
}

