package chai;

import java.util.Random;

import chesspresso.position.Position;

public class RandomAI implements ChessAI {
	public short getMove(Position position) {
		short [] moves = position.getAllMoves();
		short move = moves[new Random(2016).nextInt(moves.length)];
	
		return move;
	}
}
