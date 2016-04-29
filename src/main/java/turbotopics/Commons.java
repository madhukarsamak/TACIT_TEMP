import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by msamak on 3/7/16.
 */
public class Commons {
    private static Map<String,String> stop_words = null;
    public static Map<Object,Object> get_chi_sq_table(){
        Map<Object,Object> _chi_sq_table = new HashMap<Object,Object>();
        _chi_sq_table.put(0.1,2.70554345);
        _chi_sq_table.put(0.01,6.634897);
        _chi_sq_table.put(0.001,10.82757);
        _chi_sq_table.put(0.0001,15.13671);
        _chi_sq_table.put(0.00001,19.51142);
        _chi_sq_table.put(0.000001,23.92813);
        _chi_sq_table.put(0.0000001,28.37399);
        return _chi_sq_table;
    }

    public static String[] word_list(String doc, Map<Object,Object> vocab){
        doc = strip_text(doc);
        String[] singles = doc.split(" ");
        ArrayList<String> words = new ArrayList<String>();
        int pos = 0;
        while(pos < singles.length){
            String w = singles[pos];
            pos++;
            String word = w;
            if(!vocab.containsKey(w)){
                vocab.put(w,new HashMap<Object,Object>());
            }
            Object state = vocab.get(w);
            while((pos < singles.length) && ( state instanceof Map) && (((Map<Object,Object>) state).containsKey(singles[pos]))){
                state = ((Map<Object,Object>)state).get(singles[pos]);
                word = word + " " +singles[pos];
                pos = pos + 1;
            }
            words.add(word);
        }
        String[] result = new String[words.size()];
        return words.toArray(result);
    }

    public static String strip_text(String text){
        text = text.toLowerCase();
        text = text.replaceAll("_"," ");
        text = text.replaceAll("[^A-Za-z0-9 ]","");
        text = text.replaceAll("\\s+"," ");
        text = text.trim();
        return text;
    }

    public static Map<Object,Object> sample_no_replace(int total, ArrayList<Object[]>table, int nitems){
        Map<Object,Object> count = new HashMap<Object,Object>();
        for(Integer n: getSample(total,nitems)){
            String w = nth_item_from_table(n,table);
            if(!count.containsKey(w)){
                count.put(w,0);
            }
            count.put(w,(Integer)count.get(w)+1);
        }
        return count;
    }

    public static ArrayList<Integer> getSample(int range, int n){
        Set<Integer> randomNumbers = new HashSet<Integer>();
        Random random = new Random();
        while(randomNumbers.size() != n){
            randomNumbers.add(random.nextInt(range+1));
        }
        return new ArrayList<Integer>(randomNumbers);
    }

    public static String nth_item_from_table(int n, ArrayList<Object[]>table){
        double sum = 0;
        for(Object[] item: table){
            sum = sum + (Double)item[1];
            if(n < sum){
                return (String)item[0];
            }
        }
        System.out.println(n);
        assert (false);
        return null;
    }

    public static Map<String,String> getStopWords(){
        try {
            if (stop_words == null) {
                stop_words = new HashMap<String,String>();
                BufferedReader br = new BufferedReader(new FileReader("/Users/msamak/work/TurboTopics/src/main/java/turbotopics/stop_words"));
                String line = br.readLine();
                while (line != null) {
                    stop_words.put(line.trim(), "");
                    line = br.readLine();
                }
            }
        }catch(Exception e){
            System.out.println("Problem with file creation: "+e.getStackTrace());
            return null;
        }
        return stop_words;
    }

    public static void write_vocab(Map<Object,Object> v, String outname, Boolean incl_stop) throws Exception {
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
        Map<String,String> stop_words = getStopWords();
        for(Object item: items){
            Object[] itemArray = (Object[])item;
            if(incl_stop || stop_words == null || !stop_words.containsKey((String)itemArray[0])){
                //pw.print(itemArray[0].toString()+" "+itemArray[1]+"\n");
                pw.printf("%-25s %8.2f\n",itemArray[0],Float.parseFloat(itemArray[1].toString()));
            }
        }
        pw.close();
    }

