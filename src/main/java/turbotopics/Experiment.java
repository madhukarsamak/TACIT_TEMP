import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by msamak on 3/7/16.
 */
public class Experiment {
    public static void main(String[] args){
        /*JSONObject a = new JSONObject();
        a.put("hello",new JSONObject());
        Object b = a.get("hello");
        if(b instanceof JSONObject){
            System.out.println("hello and how do you do ? ");
        }*/

        /*String[] s = new String[2];
        s[0] = "The City of New York, often called New York City or simply New York, is the most populous city in the United States.[1] Located at the southern tip of New York State, the city is the center of the New York metropolitan area, one of the most populous urban agglomerations in the world.[9][10] A global power city,[11] New York City exerts a significant impact upon commerce, finance, media, art, fashion, research, technology, education, and entertainment, its fast pace[12] defining the term New York minute.[13] Home to the headquarters of the United Nations,[14] New York is an important center for international diplomacy[15] and has been described as the cultural and financial capital of the world.[16][17][18][19][20][21]";
        s[1] = "Situated on one of the world's largest natural harbors,[22][23] New York City consists of five boroughs, each of which is a separate county of New York State.[24] The five boroughs – Brooklyn, Queens, Manhattan, the Bronx, and Staten Island – were consolidated into a single city in 1898.[25] With a census-estimated 2014 population of 8,491,079[1][26] distributed over a land area of just 305 square miles (790 km2),[27] New York is the most densely populated major city in the United States.[28] The city and its metropolitan area constitute the premier gateway for legal immigration to the United States,[29][30][31] and as many as 800 languages are spoken in New York,[32][33][34] making it the most linguistically diverse city in the world.[33][35][36] By 2014 census estimates, the New York City metropolitan region remains by a significant margin the most populous in the United States, as defined by both the Metropolitan Statistical Area (20.1 million residents)[5] and the Combined Statistical Area (23.6 million residents).[6] In 2013, the MSA produced a gross metropolitan product (GMP) of nearly US$1.39 trillion,[37] while in 2012, the CSA[38] generated a GMP of over US$1.55 trillion, both ranking first nationally by a wide margin and behind the GDP of only twelve and eleven countries, respectively.[39]";
        System.out.println(s.toString());
        JSONObject vocab = new JSONObject();
        System.out.println(Commons.word_list(s[0],vocab).toString());
        System.out.println(Commons.word_list(s[1],vocab).toString());
        System.out.println(vocab.toString());*/

        JSONObject marg = new JSONObject();
        marg.put("a",0);
        marg.put("b",(Integer)marg.get("a")+1);
        System.out.println(marg.toString());

        Map<Object,Object> a = new HashMap<Object,Object>();
        Map<Object,Object> b = new HashMap<Object,Object>();
        a.put("a",20);
        a.put("b",30);
        b.put("c",40);
        b.put("b",50);
        for(Object o: a.keySet()){
            if(b.containsKey("a")){
                System.out.print((String)o +" "+(Integer)b.get(o));
            }
        }


        Map<Object,Object> m = new HashMap<Object,Object>();
        Map<Object,Object> m1 = new HashMap<Object,Object>();
        m1.put("madhukar",124);
        m1.put(123,123);
        m1.put(999.111,"sudhakar");
        m.put(345.678,m1);


        Map<Object,Object> n = m;//(Map<Object,Object>)Commons.deepCopy(m);

        m1.put("madhukar",125);
        m1.put(123,124);
        m1.put(999.111,"sudhakar samak");
        m.put(345.678,m1);

        System.out.println(((Map<Object,Object>)n.get(345.678)).get("madhukar").toString());

        Map<String,String> bb = new HashMap<String,String>();
        bb.put("a","madhukar");
        bb.put("b","sudhakar");

        Map<String,String> cc = new HashMap<String,String>();
        cc.put("a","madhukar1");
        cc.put("b1","sudhakar1");

        bb.putAll(cc);

        for(String s: bb.keySet()){
            System.out.println(s);
        }
        System.out.println(bb.get("a"));







    }
}
