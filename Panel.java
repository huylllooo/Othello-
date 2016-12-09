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
	private int row=0, col=0;
	private boolean clicked = false;
	boolean black = true;
	int[][] chessBoard = new int[10][10];
	
	public int getRow() {
		return this.row;
	}
	public int getCol() {
		return this.col;
	}
	public boolean isClicked() {
		return this.clicked;
	}
	protected void setClicked() {
		this.clicked = true;
	}
	public Panel(int[][] board) {
		super();
		this.chessBoard = board;
		this.setPreferredSize(new Dimension(440, 440));
		// add mouse listener
		this.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}
			
			public void mousePressed(MouseEvent e) {
				Point mp = e.getPoint();
				Rectangle r = getBounds();
								
				if (r.width != 0 && r.height != 0) {
					cx = (double) mp.x/ (double) r.width;
					cy = (double) mp.y / (double) r.height;
					repaint();
				}
				setClicked();
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
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
		
		// calculate click position
		int x = (int) (Math.floor((r.width * cx-boxW/2)/boxW)) + 1;
		int y = (int) (Math.floor((r.height * cy-boxH/2)/boxH)) + 1;
		this.row = x;
		this.col = y;
		// save click position
		if ( x<9 && y<9
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

