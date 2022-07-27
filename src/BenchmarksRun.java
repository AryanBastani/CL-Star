import com.google.common.math.BigIntegerMath;
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
import net.automatalib.words.impl.Alphabets;
import net.automatalib.words.impl.ListAlphabet;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.automatalib.automata.fsa.DFA;


public final class BenchmarksRun {
    public static int FILE_NAME = 0;
    public static int STATES = 1;
    public static int INPUTS = 2;
    public static int LSTAR_MQ = 3;
    public static int LSTAR_EQ = 4;
    public static int LSTAR_STATES = 5;
    public static int LIP_MQ = 6;
    public static int LIP_EQ = 7;
    public static int LIP_STATES = 8;
    public static int COMPONENTS = 9 ;
    public static int ROUNDS = 10;

    public static String[] data;

    private static final int EXPLORATION_DEPTH = 4;

    private static final String RESULTS_PATH = "Benchmarks/results.csv";

    private static final String[] benchmarks = {
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_0.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_01.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_02.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_03.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_04.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_05.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_06.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_07.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_08.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wisie_09.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_01.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_02.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_03.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_04.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_05.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_06.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_07.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_08.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wisie_09.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/7wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/8wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_13.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_20.txt"
            };

    public static void main(String[] args) throws IOException {
        for (String name : benchmarks){
            data = new String[11];
            data[FILE_NAME] = name;
            System.out.println("____________________________________________________________________________________");
            System.out.println(name);
            run_benchmark(name);
        }
    }

    private static void run_benchmark(String file_name) throws IOException {
        File file = new File(file_name);
        CompactDFA<String> target;
        try {
            target = Utils.getInstance().loadProductDFA(file, "BCS_SPL/Complete_FSM_files/").getDfa();
            System.out.println(target.size());
            data[STATES] = Integer.toString(target.size());
//            Visualization.visualize(target, target.getInputAlphabet());
        } catch (Exception e) {
            System.out.println("problem in loading file");
            throw new RuntimeException(e);
        }

        Alphabet<String> dfa_alphabet = target.getInputAlphabet();
//        int itter_lim = dfa_alphabet.size()> 4 ? 24 : BigIntegerMath.factorial(dfa_alphabet.size()).intValue();
        int itter_lim = 5;
        String[] alphArr =  dfa_alphabet.toArray(new String[dfa_alphabet.size()]);

        for (int i=0; i < itter_lim; i++ ){
//            System.out.println();
//            System.out.println("______________========== Running L* ==========____________");
            Collections.shuffle(Arrays.asList(alphArr));
            Alphabet<String> alphabet = Alphabets.fromArray(alphArr);
            System.out.println(alphabet.toString());

            //pure L*
            long startTime = System.nanoTime();
            CompactDFA<String> lstarModel = run_LStar(target, alphabet);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            System.out.println("time:  " + duration / 1000000);


            System.out.println("____________========== Running Learn In Parts ===========_________________");
            // Learn In Parts
            startTime = System.nanoTime();
            MembershipOracle.DFAMembershipOracle<String> sul = new SimulatorOracle.DFASimulatorOracle<>(target);
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
            System.out.printf(lip.getSigmaFamily().toString());
            data[COMPONENTS] = Integer.toString(lip.getSigmaFamily().size());
            System.out.println("time:  " + duration / 1000000);


            // model statistics
            System.out.println("-------- model statistics ------");
            System.out.println("States: " + learnedDFA.size());
            data[LIP_STATES] = Integer.toString(learnedDFA.size());
            System.out.println("Sigma: " + learnedDFA.getInputAlphabet().size());
            data[INPUTS] = Integer.toString(learnedDFA.getInputAlphabet().size());

            System.out.println("------ learning statistics --------- ");
            System.out.println(lip.getRound_counter().getSummary());
            data[ROUNDS] = Long.toString(lip.getRound_counter().getCount());
            System.out.println(mqOracle.getCounter().getSummary());
            data[LIP_MQ] = Long.toString(mqOracle.getCounter().getCount());
            System.out.println(mqOracle2.getStatisticalData().getSummary());
            System.out.println(lip.getEq_counter().getSummary());
            data[LIP_EQ] = Long.toString(lip.getEq_counter().getCount());

            Utils.writeDataLineByLine(RESULTS_PATH, data);
        }



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
        data[LSTAR_STATES] = Integer.toString(result.size());

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
        data[LSTAR_MQ] = Long.toString(mqOracle.getStatisticalData().getCount());

        System.out.println(mqOracle2.getStatisticalData().getSummary());
        System.out.println("Learning rounds (#EQs):  " + r);
        data[LSTAR_EQ] = Integer.toString(r);




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
