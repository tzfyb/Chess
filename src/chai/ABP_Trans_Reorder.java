package chai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class ABP_Trans_Reorder implements ChessAI{

	private int max_depth;
	private int curPlayer;
	protected HashMap<Long, TransValue> transTable = new HashMap<>();

	public ABP_Trans_Reorder() {
		max_depth = 3;
	}

	public ABP_Trans_Reorder(int md) {
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
		//return (maxMove(position, 0, Integer.MIN_VALUE, Integer.MAX_VALUE)).move;
		return res;
	}
	
	private short miniMaxIDS(Position position, int maxDepth) throws IllegalMoveException{
		MoveVal bestMove = new MoveVal();
		for(int i = 1; i <= maxDepth; i++){
			bestMove = maxMove(position, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, i);
			//System.out.println("bestMove " + Integer.toString(i) + " Depth Finish!");
		}
		return bestMove.move;
	}

	private MoveVal maxMove(Position position, int depth, int alpha, int beta, int curMaxDepth) throws IllegalMoveException {
		MoveVal bestMove = new MoveVal((short) 0, Integer.MIN_VALUE);
		if (depth == curMaxDepth || position.isTerminal()) {
			bestMove.setMove(position.getLastShortMove());
			bestMove.setVal(utility(position));
			// System.out.println(Integer.toString(bestMove.val));
		} else {
			ArrayList<MoveVal> sortedMoves = sortMoves(position, true);
			for (int i = sortedMoves.size() - 1; i >= 0; i--) {
				short move = sortedMoves.get(i).move;
				position.doMove(move);
				if (transTable.containsKey(position.getHashCode())
						&& (transTable.get(position.getHashCode())).depth >= curMaxDepth) {
					System.out.println("Max Contain Fonud" + Integer.toString(depth));
					TransValue tv = transTable.get(position.getHashCode());
					bestMove.setMove(move);
					bestMove.setVal(tv.val);
				} else {
					int val = (minMove(position, depth + 1, alpha, beta, curMaxDepth)).val;
					if (bestMove.val < val) {
						bestMove.setVal(val);
						bestMove.setMove(move);
					}
					
					
					transTable.put(position.getHashCode(), new TransValue(val, curMaxDepth, move));
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

	private MoveVal minMove(Position position, int depth, int alpha, int beta, int curMaxDepth) throws IllegalMoveException {
		MoveVal bestMove = new MoveVal((short) 0, Integer.MAX_VALUE);
		if (depth == curMaxDepth || position.isTerminal()) {
			bestMove.setMove(position.getLastShortMove());
			bestMove.setVal(utility(position));
			// System.out.println(Integer.toString(bestMove.val));
		} else {
			ArrayList<MoveVal> sortedMoves = sortMoves(position, false);
			for (int i = 0; i < sortedMoves.size(); i++) {
				short move = sortedMoves.get(i).move;
				position.doMove(move);
				if (transTable.containsKey(position.getHashCode())
						&& (transTable.get(position.getHashCode())).depth >= curMaxDepth) {
					System.out.println("Min Contain Fonud" + Integer.toString(depth));
					TransValue tv = transTable.get(position.getHashCode());
					bestMove.setMove(move);
					bestMove.setVal(tv.val);
				} else {
					int val = (maxMove(position, depth + 1, alpha, beta, curMaxDepth)).val;
					if (bestMove.val > val) {
						bestMove.setMove(move);
						bestMove.setVal(val);
					}
					transTable.put(position.getHashCode(), new TransValue(val, curMaxDepth, move));
				}
				position.undoMove();

				beta = bestMove.val;
				if (beta <= alpha) {
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
	
	private ArrayList<MoveVal> sortMoves(Position position, boolean turn) throws IllegalMoveException{
		ArrayList<MoveVal> res = new ArrayList<MoveVal>();
		MoveVal curMove = null;
		for(short move : position.getAllMoves()){
			position.doMove(move);
			if(transTable.containsKey(position.getHashCode()))
				curMove = new MoveVal(move, transTable.get(position.getHashCode()).val);
			else
				curMove = new MoveVal(move, turn ? Integer.MIN_VALUE : Integer.MAX_VALUE);
			position.undoMove();
			res.add(curMove);
		}
		
		Collections.sort(res, new Comparator<MoveVal>(){
			@Override
			public int compare(MoveVal m1, MoveVal m2){
				return (int) (Math.signum(m1.val - m2.val));
			}
		});
		
		return res;
	}
	
}
