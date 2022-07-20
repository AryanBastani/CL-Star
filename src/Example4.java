/* Copyright (C) 2015 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 *
 * LearnLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 *
 * LearnLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LearnLib; if not, see
 * <http://www.gnu.de/documents/lgpl.en.html>.
 */


import java.util.Random;

import de.learnlib.algorithms.lstar.dfa.ExtensibleLStarDFA;
import de.learnlib.algorithms.lstar.dfa.ExtensibleLStarDFABuilder;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.datastructure.observationtable.OTUtils;
import de.learnlib.filter.cache.dfa.DFACacheOracle;
import de.learnlib.filter.cache.dfa.DFACaches;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.oracle.equivalence.*;
import de.learnlib.oracle.membership.SimulatorOracle;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.util.automata.builders.AutomatonBuilders;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

import net.automatalib.words.impl.Alphabets;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This class demonstrates the use of LearnLib on a simple example: learning a model
 * of a coffee machine.
 * <p>
 * This is a detailed walkthrough for setting up a standard learning scenario in
 * LearnLib.
 *
 * @author Malte Isberner
 */
public class Example4 {

    public static void main(String[] args) throws Exception {
        // The coffee machine is one of the classic automata learning examples.
        // A description of it can be found in the paper "An Introduction to
        // Active Automata Learning from a Practical Perspective" by Steffen et al.
        // It is contained in the de.learnlib.testsupport:learnlib-learning-examples
        // artifact, which is referenced in the pom.xml file.
        // CompactMealy is AutomataLib's own internal data structure for explicit-state,
        // explicit-alphabet Mealy machines.
        CompactDFA<Character> target
                = constructProductDFA();

        // Many methods in LearnLib require the user to explicitly specify the input
        // alphabet on which they should operate. Therefore, it is always a good idea
        // to store a reference to this alphabet in a local variable.
        Alphabet<Character> alphabet = target.getInputAlphabet();

        // Visualize the model (rather: a view of its transition graph) using the
        // default visualization provider. If GraphVIZ is installed, then DOT
        // will be used for rendering. If there are path issues with DOT, check
        // the settings in the learnlib.properties file in the project root.
        // The 'true' parameters signifies that the dialog should be modal, i.e.,
        // the program execution is resumed only after it is closed.
//        Visualization.visualizeGraph(target.graphView(), true);

        // Automata learning algorithms do not interact with the target system directly,
        // but via a (membership) oracle. In our case, the membership oracle simply
        // simulates traces on the automaton, we therefore use a SimulatorOracle.
        MembershipOracle.DFAMembershipOracle<Character> oracle
                = new SimulatorOracle.DFASimulatorOracle<>(target);

        // Additionally, we want to count the number of queries that actually reach
        // the target system (SUL, System Under Learning). In LearnLib, this is realized
        // by encapsulating the (target) oracle in another oracle, which takes care
        // of the counting. This allows the user to declare a pipeline of several
        // so-called filters.
        DFACounterOracle<Character> queryCounter =
                new DFACounterOracle<>(oracle, "Queries to SUL");

        // To reduce the effort of learning, it is usually a good idea to store answers
        // to previous queries in a cache. Note that in our scenario, this probably
        // won't actually decrease the running time, as simulating a query on an explicitly
        // given automaton is faster than looking it up in a cache data structure. However,
        // when interacting with hardware systems or web services, the time required
        // for a single query can be considerable, so a cache will be much, much faster.
        // Again, we enable caching by "wrapping" a cache around our oracle.
        DFACacheOracle<Character> cache
                = DFACaches.createCache(alphabet, queryCounter);

        // The counter above takes care of counting how many queries reach the target system.
        // Note that since the cache was added to the pipeline *after* the first counter oracle,
        // some queries will be filtered by the cache and not reach the counter oracle
        // (and thus the target system). Therefore, we add a second counter oracle, this time
        // on top of the cache, to count the queries sent from the learner (pre-caching).
        DFACounterOracle<Character> learnerQueryCounter
                = new DFACounterOracle<>(cache, "Queries from Learner");

        // Finally, store in a reference variable the 'effective' oracle to be used by the learner.
        MembershipOracle.DFAMembershipOracle<Character> effOracle
                = learnerQueryCounter;

        // Create the learning algorithm. Here, we use LearnLib's preferred variant of the
        // L* algorithm for Mealy machines. All learning algorithms require specifying
        // an oracle and a learning alphabet. On top of that, some algorithms are configurable
        // via various other parameters, but these are all optional.
        ExtensibleLStarDFA<Character> learner
                = new ExtensibleLStarDFABuilder<Character>()
                .withAlphabet(alphabet)
                .withOracle(effOracle)
                // An example for an optional parameter is the counterexample handler.
                // By default, L* adds all prefixes of a counterexample to the observation
                // table, but other variants are generally much more efficient.
                // Uncomment the following line to see the effect of replacing a counterexample
                // handler.
//				.withCexHandler(ObservationTableCEXHandlers.RIVEST_SCHAPIRE)
                .create();

        // Another option would be to use the TTT algorithm, or yet another algorithm
        // shipped with LearnLib, such as KearnsVaziraniMealy, DHCMealy, or DTLearnerMealy.
        // All these come with a builder class (...Builder) that can be used as above.
        // Note: After uncommenting, direct your IDE to automatically import required
        // classes (e.g., using Shift+Cmd/Ctrl+O in Eclipse).

//		TTTLearnerMealy<Character> learner
//			= new TTTLearnerMealyBuilder<Input,String>()
//			.withAlphabet(alphabet)
//			.withOracle(effOracle)
//			.create();


        // Start the learning process, until it converges to a first stable hypothesis.
        // Further progress after this initial step is only triggered by counterexamples.
        learner.startLearning();

        // Create an equivalence oracle, for generating counterexamples.
        // Here, we use a random words (or random sampling) oracle, that approximates
        // equivalence queries by randomly generating words of length uniformly distributed
        // between 5 and 20. If it does not find a counterexample after 1000 steps,
        // it stops.
        EquivalenceOracle.DFAEquivalenceOracle<Character> randomEqOracle
                = new DFAWMethodEQOracle<>(
                effOracle, // the target oracle
                4     // the minimum sample length
                  // the maximum number of tests in one iteration
                );

        // A cache can also be used to implement a form of equivalence oracle, namely
        // by testing the result of every query stored in the cache against the current
        // hypothesis. Such a 'cache consistency test' can be obtained directly from the
        // cache oracle:
        EquivalenceOracle.DFAEquivalenceOracle<Character> consistencyEqOracle
                = cache.createCacheConsistencyTest();

        // For combining it with our previously defined random sampling oracle, LearnLib
        // allows to declare a 'chain' of equivalence oracle:
        EquivalenceOracle.DFAEquivalenceOracle<Character> eqOracle
                = new DFAEQOracleChain<>(consistencyEqOracle, randomEqOracle);


        // Counterexamples in LearnLib are stored in the form of a query. A query consists
        // of an input word (separated into prefix and suffix), and can be answered.
        // The DefaultQuery class stores the answer, and makes it available via
        // getOutput().
        @Nullable DefaultQuery<Character, Boolean> ce;
        // The main learning loop. Note that unlike in some descriptions, in LearnLib a learning
        // algorithm never queries an equivalence oracle. Instead, this has to be triggered
        // by the user, who then passes the obtained counterexample to the learner
        // (see below).
        // We iterate until the equivalence oracle fails to find another counterexample.
        // Note that since we use random testing, this does not guarantee that the hypothesis
        // is really equivalent to the target system!
        while ((ce = eqOracle.findCounterExample(learner.getHypothesisModel(),
                alphabet)) != null) {
            // We could now also visualize any intermediate hypothesis by uncommenting
            // the following line:
//			Visualization.visualizeAutomaton(learner.getHypothesisModel(), alphabet, true);

            // The L* algorithm internally maintains an observation table. This table can
            // be shown in the browser by uncommenting the following line:
//			OTUtils.displayHTMLInBrowser(learner.getObservationTable());
            // Note that this won't work with non-observation-table based algorithms. For
            // example, for the TTT algorithm, the discrimination tree data structure
            // can be visualized by the following line:
//			Visualization.visualizeGraph(learner.getDiscriminationTree().graphView(), true);


            // Prints the counterexample that was found on the console
            System.out.println("Refining using " + ce);
            // Requests the learner to refine its current hypothesis, using the
            // counterexample that we found.
            learner.refineHypothesis(ce);
        }

        // Now, we have a final hypothesis. Let's show it again, and also show the final
        // observation table
//        OTUtils.displayHTMLInBrowser(learner.getObservationTable());
//        Visualization.visualize(learner.getHypothesisModel(), alphabet, true);


        // Also, print the data we gathered during the learning process:
        // (1) the queries that reached the target system (after cache filtering), and
        // (2) the queries that were sent by the learner (before cache filtering).
        System.out.println(queryCounter.getStatisticalData());
        System.out.println(learnerQueryCounter.getStatisticalData());
    }


