import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.automata.transducers.impl.compact.CompactMealyTransition;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ProductMealy{

    CompactMealy<String, Word<String>> fsm;
    CompactMealy<String, Word<String>> mealy_1;
    CompactMealy<String, Word<String>> mealy_2;
    Collection<Integer> states_1;
    Collection<Integer> states_2;
    Alphabet<String> alphabet_1;
    Alphabet<String> alphabet_2;
    Alphabet<String> alphabet;
    CompactMealy<String, Word<String>> mealy;
    int states_map[][];
    Queue<Integer> states_queue;
    int components_count;
    int new_s_1;
    int new_s_2;

    Word<String> output_1;
    Word<String> output_2;
    @Nullable
    CompactMealyTransition<Word<String>> transition_1;
    @Nullable
    CompactMealyTransition<Word<String>> transition_2;
    int merged_state;

    public ProductMealy( CompactMealy<String, Word<String>> m1) {
        this.fsm = m1;
    }

    public ProductMealy mergeFSMs(CompactMealy<String, Word<String>> m2){
        initializeVariables(m2);
        generateTheMealy();
        setFirstState();

        return this;
    }

    private void setFirstState(){
        mealy.setInitialState(0);
        this.fsm = mealy;
    }

    private void initializeVariables(CompactMealy<String, Word<String>> m2){
        mealy_1 = fsm;
        mealy_2 = m2;
        states_1 = mealy_1.getStates();
        states_2 = mealy_2.getStates();

        alphabet_1 = mealy_1.getInputAlphabet();
        alphabet_2 = mealy_2.getInputAlphabet();

        // Creating the alphabet of the merged FSM
        alphabet = Alphabets.fromCollection(MergeAlphabet(alphabet_1, alphabet_2));
        mealy = new CompactMealy(alphabet);

        initializestatesNum();
        
        states_map[0][0] = states_map[0][1] = states_map[0][2] = 0;

        states_queue = new LinkedList<>();
        states_queue.add(0);
    }

    private void initializestatesNum(){
        int states_num = states_1.size() * states_2.size();
        states_map = new int[states_num][3];
        for (int[] array : states_map) {
            Arrays.fill(array, -1);
        }
    }

    private void generateTheMealy(){
        merged_state = 0;
        while (states_queue.size() != 0) {
            int current_state = states_queue.remove();
            mealy.addState();
            int s_1 = states_map[current_state][1];
            int s_2 = states_map[current_state][2];

            generateForEachAlph(current_state, s_1, s_2);
        }
    }

    private void generateForEachAlph(int current_state, int s_1, int s_2){
        for (String sigmai : alphabet) {
            initializeForEach();
            setSeperateTransition(sigmai, s_1,true);
            setSeperateTransition(sigmai, s_2,false);

            if (transition_1 != null || transition_2 != null) {
                mergeTransitions(sigmai, s_1, s_2, current_state);
            }

        }
    }

    private void mergeTransitions(String sigmai, int s_1, int s_2, int current_state){
        updateOutsAndStates(s_1, s_2);

        int equivalent_state = EquivalentState(new_s_1, new_s_2, states_map);

        String output_1_string = "";
        String output_2_string = "";
        if (output_1 != null) {
            output_1_string = output_1.toString();
        }
        if (output_2 != null) {
            output_2_string = output_2.toString();
        }
        List<String> output_1_list = new ArrayList<String>(Arrays.asList(output_1_string.split(",")));
        List<String> output_2_list = new ArrayList<String>(Arrays.asList(output_2_string.split(",")));
        List<String> output_list = new ArrayList<>();
        for (String string_1 : output_1_list) {
            if (!string_1.equals("")) {
                output_list.add(string_1);
            }
        }

        for (String string_1 : output_2_list) {
            if (!string_1.equals("") && !output_list.contains(string_1)) {
                output_list.add(string_1);
            }
        }

        String output_string = String.join(",", output_list);
        Word<String> output = Word.fromSymbols(output_string);

        if (equivalent_state == -1) {
            merged_state += 1;
            mealy.setTransition(current_state, sigmai, merged_state, output);
            states_queue.add(merged_state);
            states_map[merged_state][0] = merged_state;
            states_map[merged_state][1] = new_s_1;
            states_map[merged_state][2] = new_s_2;
        } else {
            mealy.setTransition(current_state, sigmai, equivalent_state, output);
        }
    }

    private void updateOutsAndStates(int s_1, int s_2){
        if (transition_1 != null && transition_2 != null) {
            output_1 = transition_1.getOutput();
            new_s_1 = transition_1.getSuccId();

            output_2 = transition_2.getOutput();
            new_s_2 = transition_2.getSuccId();
        }
        else if (transition_1 != null && transition_2 == null) {
            output_1 = transition_1.getOutput();
            new_s_1 = transition_1.getSuccId();

            new_s_2 = s_2;
        }
        else if (transition_1 == null && transition_2 != null) {
            new_s_1 = s_1;

            output_2 = transition_2.getOutput();
            new_s_2 = transition_2.getSuccId();
        }
    }

    private void initializeForEach(){
        new_s_1 = -10;
        new_s_2 = -10;

        output_1 = null;
        output_2 = null;

        transition_1 = null;
        transition_2 = null;
    }

    private void setSeperateTransition(String sigmai, int si, boolean isFirstAlph){
        if (isFirstAlph && alphabet_1.contains(sigmai)) {
            transition_1 = mealy_1.getTransition(si, sigmai);
        }
        else if (!isFirstAlph && alphabet_2.contains(sigmai)) {
            transition_2 = mealy_2.getTransition(si, sigmai);
        }
    }

    private Collection MergeAlphabet(Alphabet<String> a_1, Alphabet<String> a_2){
        // TODO Auto-generated method stub
        Set<String> set_1 = new LinkedHashSet<>();
        set_1.addAll(a_2);
        set_1.addAll(a_1);
        List<String> list_1 = new ArrayList<>(set_1);
        return list_1;
    }

    private static int EquivalentState ( int s1, int s2, int[][] states_map_1){
        // TODO Auto-generated method stub
        int length_1 = states_map_1.length;
        for (int i = 0; i < length_1; i++) {
            if (states_map_1[i][0] != (-1)) {
                if (states_map_1[i][1] == s1 && states_map_1[i][2] == s2) {
                    return i;
                }
            }
        }
        return -1;
    }


    public CompactMealy<String, Word<String>> getMachine () {
        return fsm;
    }

    public void setMachine (CompactMealy <String, Word<String>> machine){
        this.fsm = machine;
    }

    public int getComponents_count() {
        return components_count;
    }

    public void setComponents_count(int components_count) {
        this.components_count = components_count;
    }
}