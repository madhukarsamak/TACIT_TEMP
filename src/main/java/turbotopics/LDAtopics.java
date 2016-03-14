import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by msamak on 3/14/16.
 */
public class LDAtopics {
    String corpus;
    String assignments;
    String vocab;
    boolean use_perm = false;
    Double pvalue = 0.001;
    Integer min_count = 25;
    Integer ntopics;

    private ArrayList<String> read_vocab(String vocab_fname) throws Exception {
        ArrayList<String> terms = new ArrayList<String>();
        System.out.println("Reading vocabulary from "+vocab_fname);
        BufferedReader br = new BufferedReader(new FileReader(vocab_fname));
        String line = br.readLine();
        while(line != null){
            terms.add(line);
        }
        return terms;
    }

    private ArrayList<Map<Object,Object>> parse_word_assignments(String assigns_fname, ArrayList<String> vocab) throws Exception {
        ArrayList<Map<Object,Object>> results = new ArrayList<Map<Object,Object>>();
        BufferedReader br = new BufferedReader(new FileReader(assigns_fname));
        String line = br.readLine();
        while(line != null){
            Map<Object,Object> wordmap= new HashMap<Object,Object>();
            String[] mappings = line.split(" ");
            for(int i=1; i<mappings.length; i++){
                String[] termtopic = mappings[i].split(":");
                wordmap.put(vocab.get(Integer.parseInt(termtopic[0])),Integer.parseInt(termtopic[1]));
            }
            results.add(wordmap);
        }
        return results;
    }

    private void update_counts_from_topic(String doc, Map<Object,Object>topicmap, Integer topic, Counts counts_obj){
        for(Object item: topicmap.values()){
            if((Integer)item == topic){
                break;
            }
            return;
        }
        Function<String,Boolean> root_filter = new Function<String, Boolean>() {
            @Override
            public Boolean apply(String s) {
                int comp = -1;
                if(topicmap.containsKey(s.split(" ")[0])){
                    comp = (Integer)topicmap.get(s.split(" ")[0]);
                }
                return (comp == topic);
            }
        };
        counts_obj.update_counts(doc,root_filter);
    }

    private Counts turbo_topic(ArrayList<String> corpus, ArrayList<Map<Object,Object>> assigns, int topic, boolean use_perm, Double pvalue, int min){
        ArrayList<Object[]> iter_gen = new ArrayList<Object[]>();
        for(int i=0; i<corpus.size() && i<assigns.size(); i++){
            Object[] item = new Object[2];
            item[0] = corpus.get(i);
            item[1] = assigns.get(i);
            iter_gen.add(item);
        }
        BiConsumer<Counts,Object[]> update_fun = new BiConsumer<Counts, Object[]>() {
            @Override
            public void accept(Counts counts, Object[] objects) {
                update_counts_from_topic((String)objects[0],(Map<Object,Object>)objects[1],topic,counts);
            }
        };
        LikelihoodRatio test = new LikelihoodRatio(pvalue,use_perm);
        Counts cnts = Commons.nested_sig_bigrams(iter_gen,update_fun,test,min);
        return cnts;
    }


}
