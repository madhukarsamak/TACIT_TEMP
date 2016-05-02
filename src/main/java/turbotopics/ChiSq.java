import java.util.HashMap;
import java.util.Map;

/**
 * Created by msamak on 3/10/16.
 */
public class ChiSq {
    private Double pvalue;

    public ChiSq(Double pvalue){
        this.pvalue = pvalue;
    }

    /**
     * returns the chi_sq test scores
     */
    private Map<Object,Object> score(int count, Map<Object,Object> marg, Map<Object,Object> bigram, int total, int min_count){
        Map<Object,Object> scores = new HashMap<Object,Object>();
        for(Object w2 : bigram.keySet()){
            if((Integer)bigram.get(w2) < min_count){
                continue;
            }
            Double o_11 = (Double)bigram.get(w2);
            Double o_12 = (Double)marg.get(w2) - (Double)bigram.get(w2);
            Integer o_21 = count - (Integer)bigram.get(w2);
            Integer o_22 = total - count - (Integer)marg.get(w2);
            Double num = total * Math.pow(o_11 * o_22 - o_12 * o_21,2);
            Double den = (o_11 + o_12) * (o_11 + o_21) * (o_12 + o_22) * (o_21 + o_22);
            scores.put(w2,(num/den));
        }
        return scores;
    }

    /**
     returns the chi squared null score
     */
    private double null_score(int count, Map<Object,Object>marg, int total){
        return (Double) Turbotopics.get_chi_sq_table().get(pvalue);
    }
}
