import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by msamak on 5/1/16.
 */

/**
 * This is the main class that generates NGrams for a given corpus.
 * Recursively finds collocations for a given corpus.  writes out
 * the marginal counts to a specified file
 *
 * --corpus: This is a single file which has a list of lines. Each line is a line of text from every file in the corpus
 *
 * --out: The output path in which the file containing the NGrams and marginal counts will be generated
 *
 * --pre: The prefix for the output file
 *
 * --pvalue: a value of type Double called the precision value
 *
 * --use_perm: "true" if likelihood ratio score should be generated using permutations
 *
 * --min_count: minimum count of word occurrences to be considered
 *
 * command to run the file
 * Enter the top level directoruy and type
 * javac -cp src/main/java/turbotopics/ src/main/java/turbotopics/ComputeNGrams.java
 * java -cp src/main/java/turbotopics/ src/main/java/turbotopics/ComputeNGrams <corpus_file> <pvalue> <use_permutaion> <output_path> <min_count> <prefix>
 *
 * Once this runs, It generates the output in the location specified with the given prefix
 */
public class ComputeNGrams {
    String corpus;
    boolean use_perm = false;
    Double pvalue = 0.001;
    Integer min_count = 25;
    String out;
    Integer min_bigram_count = 5;
    Integer min_char_count = 3;
    String prefix = "ngram";

    ComputeNGrams(String corpus, Double pvalue, Boolean use_perm, String out, Integer min_count, String prefix){
        this.corpus = corpus;
        this.pvalue = pvalue;
        this.use_perm = use_perm;
        this.out = out;
        this.min_count = min_count;
        this.prefix = prefix;
    }
    public Counts generateNGrams() throws Exception{
        System.out.println("computing n-grams from "+this.corpus);
        ArrayList<String> corpus = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(this.corpus));
        String line = br.readLine();
        while(line != null){
            corpus.add(line);
            line = br.readLine();
        }
        //set up recursive hypothesis tests
        LikelihoodRatio lr = new LikelihoodRatio(this.pvalue,this.use_perm);


        ArrayList<Object[]> iter_gen = new ArrayList<Object[]>();
        for(int i=0; i<corpus.size(); i++){
            Object[] item = new Object[1];
            item[0] = corpus.get(i);
            iter_gen.add(item);
        }
        Function<String,Boolean> my_filter = new Function<String, Boolean>() {
            @Override
            public Boolean apply(String s) {
                return Turbotopics.getCharFilter(min_char_count).apply(s) &&
                       Turbotopics.getStopFilter().apply(s) &&
                       Turbotopics.getDigitFilter().apply(s);
            }
        };
        BiConsumer<Counts,Object[]> update_fun = new BiConsumer<Counts, Object[]>() {
            @Override
            public void accept(Counts counts, Object[] objects) {
                update_counts_from_topic((String)objects[0],Turbotopics.getStopFilter(),counts);
            }
        };

        // compute significant n-grams
        Counts cnts = Turbotopics.nested_sig_bigrams(iter_gen,update_fun,lr,min_count);

        System.out.println("writing to "+this.out);

        //write n-grams to file
        if(this.out.charAt(this.out.length()-1) == '/'){
            this.out = this.out.substring(0,this.out.length()-2);
        }
        writeOuptput(cnts.marg,this.out+"/"+this.prefix+".txt");
        return cnts;
    }

    private void writeOuptput(Map<Object,Object> v, String outname) throws Exception {
        PrintWriter pw = new PrintWriter(outname);
        ArrayList<Object> items =  items(v);
        items.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Object[] a1 = (Object[])o1;
                Object[] a2 = (Object[])o2;
                if ((Integer)a1[1] - (Integer)a2[1] == 0)
                    return 0;
                else if (((Integer)a1[1] - (Integer)a2[1]) < 0)
                    return 1;
                else
                    return -1;
            }
        });
        for(Object item: items){
            Object[] itemArray = (Object[])item;
            pw.printf("%s|%d\n",itemArray[0],(int)Float.parseFloat(itemArray[1].toString()));
        }
        pw.close();
    }

    private ArrayList<Object> items(Map<Object,Object>map){
        ArrayList<Object> items = new ArrayList<Object>();
        for(Map.Entry<Object,Object> pair: map.entrySet()){
            Object[] comps = new Object[2];
            comps[0] = pair.getKey();
            comps[1] = pair.getValue();
            items.add(comps);
        }
        return items;
    }

    private void update_counts_from_topic(String doc, Function<String,Boolean> root_filter, Counts counts_obj){
        counts_obj.update_counts(doc,root_filter);
    }

    public static void main(String[] args)throws Exception{
        Boolean use_perm = false;
        if(args[2].equals("true")){
            use_perm = true;
        }
        ComputeNGrams ngrams = new ComputeNGrams(args[0],Double.parseDouble(args[1]),use_perm,args[3],Integer.parseInt(args[4]),args[5]);
        ngrams.generateNGrams();
    }
}
