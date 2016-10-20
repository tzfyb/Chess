package chai;

import sun.launcher.resources.launcher;

/**
 * Created by JackGuan on 2/18/14.
 */
public class Config {
    static int IDS_DEPTH = 5;
    static double[] IDS_DEPTHS = {3., 3.};
    static int NMH_R = 2;
    static short[] last_moves = {0, 0, 0, 0};
    static Double[][] last_time = {{0.95, 0.95, 0.95},{0.95, 0.95, 0.95}};
    static int repeat_pointer = 0;
    static int time_pointer = 0;
    static short repeat_cnt = 0;

    public static void tryBreakTie(int turn, short move) {
        if (turn == 0) {
            last_moves[repeat_pointer] = move;
            repeat_pointer = (repeat_pointer + 1) % 4;
            if (last_moves[0] == last_moves[2]
                    && last_moves[1] == last_moves[3]) {
                IDS_DEPTH++;
                IDS_DEPTHS[0]++;
                IDS_DEPTHS[1]++;
                repeat_cnt++;
//                System.out.println("breaking tie: " + IDS_DEPTH);
            } else if (repeat_cnt != 0) {
                IDS_DEPTH -= repeat_cnt;
                IDS_DEPTHS[0] -= repeat_cnt;
                IDS_DEPTHS[1] -= repeat_cnt;
                repeat_cnt = 0;
//                System.out.println("tie solved: " + IDS_DEPTH);
            }
        }
    }

    public static void tuneDepth(Double timeSec, int turn){
        if(repeat_cnt != 0) return;
        Double mean = 0.;
        last_time[turn][(time_pointer++) % last_time.length] = timeSec;
        for(int i = 0; i < last_time[turn].length; i++){
            mean += last_time[turn][i];
        }
        mean /= last_time[turn].length;
        if(mean <= .5){
            IDS_DEPTHS[turn]+=1;
        }else if(mean <= 1.5){
            IDS_DEPTHS[turn]+=0.25;
        }
        if(mean >= 10){
            IDS_DEPTHS[turn]-=2;
        }else if(mean >= 3){
            IDS_DEPTHS[turn]-=1;
        }else if(mean >= 1.5){
            IDS_DEPTHS[turn]-=0.25;
        }
    }
}