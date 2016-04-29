import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.Buffer;
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
    String out;

    private ArrayList<String> read_vocab() throws Exception {
        String vocab_fname = vocab;
        ArrayList<String> terms = new ArrayList<String>();
        System.out.println("Reading vocabulary from "+vocab_fname);
        BufferedReader br = new BufferedReader(new FileReader(vocab_fname));
        String line = br.readLine();
        while(line != null){
            terms.add(line);
            line = br.readLine();
        }
        return terms;
    }

    private ArrayList<Map<Object,Object>> parse_word_assignments(ArrayList<String> vocab) throws Exception {
        String assigns_fname = this.assignments;
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
            line = br.readLine();
        }
        return results;
    }

    private void update_counts_from_topic(String doc, Map<Object,Object>topicmap, Integer topic, Counts counts_obj){
        boolean topicFound = false;
        for(Object item: topicmap.values()){
            if((Integer)item == topic){
                topicFound = true;
                break;
            }
        }
        if(!topicFound) return;
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

    LDAtopics(String corpus, String assignments, String vocab, String out, Integer ntopics, Integer min_count, Double pvalue, Boolean use_perm){
        this.corpus = corpus;
        this.assignments = assignments;
        this.vocab = vocab;
        this.ntopics = ntopics;
        this.out = out;
        if(min_count != null){
            this.min_count = min_count;
        }
        if(pvalue != null){
            this.pvalue = pvalue;
        }
        if(use_perm != null){
            this.use_perm = use_perm;
        }

    }

    public void setCorpus(String corups){
        this.corpus = corups;
    }

    public void setAssignments(String assignments){
        this.assignments = assignments;
    }

    public void setVocab(String vocab){
        this.vocab = vocab;
    }

    public void setNtopics(int ntopics){
        this.ntopics = ntopics;
    }


    public void generateTurboTopics() throws Exception{
        ArrayList<String> vocab = read_vocab();
        ArrayList<Map<Object,Object>> assigns = parse_word_assignments(vocab);
        ArrayList<String> corpus = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(this.corpus));
        String line = br.readLine();
        while(line != null){
            corpus.add(line);
            line = br.readLine();
        }
        for(int topic=0; topic<this.ntopics; topic++){
            System.out.println("'writing topic "+topic);
            Counts sig_bigrams = turbo_topic(corpus,assigns,topic,this.use_perm,this.pvalue,this.min_count);
            Commons.write_vocab(sig_bigrams.marg,this.out+"topic"+topic+".txt");
        }
    }

    public static void main(String[] args)throws Exception{
        LDAtopics lda = new LDAtopics(args[0],args[1],args[2],args[3],Integer.parseInt(args[4]),null,null,null);
        lda.generateTurboTopics();
    }



}