    public static void write_vocab(Map<Object,Object> v, String outname)throws Exception{
        write_vocab(v,outname,false);
    }

    public static Counts nested_sig_bigrams(ArrayList<Object[]>iter_generator, BiConsumer<Counts,Object[]> update_fun, LikelihoodRatio sig_test, Integer min){
        System.out.println("computing initial counts\n");
        Counts counts = new Counts();
        ArrayList<String> terms = new ArrayList<String>();
        int ccc =0;
        for(Object[] doc: iter_generator){
            if (ccc == 32){
                int abcd = 0;
            }
            update_fun.accept(counts,doc);
            ccc++;
        }
        //ArrayList<Object> items = (ArrayList<Object>) counts.marg.values();
        ArrayList<Object> items = items(counts.marg);
        terms = getTerms(items,min);
        while(terms.size() > 0){
            Map<Object,Object> new_vocab = new HashMap<Object,Object>();
            sig_test.reset();
            System.out.println("analysing "+terms.size()+" terms");
            for(String v: terms){
                Map<Object,Object> sig_bigrams = counts.sig_bigrams(v, sig_test, min);
                new_vocab.putAll(sig_bigrams);
            }

            for(Object selected : new_vocab.keySet()) {
                System.out.println("bigram : "+selected);
                update_vocab((String)selected, counts.vocab);
            }
            counts.reset_counts();
            for(Object[] doc: iter_generator){
                update_fun.accept(counts,doc);
            }
            items = items(new_vocab);
            items.sort(new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    Object[] a1 = (Object[])o1;
                    Object[] a2 = (Object[])o2;
                    if ((Integer)a1[1] - (Integer)a2[1] == 0)
                        return 0;
                    else if (((Integer)a1[1] - (Integer)a2[1]) > 0)
                        return 1;
                    else
                        return -1;
                }
            });
            terms = new ArrayList<String>();
            for(Object item: items){
                Object[] arr = (Object[])item;
                if((Integer)arr[1] >= min) {
                    terms.add((String) arr[0]);
                }
            }
        }
        return counts;
    }

    public static ArrayList<String> getTerms(ArrayList<Object> items, int min){
        ArrayList<String> terms = new ArrayList<String>();
        items.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Object[]a1 = (Object[])o1;
                Object[]a2 = (Object[])o2;
                Integer v1 = (Integer) a1[1];
                Integer v2 = (Integer)a2[1];
                if(v1-v2== 0)
                    return 0;
                else if(v1-v2 > 0)
                    return 1;
                else
                    return -1;
            }
        });
        for(Object item: items){
            Object[] it = (Object[]) item;
            if((Integer)it[1] >= min){
                terms.add((String)it[0]);
            }
        }
        return terms;
    }

    public static Object deepCopy(Object obj){
        if(!(obj instanceof Map)){
            return obj;
        }
        Map<Object,Object> map = (Map<Object,Object>)obj;
        Map<Object,Object> newMap = new HashMap<Object,Object>();
        for(Map.Entry<Object,Object> pair: map.entrySet()){
            newMap.put(pair.getKey(),deepCopy(pair.getValue()));
        }
        return newMap;
    }

    public static void update_vocab(String word, Map<Object,Object>vocab){
        String[] words = word.split(" ");
        Map<Object,Object> mach = vocab;
        int i = 0;
        while (i < words.length){
            String w = words[i];
            if (!mach.containsKey(w)){
                mach.put(w,new HashMap<Object,Object>());
            }
            mach = (Map<Object,Object>)mach.get(w);
            i = i + 1;
        }
    }

    public static ArrayList<Object> items(Map<Object,Object>map){
        ArrayList<Object> items = new ArrayList<Object>();
        for(Map.Entry<Object,Object> pair: map.entrySet()){
            Object[] comps = new Object[2];
            comps[0] = pair.getKey();
            comps[1] = pair.getValue();
            items.add(comps);
        }
        return items;
    }


}
