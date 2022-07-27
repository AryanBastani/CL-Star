import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.visualization.Visualization;

import java.io.File;
/*
* This example shows how to load a dfa from a dot file
* */
public final class example3 {

    public static void main(String[] args) {
        File file = new File("Benchmarks/BCS_SPL/Complete_FSM_files/4_3_BCS_FP.dot");
        try {
            CompactDFA dfa = Utils.getInstance().loadDFAfromDOT(file);
            Visualization.visualize(dfa, dfa.getInputAlphabet());
        } catch (Exception e) {
            System.out.println("problem in loading DFA");
            throw new RuntimeException(e);
        }

    }
}
