package chai;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

public class Minimax implements ChessAI{
	private int max_depth;
	private int curPlayer;
	long timeSpend;
	
	public Minimax(){
		max_depth = 3;
		timeSpend = 0;
	}
	
	public Minimax(int md){
		max_depth = md;
		timeSpend = 0;
	}
	
	public class MoveVal{
		private short move;
		private int val;
		
		public MoveVal(){
			move = 0; val = 0;
		}
		public MoveVal(short m, int v){
			move = m; val = v;
		}
		public void setVal(int v){val = v;}
		public void setMove(short m){move = m;}
	}
	
	@Override
	public short getMove(Position position) throws IllegalMoveException{
		// TODO Auto-generated method stub
		curPlayer = position.getToPlay();
		long startTime = System.currentTimeMillis();
		short res = miniMaxIDS(position, max_depth);
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		timeSpend += elapsedTime;
		try {
            FileOutputStream fos = new FileOutputStream("Minimax_Log.txt", true);
            fos.write((Long.toString(elapsedTime) + "\n").getBytes());
            fos.write(System.getProperty("line.separator").getBytes());
            fos.close();
        } catch (FileNotFoundException fnfe) {
            System.out.println("FileNotFoundException : " + fnfe);
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
        }
		System.out.println("Minimax time spend " + Long.toString(elapsedTime) + "ms");
		return res;
	}
	
	private short miniMaxIDS(Position position, int maxDepth) throws IllegalMoveException{
		MoveVal bestMove = new MoveVal();
		for(int i = maxDepth - 1; i >= 0; i--)
			bestMove = maxMove(position, i);
		String[] player = {"White", "Black"};
		if(bestMove.val == Integer.MAX_VALUE){
			System.out.println(player[curPlayer] + " Wins!");
			System.out.println("Time Spent: " + Long.toString(timeSpend));
		}
		else if(bestMove.val == Integer.MIN_VALUE){
			System.out.println(player[(curPlayer + 1) % 2] + " Wins!");
			System.out.println("Time Spent: " + Long.toString(timeSpend));
		}
		else if(bestMove.val == 0){
			System.out.println("Draw!");
			System.out.println("Time Spent: " + Long.toString(timeSpend));
		}
		return bestMove.move;
	}
	
	private MoveVal maxMove(Position position, int depth) throws IllegalMoveException{
		MoveVal bestMove = new MoveVal((short) 0, Integer.MIN_VALUE);
		if(depth == max_depth || position.isTerminal()){
			bestMove.setMove(position.getLastShortMove());
			bestMove.setVal(utility(position));
			//System.out.println(Integer.toString(bestMove.val));
		}
		else{
			for(short move : position.getAllMoves()){
				position.doMove(move);
				int val = (minMove(position, depth + 1)).val;
				if(bestMove.val < val){
					bestMove.setVal(val);
					bestMove.setMove(move);
				}
				position.undoMove();
			}
		}
		return bestMove;	
	}
	
	private MoveVal minMove(Position position, int depth) throws IllegalMoveException{
		MoveVal bestMove = new MoveVal((short)0, Integer.MAX_VALUE);
		if(depth == max_depth || position.isTerminal()){
			bestMove.setMove(position.getLastShortMove());
			bestMove.setVal(utility(position));
		}
		else{
			for(short move : position.getAllMoves()){
				position.doMove(move);
				int val = (maxMove(position, depth + 1)).val;
				if(bestMove.val > val){
					bestMove.setMove(move);
					bestMove.setVal(val);
				}
				position.undoMove();
			}
		}
		return bestMove;
	}
	
	private int utility(Position position){
		if(position.isTerminal() && position.isMate()){
			if(position.getToPlay() == curPlayer)
				return Integer.MIN_VALUE;
			else return Integer.MAX_VALUE;
		}
		else if(position.isTerminal()) return 0;
		else{
			int value = position.getMaterial() + (int)position.getDomination();
			if(curPlayer == position.getToPlay())
				return value;
			else return -value;
		}
	}
}
