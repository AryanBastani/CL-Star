/* Copyright (C) 2013-2022 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//package de.learnlib.examples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.datastructure.observationtable.OTUtils;
import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;
import de.learnlib.filter.cache.dfa.DFACacheOracle;
import de.learnlib.filter.cache.dfa.DFACaches;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.oracle.equivalence.DFAEQOracleChain;
import de.learnlib.oracle.equivalence.DFAWMethodEQOracle;
import de.learnlib.oracle.equivalence.EQOracleChain;
import de.learnlib.oracle.membership.SimulatorOracle;
import de.learnlib.util.Experiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.util.automata.builders.AutomatonBuilders;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.Alphabets;
import net.automatalib.words.impl.ListAlphabet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This example shows the usage of product DFA and Learn In Parts algorithm.
 *
 * @author faezeh_labbaf
 */
@SuppressWarnings("PMD.SystemPrintln")
public final class Example2 {

    private static final int EXPLORATION_DEPTH = 4;

    private Example2() {
        // prevent instantiation
    }

    public static void main(String[] args) throws IOException {
        // load DFA and alphabet
        CompactDFA<Character> target = constructSUL1();
        Alphabet<Character> inputs1 = target.getInputAlphabet();

        CompactDFA<Character> target2 = constructSUL2();
        Alphabet<Character> inputs2 = target.getInputAlphabet();

        ProductDFA<Character> productDFA = new ProductDFA(target);
        productDFA.interleaving_parallel_composition(target2);


        // show model
        System.out.println();
//        System.out.println("Model: ");
//        GraphDOT.write(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet(), System.out); // may throw IOException!

//        Visualization.visualize(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());
        System.out.println("-------------------------------------------------------");
        System.out.println("_______ Running L* ________");

        //pure L*
        long startTime = System.nanoTime();
        CompactDFA<Character> lstarModel = run_LStar(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        String time_msg = String.format("_____The Model learned by L* algorithm in %s milliseconds _____", duration/1000000);
        System.out.println(time_msg);

        System.out.println("----------------------------**********-------------------------------------");
        System.out.println("_______ Running Learn In Parts ________");
         // Learn In Parts
        startTime = System.nanoTime();
        Alphabet<Character> alphabet = productDFA.getDfa().getInputAlphabet();
        MembershipOracle.DFAMembershipOracle<Character> sul = new SimulatorOracle.DFASimulatorOracle<>(productDFA.getDfa());
        // oracle for counting queries wraps SUL
        DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(sul, "cache membership queries");
        DFACacheOracle<Character> cacheSul = DFACaches.createCache(alphabet, mqOracle);
        DFACounterOracle<Character> mqOracle2 = new DFACounterOracle<>(cacheSul, "learner membership queries");
        // Finally, store in a reference variable the 'effective' oracle to be used by the learner.
        DFACounterOracle<Character> effOracle = mqOracle2;
        // Equivalence Oracle
        DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle2, EXPLORATION_DEPTH);
        EquivalenceOracle.DFAEquivalenceOracle<Character> consistencyEqOracle
                = cacheSul.createCacheConsistencyTest();
        // declare a 'chain' of equivalence oracle:
        EquivalenceOracle.DFAEquivalenceOracle<Character> eqOracle =
                new DFAEQOracleChain<>(consistencyEqOracle, wMethod);


        List<Alphabet<Character>> initialSimaF = new ArrayList<>();
        for (Character action : alphabet){
            Alphabet<Character> sigmai = new ListAlphabet<>(Arrays.asList(action));
            initialSimaF.add(sigmai);
        }
        LearnInParts<Character> lip = new LearnInParts<Character>(alphabet, mqOracle2, eqOracle);
        CompactDFA learnedDFA = lip.run(initialSimaF);

        endTime = System.nanoTime();
        duration = (endTime - startTime);
        time_msg = String.format("_____The Model learned by LIP algorithm in %s milliseconds", duration/1000000);
        System.out.println(time_msg);

//        // profiling
//        System.out.println(" --------- simple profiler: ---------");
//        System.out.println(SimpleProfiler.getResults());

        // learning statistics
        System.out.println("------ learning statistics---------- ");
        System.out.println(lip.getRound_counter().getSummary());
        System.out.println(lip.getMq_counter().getSummary());
        System.out.println(lip.getEq_counter().getSummary());

        // model statistics
        System.out.println("-------- model statistics ------");
        System.out.println("States: " + learnedDFA.size());
        System.out.println("Sigma: " + learnedDFA.getInputAlphabet().size());

        System.out.println("------ learning statistics (mqOracles) ---------- ");
        System.out.println(lip.getRound_counter().getSummary());
        System.out.println(mqOracle.getCounter().getSummary());
        System.out.println(mqOracle2.getStatisticalData().getSummary());
        System.out.println(lip.getEq_counter().getSummary());


//        System.out.println();
//        System.out.println("Model: ");
//        GraphDOT.write(learnedDFA, learnedDFA.getInputAlphabet(), System.out);
//        Visualization.visualize(learnedDFA, learnedDFA.getInputAlphabet());
    }

