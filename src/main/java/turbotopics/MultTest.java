import java.util.*;

/**
 * Created by msamak on 3/10/16.
 */
public class MultTest {
    private int pvalue;
    private Map<Object,Object> perms;
    private int perm_hash = 10;
    private boolean use_perm;
    public MultTest(int pvalue, Boolean use_perm){
        this.pvalue = pvalue;
        this.perms = new HashMap<Object,Object>();
        this.use_perm = use_perm;
    }

    public MultTest(int pvalue, Boolean use_perm, Integer perm_hash){
        this(pvalue,use_perm);
        this.perm_hash = perm_hash;
    }

    private Map<Object,Object> score(int count, Map<Object,Object> marg, Map<Object,Object> bigram, int total, int min_count){
        Map<Object,Object> scores = new HashMap<Object,Object>();
        Integer n_u = count;
        Integer n = total;
        Integer n_nu = n - n_u;
        Double log_n_u = Math.log(n_u);
        Double log_n   = Math.log(n);
        for (Object v: bigram.keySet()){
            if ((Double)bigram.get(v) < min_count) {
                continue;
            }
            Integer n_v = (Integer)marg.get(v);
            Integer n_nv = n - n_v;
            Integer n_uv   = (Integer)bigram.get(v);
            Integer n_nuv  = n_v - n_uv;
            Integer n_unv  = n_u - n_uv;
            Integer n_nunv = n - n_u - (n_v - n_uv);
            Double val = 0.0;
            if (n_uv > 0)
                val +=  (n_uv) * (Math.log(n_uv) - log_n_u - Math.log(n_v) + log_n);
            if (n_nuv > 0)
                val +=  (n_nuv) * (Math.log(n_nuv/n) - Math.log(n_nu/n) - Math.log(n_v/n));
            if (n_unv > 0)
                val +=  (n_unv) * (Math.log(n_unv) - log_n_u - Math.log(n_nv) + log_n);
            if (n_nunv > 0)
                val +=  (n_nunv) * (Math.log(n_nunv/n) - Math.log(n_nu/n) - Math.log(n_nv/n));
            scores.put(v, 2 * val);
        }
        return scores;
    }

    private double mylog(double x) {
        return (x==0)?-1000000:Math.log(x);
    }

    private Double null_score_perm(int count, Map<Object,Object> marg, int total){
        Integer perm_key = (int)(count/perm_hash);
        if(perms.containsKey(perm_key)){
            return (Double)perms.get(perm_key);
        }
        double max_score = 0;
        int nperm = 1/pvalue;
        ArrayList<Object[]> table = new ArrayList<Object[]>();
        for(Object key: marg.keySet()){
            Object[] items = new Object[2];
            items[0] = key;
            items[1] = marg.get(key);
            table.add(items);
        }
        table.sort(new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                if ((Double)o1[1] - (Double)o2[1] == 0)
                    return 0;
                else if (((Double)o1[1] - (Double)o2[1]) > 0)
                    return 1;
                else
                    return -1;
            }
        });
        for(int perm=0; perm < nperm; perm++){
            Map<Object,Object> perm_bigram = Commons.sample_no_replace(total,table,count);
            Map<Object,Object> obs= score(count,marg,perm_bigram,total,1);
            Comparator<Object> comp = new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    if((Double)o1 -(Double)o2 == 0)
                        return 0;
                    else if((Double)o1 - (Double)o2 > 0)
                        return 1;
                    else
                        return -1;
                }
            };
            List<Object> obs_val_list = (List)obs.values();
            Collections.sort(obs_val_list,comp);
            Double obs_score = (Double)obs_val_list.get(0);
            if(obs_score > max_score || perm == 0){
                max_score = obs_score;
            }
        }
        perms.put(perm_key,max_score);
        return max_score;
    }

    private Double null_score_chi_sq(int count, Map<Object,Object>marg, int total){
        return (Double)Commons.get_chi_sq_table().get(pvalue);
    }

    public Double null_score(int count, Map<Object,Object>marg, int total){
        if(use_perm){
            return null_score_perm(count,marg,total);
        }else{
            return null_score_chi_sq(count,marg,total);
        }
    }





}

