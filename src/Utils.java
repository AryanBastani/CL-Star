

import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.automata.transducers.impl.compact.CompactMealyTransition;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.WordBuilder;
import net.automatalib.words.impl.Alphabets;


public class Utils {
    private static Utils instance;

    private static String BenchmarksDir = "Benchmarks/";
    public static Utils getInstance() {
        if(instance == null){
            Utils.instance = new Utils();
        }
        return instance;
    }
    public CompactDFA<String> loadDFA(File f) throws Exception {

        Pattern kissLine = Pattern.compile("\\s*(\\S+)\\s+--\\s+(\\S+)\\s+->\\s+(\\S+)\\s*");
        Pattern pattern = Pattern.compile("[qQ]\\s*(\\d+)\\s*");

        BufferedReader br = new BufferedReader(new FileReader(f));

        List<String[]> trs = new ArrayList<String[]>();

        HashSet<String> abcSet = new HashSet<>();
        List<String> abc = new ArrayList<>();
        List<Integer> final_states = new ArrayList<>();

        //		int count = 0;

        while (br.ready()) {
            String line = br.readLine();
            Matcher m = kissLine.matcher(line);
            Matcher m2 = pattern.matcher(line);
            if (m.matches()) {
//                System.out.println(m.group(0));
//                System.out.println(m.group(1));
//                System.out.println(m.group(2));
//                System.out.println(m.group(3));
//                System.out.println(m.group(4));

                String[] tr = new String[3];
                tr[0] = m.group(1);
                tr[1] = m.group(2);
                if (!abcSet.contains(tr[1])) {
                    abcSet.add(tr[1]);
                    abc.add(tr[1]);
                }
                tr[2] = m.group(3);
                trs.add(tr);
            }
            //			count++;
            else if (m2.matches()){
//                System.out.println(m2.group(0));
//                System.out.println(m2.group(1));
                String stateId = m2.group(1);
                final_states.add(Integer.parseInt(stateId));

            }
        }

        br.close();

        Collections.sort(abc);
        Alphabet<String> alphabet = Alphabets.fromCollection(abc);
        CompactDFA<String> dfa = new CompactDFA<String>(alphabet);


        Map<String,Integer> states = new HashMap<String,Integer>();
        Integer si=null,sf=null;
        Integer s0 = null;

        for (String[] tr : trs) {
            if(!states.containsKey(tr[0])) states.put(tr[0], dfa.addState());
            if(!states.containsKey(tr[2])) states.put(tr[2], dfa.addState());

            si = states.get(tr[0]);
            if(s0==null) s0 = si;
            sf = states.get(tr[2]);


            dfa.addTransition(si, tr[1], sf, null);
        }

        for (Integer st : dfa.getStates()) {
            for (String in : alphabet) {
                if(dfa.getTransition(st, in)==null){
                    dfa.addTransition(st, in, st, null);
                }
            }
        }

        for(int state_id : final_states){
            dfa.setAccepting(state_id, true);
        }

        dfa.setInitialState(s0);

        return dfa;

    }

    public CompactDFA<String> loadDFA(File f, String num) throws Exception {

        Pattern kissLine = Pattern.compile("\\s*(\\S+)\\s+--\\s+(\\S+)\\s+->\\s+(\\S+)\\s*");
        Pattern pattern = Pattern.compile("[qQ]\\s*(\\d+)\\s*");

        BufferedReader br = new BufferedReader(new FileReader(f));

        List<String[]> trs = new ArrayList<String[]>();

        HashSet<String> abcSet = new HashSet<>();
        List<String> abc = new ArrayList<>();
        List<Integer> final_states = new ArrayList<>();

        //		int count = 0;

        while (br.ready()) {
            String line = br.readLine();
            Matcher m = kissLine.matcher(line);
            Matcher m2 = pattern.matcher(line);
            if (m.matches()) {
//                System.out.println(m.group(0));
//                System.out.println(m.group(1));
//                System.out.println(m.group(2));
//                System.out.println(m.group(3));
//                System.out.println(m.group(4));

                String[] tr = new String[3];
                tr[0] = m.group(1);
                tr[1] = "c" + num + "_" + m.group(2);
                if (!abcSet.contains(tr[1])) {
                    abcSet.add(tr[1]);
                    abc.add(tr[1]);
                }
                tr[2] = m.group(3);
                trs.add(tr);
            }
            //			count++;
            else if (m2.matches()){
//                System.out.println(m2.group(0));
//                System.out.println(m2.group(1));
                String stateId = m2.group(1);
                final_states.add(Integer.parseInt(stateId));

            }
        }

        br.close();

        Collections.sort(abc);
        Alphabet<String> alphabet = Alphabets.fromCollection(abc);
        CompactDFA<String> dfa = new CompactDFA<String>(alphabet);


        Map<String,Integer> states = new HashMap<String,Integer>();
        Integer si=null,sf=null;
        Integer s0 = null;

        for (String[] tr : trs) {
            if(!states.containsKey(tr[0])) states.put(tr[0], dfa.addState());
            if(!states.containsKey(tr[2])) states.put(tr[2], dfa.addState());

            si = states.get(tr[0]);
            if(s0==null) s0 = si;
            sf = states.get(tr[2]);


            dfa.addTransition(si, tr[1], sf, null);
        }

        for (Integer st : dfa.getStates()) {
            for (String in : alphabet) {
                if(dfa.getTransition(st, in)==null){
                    dfa.addTransition(st, in, st, null);
                }
            }
        }

        for(int state_id : final_states){
            dfa.setAccepting(state_id, true);
        }

        dfa.setInitialState(s0);

        return dfa;

    }

    public ProductDFA<String> loadProductDFA(File f, String dir) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        ProductDFA<String> productDFA = null;
        int comp_num = 1;
//        Pattern kissLine = Pattern.compile("\\s*(\\S+)\\s+--\\s+(\\S+)\\s+->\\s+(\\S+)\\s*.txt");
        while (br.ready()) {
            String line = br.readLine();
            line = BenchmarksDir + dir + line;
            File file = new File(line);
            CompactDFA component = loadDFA(file, Integer.toString(comp_num));
//            Visualization.visualize(component, component.getInputAlphabet());
            if (productDFA == null) productDFA = new ProductDFA<>(component);
            else productDFA.interleaving_parallel_composition(component);
            comp_num++;
        }
        return productDFA;
    }
}
