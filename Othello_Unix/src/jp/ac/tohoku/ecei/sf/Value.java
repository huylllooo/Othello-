package jp.ac.tohoku.ecei.sf;

public class Value {
	private int point;
	private Move move;

	public Value(int pts, Move mv) {
		this.point = pts;
		this.move = mv;
	}
	
	public int getPoint() {
		return this.point;
	}
	
	public Move getMove() {
		return this.move;
	}
}