    private static CompactDFA<Character> constructSUL1() {
        // input alphabet contains characters 'a'
        Alphabet<Character> sigma = Alphabets.characters('a','b');

        // @formatter:off
        // create automaton
        return AutomatonBuilders.newDFA(sigma)
                .withInitial("q0")
                .from("q0")
                .on('a').to("q0")
                .on('b').to("q2")
                .from("q1")
                .on('a').to("q0")
                .on('b').to("q2")
                .from("q2")
                .on('a').to("q0")
                .on('b').to("q2")
                .withAccepting("q2")
                .create();

        // @formatter:on
    }

    private static CompactDFA<Character> constructSUL2() {
        // input alphabet contains characters 'a'..'b'
        Alphabet<Character> sigma = Alphabets.characters('x', 'y');

        // @formatter:off
        // create automaton
        return AutomatonBuilders.newDFA(sigma)
                .withInitial("q0")
                .from("q0")
                .on('x').to("q1")
                .on('y').to("q0")
                .from("q1")
                .on('x').to("q1")
                .on('y').to("q0")
                .withAccepting("q1")
                .create();

        // @formatter:on
    }

    private static CompactDFA<Character> constructProductDFA(){
        CompactDFA<Character> target = constructSUL1();
        Alphabet<Character> inputs1 = target.getInputAlphabet();

        CompactDFA<Character> target2 = constructSUL2();
        Alphabet<Character> inputs2 = target.getInputAlphabet();

        ProductDFA<Character> productDFA = new ProductDFA(target);
        productDFA.interleaving_parallel_composition(target2);
        return productDFA.getDfa();
    }


}