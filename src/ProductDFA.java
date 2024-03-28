import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.util.automata.builders.AutomatonBuilders;
import net.automatalib.util.automata.builders.DFABuilder;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;
import net.automatalib.words.impl.ListAlphabet;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDFA<I> {
    private CompactDFA<I> dfa;
    private CompactDFA<I> dfa2;
    private Object Arrays;
    private Map<String,Integer> states;
    private Integer sf;
    String initial_label;
    private CompactDFA<I> productDFA;
    private I[] sigma1;
    private I[] sigma2;

    public ProductDFA( CompactDFA<I> dfa1) {
        this.dfa = dfa1;
    }

    public ProductDFA<I> interleaving_parallel_composition(CompactDFA<I> dfa2){
        initializeVars(dfa2);
        generateProductDFA();
        setFirstState();
        return this;
    }

    public CompactDFA<I> getDfa() {
        return dfa;
    }

    public void setDfa(CompactDFA<I> dfa) {
        this.dfa = dfa;
    }

    private void addNewTransition(I action, Integer s1, Integer s2, Integer si, boolean sigma1loop){
        Integer tgt = this.dfa.getTransition(s1, action);
        String tgt_label = Integer.toString(tgt) + "_" + Integer.toString(s2);
        if(!sigma1loop){
            tgt = this.dfa2.getTransition(s2, action);
            tgt_label = Integer.toString(s1) + "_" + Integer.toString(tgt);
        }

        if(!states.containsKey(tgt_label)) states.put(tgt_label, productDFA.addState());
        sf = states.get(tgt_label);
        productDFA.addTransition(si, action, sf, null);
    }

    private void generateProductDFA(){
        boolean accepting = false;
        for(Integer s1 : this.dfa.getStates()){
            accepting = dfa.isAccepting(s1);
            for (Integer s2 : this.dfa2.getStates()) {
                generateCurrently(accepting, s1, s2);
            }
        }
    }

    private void initializeVars(CompactDFA<I> dfa2){
        this.dfa2 = dfa2;

        sigma1 = (I[]) this.dfa.getInputAlphabet().toArray();
        sigma2 = (I[]) this.dfa2.getInputAlphabet().toArray();
        I[] sigma = (I[]) Array.newInstance(sigma1.getClass().getComponentType(),
                sigma1.length + sigma2.length);

        System.arraycopy(sigma1, 0, sigma, 0, sigma1.length);
        System.arraycopy(sigma2, 0, sigma, sigma1.length, sigma2.length);

        productDFA = new CompactDFA<I>(Alphabets.fromArray(sigma));
        states = new HashMap<String, Integer>();
        initial_label = Integer.toString(dfa.getInitialState()) +
                "_" + Integer.toString(this.dfa2.getInitialState());
    }

    private void setFirstState(){
        Integer s0 = states.get(initial_label);
        productDFA.setInitialState(s0);
        this.setDfa(productDFA);
    }

    private void generateCurrently(boolean accepting, Integer s1, Integer s2){
        Integer si = null;
        accepting = accepting || this.dfa2.isAccepting(s2);
        String s_label = Integer.toString(s1) + "_" + Integer.toString(s2);
        if(!states.containsKey(s_label)) states.put(s_label, productDFA.addState());

        si = states.get(s_label);
        productDFA.setAccepting(si, accepting);

        for (I action : sigma1) {
            addNewTransition(action, s1, s2, si, true);
        }
        for (I action : sigma2) {
            addNewTransition(action, s1, s2, si, false);
        }
    }
}
