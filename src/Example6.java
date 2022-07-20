import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.filter.cache.dfa.DFACacheOracle;
import de.learnlib.filter.cache.dfa.DFACaches;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.oracle.equivalence.DFAEQOracleChain;
import de.learnlib.oracle.equivalence.DFAWMethodEQOracle;
import de.learnlib.oracle.membership.SimulatorOracle;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.ListAlphabet;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

public final class Example6 {
    private static final int EXPLORATION_DEPTH = 4;

    private static final String[] benchmarks = {"Benchmarks/ws/products_all.prtz",
            "Benchmarks/ws/products_2wise.prtz",
            "Benchmarks/ws/products_3wise.prtz",
            "Benchmarks/ws/products_4wise.prtz"};

    public static void main(String[] args) {
        for (String name : benchmarks){
            System.out.println("____________________________________________________________________________________");
            System.out.println(name);

            run_benchmark(name);
        }
    }

    private static void run_benchmark(String file_name){
        File file = new File(file_name);
        ProductDFA<String> productDFA;
        try {
            productDFA = Utils.getInstance().loadProductDFA(file, "ws/");
//            Visualization.visualize(dfa, dfa.getInputAlphabet());
        } catch (Exception e) {
            System.out.println("problem in loading file");
            throw new RuntimeException(e);
        }

        System.out.println();
//        System.out.println("Model: ");
//        GraphDOT.write(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet(), System.out); // may throw IOException!

//        Visualization.visualize(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());
//        System.out.println("-------------------------------------------------------");
        System.out.println("______________========== Running L* ==========____________");

        //pure L*
        long startTime = System.nanoTime();
        CompactDFA<String> lstarModel = run_LStar(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
//        String time_msg = String.format("_____The Model learned by L* algorithm in %s milliseconds _____", duration / 1000000);
        System.out.println("time:  " + duration / 1000000);

//        System.out.println("----------------------------**********-------------------------------------");
        System.out.println("____________========== Running Learn In Parts ===========_________________");
        // Learn In Parts
        startTime = System.nanoTime();
        Alphabet<String> alphabet = productDFA.getDfa().getInputAlphabet();
        MembershipOracle.DFAMembershipOracle<String> sul = new SimulatorOracle.DFASimulatorOracle<>(productDFA.getDfa());
        // oracle for counting queries wraps SUL
        DFACounterOracle<String> mqOracle = new DFACounterOracle<>(sul, "cache membership queries");
        DFACacheOracle<String> cacheSul = DFACaches.createCache(alphabet, mqOracle);
        DFACounterOracle<String> mqOracle2 = new DFACounterOracle<>(cacheSul, "learner membership queries");
        // Finally, store in a reference variable the 'effective' oracle to be used by the learner.
        DFACounterOracle<String> effOracle = mqOracle2;
        // Equivalence Oracle
        DFAWMethodEQOracle<String> wMethod = new DFAWMethodEQOracle<>(mqOracle2, EXPLORATION_DEPTH);
        EquivalenceOracle.DFAEquivalenceOracle<String> consistencyEqOracle
                = cacheSul.createCacheConsistencyTest();
        // declare a 'chain' of equivalence oracle:
        EquivalenceOracle.DFAEquivalenceOracle<String> eqOracle =
                new DFAEQOracleChain<>(consistencyEqOracle, wMethod);


        List<Alphabet<String>> initialSimaF = new ArrayList<>();
        for (String action : alphabet) {
            Alphabet<String> sigmai = new ListAlphabet<>(Arrays.asList(action));
            initialSimaF.add(sigmai);
        }
        LearnInParts<String> lip = new LearnInParts<String>(alphabet, mqOracle2, eqOracle);
        CompactDFA learnedDFA = lip.run(initialSimaF);

        endTime = System.nanoTime();
        duration = (endTime - startTime);
//        System.out.printf(lip.getSigmaFamily().toString());
//        time_msg = String.format("_____The Model learned by LIP algorithm in %s milliseconds", duration / 1000000);
//        System.out.println(time_msg);
        System.out.println("time:  " + duration / 1000000);


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
    }
    private static CompactDFA run_LStar(CompactDFA<String> target, Alphabet<String> inputs){
        MembershipOracle.DFAMembershipOracle<String> sul = new SimulatorOracle.DFASimulatorOracle<>(target);
        // oracle for counting queries wraps SUL
        DFACounterOracle<String> mqOracle = new DFACounterOracle<>(sul, "cache membership queries");
        DFACacheOracle<String> cacheSul = DFACaches.createCache(inputs, mqOracle);
        DFACounterOracle<String> mqOracle2 = new DFACounterOracle<>(cacheSul, "learner membership queries");
        // Finally, store in a reference variable the 'effective' oracle to be used by the learner.
        MembershipOracle.DFAMembershipOracle<String>  effOracle = mqOracle2;

        // Equivalence Oracle
        DFAWMethodEQOracle<String> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);
//        EquivalenceOracle.DFAEquivalenceOracle<String> consistencyEqOracle
//                = cacheSul.createCacheConsistencyTest();
        // declare a 'chain' of equivalence oracle:
//        EquivalenceOracle.DFAEquivalenceOracle<String> eqOracle =
//                new DFAEQOracleChain<>(consistencyEqOracle, wMethod);


//        MembershipOracle.DFAMembershipOracle<String> sul = new SimulatorOracle.DFASimulatorOracle<>(target);

        // oracle for counting queries wraps SUL
//        DFACounterOracle<String> mqOracle = new DFACounterOracle<>(sul, "membership queries");

        // construct L* instance
        ClassicLStarDFA<String> lstar =
                new ClassicLStarDFABuilder<String>().withAlphabet(inputs) // input alphabet
                        .withOracle(mqOracle2) // membership oracle
                        .create();

        lstar.startLearning();
        @Nullable DefaultQuery<String, Boolean> ce;
        int r =0;
        while ((ce = wMethod.findCounterExample(lstar.getHypothesisModel(),
                inputs)) != null) {
            r++;
            // Prints the counterexample that was found on the console
//            System.out.println("Refining using " + ce);

            lstar.refineHypothesis(ce);
        }
        DFA<?, String> result = lstar.getHypothesisModel();

        // construct a W-method conformance test
        // exploring the system up to depth 4 from
        // every state of a hypothesis
//        DFAWMethodEQOracle<String> wMethod = new DFAWMethodEQOracle<>(mqOracle, EXPLORATION_DEPTH);

        // construct a learning experiment from
        // the learning algorithm and the conformance test.
        // The experiment will execute the main loop of active learning
//        Experiment.DFAExperiment<String> experiment = new Experiment.DFAExperiment<>(lstar, wMethod, inputs);

        // turn on time profiling
//        experiment.setProfile(true);

        // enable logging of models
//        experiment.setLogModels(true);

        // run experiment
//        experiment.run();

        // get learned model
//        DFA<?, Character> result = experiment.getFinalHypothesis();

        // report results
//        System.out.println("-------------------------------------------------------");

        // profiling
//        System.out.println(" --------- simple profiler: ---------");
//        System.out.println(SimpleProfiler.getResults());

        // learning statistics
        System.out.println("------ learning statistics---------- ");
//        System.out.println(experiment.getRounds().getSummary());
        System.out.println(mqOracle.getStatisticalData().getSummary());
        System.out.println(mqOracle2.getStatisticalData().getSummary());
        System.out.println("Learning rounds (#EQs):  " + r);



        // model statistics
        System.out.println("-------- model statistics ------");
        System.out.println("States: " + result.size());
        System.out.println("Sigma: " + inputs.size());

        // show model
        System.out.println();
//        System.out.println("Model: ");
//        try {
//            GraphDOT.write(result, inputs, System.out); // may throw IOException!
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        Visualization.visualize(result, inputs);

//        System.out.println("-------------------------------------------------------");

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
