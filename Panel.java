package jp.ac.tohoku.ecei.sf;

import java.awt.Color;
import java.awt.Component;
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
	private boolean isClicked = false;
	private int[][] chessBoard = new int[10][10];
	
	public boolean isClicked() {
		return this.isClicked;
	}
	public void setClick() {
		this.isClicked = true;
	}
	
	public void resetClick() {
		this.isClicked = false;
	}
	
	public Panel(int[][] board) {
		super();
		for (int i=0; i<10; i++)
			for (int j = 0; j<10; j++)
				this.chessBoard[j][i] = board[i][j];
		this.setPreferredSize(new Dimension(440, 440));
		
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
		// save click position
		if ( x<9 && y<9
		   && x>=0 && y >=0 
		   && chessBoard[x][y] != 2) {
			setClick(); // need to update with accuracy 
			chessBoard[x][y] = 1;
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

