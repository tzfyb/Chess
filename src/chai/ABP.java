package chai;

import java.util.HashMap;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class ABP implements ChessAI {
	private int max_depth;
	private int curPlayer;
	protected HashMap<Long, TransValue> transTable = new HashMap<>();
	

	public ABP() {
		max_depth = 3;
	}

	public ABP(int md) {
		max_depth = md;
	}

	public class MoveVal {
		protected short move;
		protected int val;

		public MoveVal() {
			move = 0;
			val = 0;
		}

		public MoveVal(short m, int v) {
			move = m;
			val = v;
		}

		public void setVal(int v) {
			val = v;
		}

		public void setMove(short m) {
			move = m;
		}
	}

	public class TransValue {
		protected int val;
		protected int depth;
		protected short move;

		TransValue(int v, int d, int m) {
			val = v;
			d = depth;
			m = move;
		}
	}

	@Override
	public short getMove(Position position) throws IllegalMoveException {
		// TODO Auto-generated method stub
		curPlayer = position.getToPlay();
		short res = miniMaxIDS(position, max_depth);
		return res;
	}
	
	private short miniMaxIDS(Position position, int maxDepth) throws IllegalMoveException{
		MoveVal bestMove = new MoveVal();
		for(int i = maxDepth - 1; i >= 0; i--)
			bestMove = maxMove(position, i, Integer.MIN_VALUE, Integer.MAX_VALUE);
		return bestMove.move;
	}

	private MoveVal maxMove(Position position, int depth, int alpha, int beta) throws IllegalMoveException {
		MoveVal bestMove = new MoveVal((short) 0, Integer.MIN_VALUE);
		if (depth == max_depth || position.isTerminal()) {
			bestMove.setMove(position.getLastShortMove());
			bestMove.setVal(utility(position));
			// System.out.println(Integer.toString(bestMove.val));
		} else {
			for (short move : position.getAllMoves()) {
				position.doMove(move);
				int val = (minMove(position, depth + 1, alpha, beta)).val;
				if (bestMove.val < val) {
					bestMove.setVal(val);
					bestMove.setMove(move);
				}
				position.undoMove();

				alpha = bestMove.val;

				if (alpha >= beta) {
					bestMove.setVal(beta);
					break;
				}
			}
		}
		return bestMove;
	}

	private MoveVal minMove(Position position, int depth, int alpha, int beta) throws IllegalMoveException {
		MoveVal bestMove = new MoveVal((short) 0, Integer.MAX_VALUE);
		if (depth == max_depth || position.isTerminal()) {
			bestMove.setMove(position.getLastShortMove());
			bestMove.setVal(utility(position));
			// System.out.println(Integer.toString(bestMove.val));
		} else {
			for (short move : position.getAllMoves()) {
				position.doMove(move);
				int val = (maxMove(position, depth + 1, alpha, beta)).val;
				if (bestMove.val > val) {
					bestMove.setMove(move);
					bestMove.setVal(val);
				}
				position.undoMove();
				
				beta = bestMove.val;
				if(beta <= alpha){
					bestMove.setVal(alpha);
					break;
				}
			}
		}
		return bestMove;
	}

	private int utility(Position position) {
		if (position.isTerminal() && position.isMate()) {
			if (position.getToPlay() == curPlayer)
				return Integer.MIN_VALUE;
			else
				return Integer.MAX_VALUE;
		} else if (position.isTerminal())
			return 0;
		else {
			int value = position.getMaterial() + (int) position.getDomination();
			if (curPlayer == position.getToPlay())
				return value;
			else
				return -value;
		}
	}
}