    /**
     *
     * @return example dfa
     */
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

    private static CompactDFA run_LStar(CompactDFA<Character> target, Alphabet<Character> inputs){
        MembershipOracle.DFAMembershipOracle<Character> sul = new SimulatorOracle.DFASimulatorOracle<>(target);
        // oracle for counting queries wraps SUL
        DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(sul, "cache membership queries");
        DFACacheOracle<Character> cacheSul = DFACaches.createCache(inputs, mqOracle);
        DFACounterOracle<Character> mqOracle2 = new DFACounterOracle<>(cacheSul, "learner membership queries");
        // Finally, store in a reference variable the 'effective' oracle to be used by the learner.
        MembershipOracle.DFAMembershipOracle<Character>  effOracle = mqOracle2;

        // Equivalence Oracle
        DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
        EquivalenceOracle.DFAEquivalenceOracle<Character> consistencyEqOracle
                = cacheSul.createCacheConsistencyTest();
        // declare a 'chain' of equivalence oracle:
        EquivalenceOracle.DFAEquivalenceOracle<Character> eqOracle =
                new DFAEQOracleChain<>(consistencyEqOracle, wMethod);


//        MembershipOracle.DFAMembershipOracle<Character> sul = new SimulatorOracle.DFASimulatorOracle<>(target);

        // oracle for counting queries wraps SUL
//        DFACounterOracle<Character> mqOracle = new DFACounterOracle<>(sul, "membership queries");

        // construct L* instance
        ClassicLStarDFA<Character> lstar =
                new ClassicLStarDFABuilder<Character>().withAlphabet(inputs) // input alphabet
                        .withOracle(mqOracle2) // membership oracle
                        .create();

        lstar.startLearning();
        @Nullable DefaultQuery<Character, Boolean> ce;
        while ((ce = wMethod.findCounterExample(lstar.getHypothesisModel(),
                inputs)) != null) {
            // Prints the counterexample that was found on the console
            System.out.println("Refining using " + ce);

            lstar.refineHypothesis(ce);
        }


        // construct a W-method conformance test
        // exploring the system up to depth 4 from
        // every state of a hypothesis
//        DFAWMethodEQOracle<Character> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);

        // construct a learning experiment from
        // the learning algorithm and the conformance test.
        // The experiment will execute the main loop of active learning
//        Experiment.DFAExperiment<Character> experiment = new Experiment.DFAExperiment<>(lstar, wMethod, inputs);

        // turn on time profiling
//        experiment.setProfile(true);

        // enable logging of models
//        experiment.setLogModels(true);

        // run experiment
//        experiment.run();

        // get learned model
//        DFA<?, Character> result = experiment.getFinalHypothesis();

        // report results
        System.out.println("-------------------------------------------------------");

        // profiling
//        System.out.println(" --------- simple profiler: ---------");
//        System.out.println(SimpleProfiler.getResults());

        // learning statistics
        System.out.println("------ learning statistics---------- ");
//        System.out.println(experiment.getRounds().getSummary());
        System.out.println(mqOracle.getStatisticalData().getSummary());
        System.out.println(mqOracle2.getStatisticalData().getSummary());


        // model statistics
//        System.out.println("-------- model statistics ------");
//        System.out.println("States: " + result.size());
//        System.out.println("Sigma: " + inputs.size());

        // show model
        System.out.println();
//        System.out.println("Model: ");
//        try {
//            GraphDOT.write(result, inputs, System.out); // may throw IOException!
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        Visualization.visualize(result, inputs);

        System.out.println("-------------------------------------------------------");

//        System.out.println("Final observation table:");
//        new ObservationTableASCIIWriter<>().write(lstar.getObservationTable(), System.out);

//        try {
//            OTUtils.displayHTMLInBrowser(lstar.getObservationTable());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return (CompactDFA) null;
    }
}