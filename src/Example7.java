///* Copyright (C) 2013-2022 TU Dortmund
// * This file is part of LearnLib, http://www.learnlib.de/.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
////package de.learnlib.examples;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFA;
//import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFABuilder;
//import de.learnlib.api.SUL;
//import de.learnlib.api.oracle.EquivalenceOracle;
//import de.learnlib.api.oracle.MembershipOracle;
//import de.learnlib.api.query.DefaultQuery;
//import de.learnlib.api.statistic.StatisticSUL;
//import de.learnlib.datastructure.observationtable.OTUtils;
//import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;
//import de.learnlib.filter.cache.dfa.DFACacheOracle;
//import de.learnlib.filter.cache.dfa.DFACaches;
//import de.learnlib.filter.statistic.oracle.DFACounterOracle;
//import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
//import de.learnlib.oracle.equivalence.DFAEQOracleChain;
//import de.learnlib.oracle.equivalence.DFAWMethodEQOracle;
//import de.learnlib.oracle.equivalence.EQOracleChain;
//import de.learnlib.oracle.membership.SimulatorOracle;
//import de.learnlib.util.Experiment;
//import de.learnlib.util.statistics.SimpleProfiler;
//import net.automatalib.automata.fsa.DFA;
//import net.automatalib.automata.fsa.impl.compact.CompactDFA;
//import net.automatalib.serialization.dot.GraphDOT;
//import net.automatalib.util.automata.builders.AutomatonBuilders;
//import net.automatalib.visualization.Visualization;
//import net.automatalib.words.Alphabet;
//import net.automatalib.words.impl.Alphabets;
//import net.automatalib.words.impl.ListAlphabet;
//import org.checkerframework.checker.nullness.qual.Nullable;
//
///**
// * This example shows the usage of product DFA and Learn In Parts algorithm.
// *
// * @author faezeh_labbaf
// */
//@SuppressWarnings("PMD.SystemPrintln")
//public final class Example7 {
//
//    private static final int EXPLORATION_DEPTH = 4;
//
//    private Example7() {
//        // prevent instantiation
//    }
//
//    public static void main(String[] args) throws IOException {
//        // load DFA and alphabet
//        CompactDFA<String> target = constructSUL1();
//        Alphabet<String> inputs1 = target.getInputAlphabet();
//
//        CompactDFA<String> target2 = constructSUL2();
//        Alphabet<String> inputs2 = target.getInputAlphabet();
//
//        ProductDFA<String> productDFA = new ProductDFA(target);
//        productDFA.interleaving_parallel_composition(target2);
//
//        Alphabet<String> alphabet = productDFA.getDfa().getInputAlphabet();
//        CompactDFA targetDFA = productDFA.getDfa();
//
//
//          /////////////////////////////////
//         // _______ Running L* ________///
//        /////////////////////////////////
//        System.out.println("_______ Running L* ________");
//        //pure L*
//        long startTime = System.nanoTime();
//        CompactDFA<String> lstarModel = run_LStar(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime);
//        String time_msg = String.format("_____The Model learned by L* algorithm in %s milliseconds _____", duration/1000000);
//        System.out.println(time_msg);
//
//        System.out.println("----------------------------**********-------------------------------------");
//        System.out.println("_______ Running Learn In Parts ________");
//
//          ////////////////////////////////
//         //  Running learn in parts   ///
//        ////////////////////////////////
//        startTime = System.nanoTime();
//        MembershipOracle.DFAMembershipOracle<String> sulSim = new SimulatorOracle.DFASimulatorOracle<>(targetDFA);
//
////        SUL<String,Boolean> sulSim = new SimulatorOracle.DFASimulatorOracle<>((DFA) targetDFA);
//
//        //////////////////////////////////
//        // Setup objects related to MQs	//
//        //////////////////////////////////
//
//        // Counters for MQs
//        StatisticSUL<String, Boolean> mq_sym = new SymbolCounterSUL<>("MQ", sulSim);
//        StatisticSUL<String, Word<String>>  mq_rst = new ResetCounterSUL <>("MQ", mq_sym);
//
//        // SUL for counting queries wraps sul
//        SUL<String, Word<String>> mq_sul = mq_rst;
//
//        // use caching to avoid duplicate queries
//        if(line.hasOption(CACHE))  {
//            // SULs for associating the IncrementalMealyBuilder 'mq_cbuilder' to MQs
//            mq_sul = SULCache.createDAGCache(mealyss.getInputAlphabet(), mq_rst);
//        }
//        MembershipOracle<String, Word<Word<String>>> mqOracle = new SULOracle<String, Word<String>>(mq_sul);
//
//        logger.logEvent("Cache: "+(line.hasOption(CACHE)?"Y":"N"));
//
//        //////////////////////////////////
//        // Setup objects related to EQs	//
//        //////////////////////////////////
//
//
//        logger.logEvent("ClosingStrategy: "+strategy.toString());
//        logger.logEvent("ObservationTableCEXHandler: "+handler.toString());
//
//        // Counters for EQs
//        StatisticSUL<String, Word<String>>  eq_sym = new SymbolCounterSUL<>("EQ", sulSim);
//        StatisticSUL<String, Word<String>>  eq_rst = new ResetCounterSUL <>("EQ", eq_sym);
//
//        // SUL for counting queries wraps sul
//        SUL<String, Word<String>> eq_sul = eq_rst;
//
//        // use caching to avoid duplicate queries
//        if(line.hasOption(CACHE))  {
//            // SULs for associating the IncrementalMealyBuilder 'cbuilder' to EQs
//            eq_sul = SULCache.createDAGCache(mealyss.getInputAlphabet(), eq_rst);
//        }
//
//
//
//        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle = null;
//        eqOracle = buildEqOracle(rnd_seed, line, logger, mealyss, eq_sul);
//
//        /////////////////////////////////
//        // _______ my code    ________///
//        /////////////////////////////////
//
//        Alphabet<String> alphabet = productDFA.getDfa().getInputAlphabet();
//        MembershipOracle.DFAMembershipOracle<String> sul = new SimulatorOracle.DFASimulatorOracle<>(productDFA.getDfa());
//        // oracle for counting queries wraps SUL
//        DFACounterOracle<String> mqOracle = new DFACounterOracle<>(sul, "cache membership queries");
//        DFACacheOracle<String> cacheSul = DFACaches.createCache(alphabet, mqOracle);
//        DFACounterOracle<String> mqOracle2 = new DFACounterOracle<>(cacheSul, "learner membership queries");
//        // Finally, store in a reference variable the 'effective' oracle to be used by the learner.
//        DFACounterOracle<String> effOracle = mqOracle2;
//        // Equivalence Oracle
//        DFAWMethodEQOracle<String> wMethod = new DFAWMethodEQOracle<>(mqOracle2, EXPLORATION_DEPTH);
//        EquivalenceOracle.DFAEquivalenceOracle<String> consistencyEqOracle
//                = cacheSul.createCacheConsistencyTest();
//        // declare a 'chain' of equivalence oracle:
//        EquivalenceOracle.DFAEquivalenceOracle<String> eqOracle =
//                new DFAEQOracleChain<>(consistencyEqOracle, wMethod);
//
//
//        List<Alphabet<String>> initialSimaF = new ArrayList<>();
//        for (String action : alphabet){
//            Alphabet<String> sigmai = new ListAlphabet<>(Arrays.asList(action));
//            initialSimaF.add(sigmai);
//        }
//        LearnInParts<String> lip = new LearnInParts<String>(alphabet, mqOracle2, eqOracle);
//        CompactDFA learnedDFA = lip.run(initialSimaF);
//
//        //////////////////////////////////
//        //      Log the Resultse        //
//        //////////////////////////////////
//        endTime = System.nanoTime();
//        duration = (endTime - startTime);
//        time_msg = String.format("_____The Model learned by LIP algorithm in %s milliseconds", duration/1000000);
//        System.out.println(time_msg);
//
//
//        // learning statistics
//        System.out.println("------ learning statistics---------- ");
//        System.out.println(lip.getRound_counter().getSummary());
//        System.out.println(lip.getMq_counter().getSummary());
//        System.out.println(lip.getEq_counter().getSummary());
//
//        // model statistics
//        System.out.println("-------- model statistics ------");
//        System.out.println("States: " + learnedDFA.size());
//        System.out.println("Sigma: " + learnedDFA.getInputAlphabet().size());
//
//        System.out.println("------ learning statistics (mqOracles) ---------- ");
//        System.out.println(lip.getRound_counter().getSummary());
//        System.out.println(mqOracle.getCounter().getSummary());
//        System.out.println(mqOracle2.getStatisticalData().getSummary());
//        System.out.println(lip.getEq_counter().getSummary());
//
//    }
//
//    /**
//     *
//     * @return example dfa
//     */
//    private static CompactDFA<String> constructSUL1() {
//        // input alphabet contains Strings "a"
//        Alphabet<String> sigma = Alphabets.fromList(List.of("a","b"));
//
//        // @formatter:off
//        // create automaton
//        return AutomatonBuilders.newDFA(sigma)
//                .withInitial("q0")
//                .from("q0")
//                .on("a").to("q0")
//                .on("b").to("q2")
//                .from("q1")
//                .on("a").to("q0")
//                .on("b").to("q2")
//                .from("q2")
//                .on("a").to("q0")
//                .on("b").to("q2")
//                .withAccepting("q2")
//                .create();
//
//        // @formatter:on
//    }
//
//    private static CompactDFA<String> constructSUL2() {
//        // input alphabet contains characters "a".."b"
//        Alphabet<String> sigma = Alphabets.fromList(List.of("x", "y"));
//
//        // @formatter:off
//        // create automaton
//        return AutomatonBuilders.newDFA(sigma)
//                .withInitial("q0")
//                .from("q0")
//                .on("x").to("q1")
//                .on("y").to("q0")
//                .from("q1")
//                .on("x").to("q1")
//                .on("y").to("q0")
//                .withAccepting("q1")
//                .create();
//
//        // @formatter:on
//    }
//
//    private static CompactDFA run_LStar(CompactDFA<String> target, Alphabet<String> inputs){
//        MembershipOracle.DFAMembershipOracle<String> sul = new SimulatorOracle.DFASimulatorOracle<>(target);
//        // oracle for counting queries wraps SUL
//        DFACounterOracle<String> mqOracle = new DFACounterOracle<>(sul, "cache membership queries");
//        DFACacheOracle<String> cacheSul = DFACaches.createCache(inputs, mqOracle);
//        DFACounterOracle<String> mqOracle2 = new DFACounterOracle<>(cacheSul, "learner membership queries");
//        // Finally, store in a reference variable the 'effective' oracle to be used by the learner.
//        MembershipOracle.DFAMembershipOracle<String>  effOracle = mqOracle2;
//
//        // Equivalence Oracle
//        DFAWMethodEQOracle<String> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
//        EquivalenceOracle.DFAEquivalenceOracle<String> consistencyEqOracle
//                = cacheSul.createCacheConsistencyTest();
//        // declare a 'chain' of equivalence oracle:
//        EquivalenceOracle.DFAEquivalenceOracle<String> eqOracle =
//                new DFAEQOracleChain<>(consistencyEqOracle, wMethod);
//
//        // construct L* instance
//        ClassicLStarDFA<String> lstar =
//                new ClassicLStarDFABuilder<String>().withAlphabet(inputs) // input alphabet
//                        .withOracle(mqOracle2) // membership oracle
//                        .create();
//
//        lstar.startLearning();
//        @Nullable DefaultQuery<String, Boolean> ce;
//        while ((ce = wMethod.findCounterExample(lstar.getHypothesisModel(),
//                inputs)) != null) {
//            // Prints the counterexample that was found on the console
//            System.out.println("Refining using " + ce);
//
//            lstar.refineHypothesis(ce);
//        }
//
//        // report results
//        System.out.println("-------------------------------------------------------");
//
//        // learning statistics
//        System.out.println("------ learning statistics---------- ");
//        System.out.println(mqOracle.getStatisticalData().getSummary());
//        System.out.println(mqOracle2.getStatisticalData().getSummary());
//        System.out.println();
//        System.out.println("-------------------------------------------------------");
//
//        return (CompactDFA) null;
//    }
//}