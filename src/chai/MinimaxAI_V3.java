package chai;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;
import java.util.Random;
class MinimaxAI_V3 implements ChessAI {

    private static int MAX_DEPTH = 6;
    private int count = 0;
    private int currentPlayer;
    @Override
    public short getMove(Position position) throws IllegalMoveException {
        count = 0;
        currentPlayer = position.getToPlay();
        short move = (short) minimax_decision(position);
        //System.out.println(count);
        return move;
    }

    private int minimax_decision(Position position) throws IllegalMoveException{
        return max_value(position, 1)[0];
    }

    private int[] max_value(Position position, int depth) throws IllegalMoveException{
        count++;
        int[] best = {0, -10000};
        if (cutoff_test(depth) || terminal_test(position)) {
            best[0] = position.getLastShortMove();
            best[1] = utility_function(position);
        }
        else {
            short[] moves = position.getAllMoves();
            for (short move: moves) {
                position.doMove(move);
                int[] value = min_value(position, depth + 1);
                if (best[1] < value[1]){
                    best[1] = value[1];
                    best[0] = move;
                }
                position.undoMove();
            }
        }
        //if(depth == 1)
        //System.out.println(depth + ": " + best[1]);
        return best;
    }

    private int[] min_value(Position position, int depth) throws IllegalMoveException{
        count++;
        int[] best = {0, 10000};
        if (cutoff_test(depth) || terminal_test(position)) {
            best[0] = position.getLastShortMove();
            best[1] = utility_function(position);
        }
        else {
            short[] moves = position.getAllMoves();
            for (short move: moves) {
                position.doMove(move);
                int value[] = max_value(position, depth + 1);
                if (best[1] > value[1]){
                    best[1] = value[1];
                    best[0] = move;
                }
                position.undoMove();
            }
        }
        //System.out.println(depth + ": " + best[1]);
        return best;
    }

    private boolean terminal_test(Position position)
    {
        if (position.isTerminal())
            return true;
        return false;
    }


    private boolean cutoff_test(int depth){
        if(depth == MAX_DEPTH)
            return true;
        return false;
    }

    private int utility_function(Position position){
        if(position.isTerminal() && position.isMate()) {
            if(position.getToPlay() == currentPlayer)
                return Integer.MIN_VALUE;
            else
                return Integer.MAX_VALUE;
        }
        else if (position.isTerminal())
            return 0;
        else {
            int value = position.getMaterial();
            if (currentPlayer == position.getToPlay())
                return value;
            else
                return -value;
        }
    }
}