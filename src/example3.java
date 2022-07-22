import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.visualization.Visualization;

import java.io.File;
/*
* This example shows how to load a dfa from a txt file
* */
public final class example3 {

    public static void main(String[] args) {
        File file = new File("src/dfa1.txt");
        try {
            CompactDFA dfa = Utils.getInstance().loadDFA(file);
            Visualization.visualize(dfa, dfa.getInputAlphabet());
        } catch (Exception e) {
            System.out.println("problem in loading DFA");
            throw new RuntimeException(e);
        }

    }
}
