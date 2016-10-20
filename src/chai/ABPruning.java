package chai;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by JackGuan on 2/17/14.
 */
public class ABPruning implements ChessAI {
    public static boolean MAX_TURN = true, MIN_TURN = false;
    public static int MATE = (int) (Integer.MAX_VALUE * 0.9), BE_MATED = (int) (Integer.MIN_VALUE * 0.9);
    protected boolean terminalFound;

    public class MoveValuePair implements Comparable<MoveValuePair> {
        public short move = 0;
        public int eval;

        MoveValuePair() {
        }

        MoveValuePair setGetVal(int e) {
            eval = e;
            return this;
        }

        MoveValuePair(short m, int e) {
            move = m;
            eval = e;
        }

        public void updateMinMax(short m, int e, boolean findMax) {
            if (move == 0 || (findMax && (this.eval < e)) || (!findMax && (this.eval > e))) {
                this.move = m;
                this.eval = e;
            }
        }

        @Override
        public int compareTo(MoveValuePair o) {
            // default sort, ascending
            return (int) Math.signum(eval - o.eval);
        }
    }

    @Override
    public short getMove(Position position) throws IllegalMoveException {
        long start = System.currentTimeMillis();
        short result = minimaxIDS(position, (int) Config.IDS_DEPTHS[position.getToPlay()]);
//        short result = minimaxIDS(position, Config.IDS_DEPTH);
        //short result = minimaxIDS(position, 5);
        long elapsedTime = System.currentTimeMillis() - start;
        try {
            FileOutputStream timecompete = new FileOutputStream("timecompete.txt", true);
            timecompete.write((elapsedTime / 1000. + "\t").getBytes());
            timecompete.close();
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException : " + ex);
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
        }
        System.out.println("ABP  making move " + elapsedTime / 1000. + "\t");
        Config.tryBreakTie(position.getToPlay(), result);
        Config.tuneDepth(elapsedTime / 1000., position.getToPlay());
        return result;
    }

    protected short minimaxIDS(Position position, int maxDepth) throws IllegalMoveException {
        this.terminalFound = false;
        MoveValuePair bestMove = new MoveValuePair();
        for (int d = 1; d <= maxDepth && !this.terminalFound; d++) {
            bestMove = ABMaxMinValue(position, maxDepth - 1, BE_MATED, MATE, MAX_TURN);
        }
        return bestMove.move;
    }

    protected MoveValuePair ABMaxMinValue(Position position, int depth, int alpha, int beta, boolean maxTurn) throws IllegalMoveException {
        if (depth <= 0 || position.isTerminal()) {
            return handleTerminal(position, maxTurn);
        } else {
            MoveValuePair bestMove = new MoveValuePair();
            for (short move : position.getAllMoves()) {
                // collect values from further moves
                position.doMove(move);
                MoveValuePair childMove = ABMaxMinValue(position, depth - 1, alpha, beta, !maxTurn);
                bestMove.updateMinMax(move, childMove.eval, maxTurn);
                position.undoMove();
                // update the alpha beta boundary
                alpha = maxTurn ? bestMove.eval : alpha;
                beta = !maxTurn ? bestMove.eval : beta;
                // prune the subtree if needed
                if(alpha >= beta)
                    return maxTurn ? bestMove.setGetVal(beta) : bestMove.setGetVal(alpha);
            }
            return bestMove;
        }
    }

    protected MoveValuePair handleTerminal(Position position, boolean maxTurn) {
        MoveValuePair finalMove = new MoveValuePair();
        if (position.isTerminal() && position.isMate()) {
            this.terminalFound = position.isTerminal();
            finalMove.eval = (maxTurn ? BE_MATED : MATE);
        } else if (position.isTerminal() && position.isStaleMate())
            finalMove.eval = 0;
        else {
            finalMove.eval = (int) ( (maxTurn ? 1 : -1) * (position.getMaterial() + position.getDomination()));
        }
//        System.out.print(finalMove.eval + "\t");
        return finalMove;
    }
}
