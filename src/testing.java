import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.util.automata.builders.AutomatonBuilders;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;

import java.io.IOException;

public class testing {
    public static void main(String[] args) throws IOException {
        // load DFA and alphabet
        CompactDFA<Character> target = constructSUL1();
        Alphabet<Character> inputs = target.getInputAlphabet();

        CompactDFA<Character> target2 = constructSUL2();
        Alphabet<Character> inputs2 = target.getInputAlphabet();

        ProductDFA<Character> productDFA = new ProductDFA<>(target);
        productDFA.interleaving_parallel_composition(target2);

        // report results
        System.out.println("-------------------------------------------------------");

        // show model
        System.out.println();
        System.out.println("Model: ");
        GraphDOT.write(productDFA.getDfa(), inputs, System.out); // may throw IOException!


        Visualization.visualize(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());

        System.out.println("-------------------------------------------------------");
        System.out.println(productDFA.getDfa().getInputAlphabet());
//        System.out.println(target.getTransitions(0,'a'));
    }

    /**
     *
     * @return example dfa
     */
    private static CompactDFA<Character> constructSUL1() {
        // input alphabet contains characters 'a'
        Alphabet<Character> sigma = Alphabets.characters('a','a');

        // @formatter:off
        // create automaton
        return AutomatonBuilders.newDFA(sigma)
                .withInitial("q0")
                .from("q0")
                .on('a').to("q1")
                .from("q1")
                .on('a').to("q0")
                .withAccepting("q1")
                .create();

        // @formatter:on
    }

    private static CompactDFA<Character> constructSUL2() {
        // input alphabet contains characters 'a'..'b'
        Alphabet<Character> sigma = Alphabets.characters('b', 'b');

        // @formatter:off
        // create automaton
        return AutomatonBuilders.newDFA(sigma)
                .withInitial("q0")
                .from("q0")
                .on('b').to("q1")
                .from("q1")
                .on('b').to("q1")
                .withAccepting("q1")
                .create();

        // @formatter:on
    }
}
