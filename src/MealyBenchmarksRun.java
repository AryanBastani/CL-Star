
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.api.SUL;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.statistic.StatisticSUL;
import de.learnlib.driver.util.MealySimulatorSUL;
import de.learnlib.filter.cache.sul.SULCache;
import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
import de.learnlib.oracle.equivalence.WpMethodEQOracle;
import de.learnlib.oracle.membership.SULOracle;
import de.learnlib.util.Experiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.commons.util.Pair;
import net.automatalib.serialization.InputModelDeserializer;
import net.automatalib.serialization.dot.DOTParsers;
import net.automatalib.visualization.Visualization;
import net.automatalib.visualization.VisualizationHelper;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class MealyBenchmarksRun {

    private static final String[] benchmarks = {
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_1.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_2.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_18.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_13.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_20.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_5.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_6.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_8.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_10.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_12.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_13.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_14.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_16.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_17.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_18.txt",
//            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/2wise_20.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_1.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_2.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_3.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_5.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_6.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_7.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_8.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_10.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_11.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_15.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_16.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_17.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_18.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/3wise_20.txt",
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

            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_20.txt"
    };

    public static int FILE_NAME = 0;
    public static int STATES = 1;
    public static int INPUTS = 2;
    public static int LSTAR_MQ_SYM = 3;
    public static int LSTAR_MQ_RST = 4;
    public static int LSTAR_EQ_SYM = 5;
    public static int LSTAR_EQ_RST = 6;
    public static int LSTAR_TOTAL_SYM = 7;
    public static int LSTAR_TOTAL_RST = 8;
    public static int LSTAR_EQs = 9;
    public static int LSTAR_STATES = 10;
    public static int LIP_MQ_SYM = 11;
    public static int LIP_MQ_RST = 12;
    public static int LIP_EQ_SYM = 13;
    public static int LIP_EQ_RST = 14;
    public static int LIP_TOTAL_SYM = 15;
    public static int LIP_TOTAL_RST = 16;
    public static int LIP_EQs = 17;
    public static int LIP_STATES = 18;
    public static int COMPONENTS = 19 ;
    public static int ROUNDS = 20;

    public static String[] data;

    private static final int EXPLORATION_DEPTH = 4;

    private static final String RESULTS_PATH = "Benchmarks/mealyResults.csv";



    public static void main(String[] args) throws IOException {
        for(String c : benchmarks){
            data = new String[21];
            File file = new File(c);
            data[FILE_NAME] = c;
            CompactMealy<String, Word<String>> target;
            try {
                target = Utils.getInstance().loadProductMealy(file, "BCS_SPL/Complete_FSM_files/").fsm;
//                Visualization.visualize(target, target.getInputAlphabet());
            } catch (Exception e) {
                System.out.println("problem in loading file");
                System.out.println(c);
                continue;
//                throw new RuntimeException(e);
            }
            System.out.println(target.size());
            System.out.println(c);
            data[STATES] = Integer.toString(target.size());
            data[INPUTS] = Integer.toString(target.numInputs());
//          // Shuffle the alphabet
            Alphabet<String> alph = target.getInputAlphabet();
            String[] alphArr =  alph.toArray(new String[alph.size()]);
            Collections.shuffle(Arrays.asList(alphArr));
            Alphabet<String> alphabet = Alphabets.fromArray(alphArr);

            learnProductMealy(target, alphabet);
            learnMealyInParts(target, alphabet);
            Utils.writeDataLineByLine(RESULTS_PATH, data);
        }
    }

    public static void learnMealyInParts(CompactMealy mealyss, Alphabet<String> alphabet){

        Utils.getInstance();
        // SUL simulator
        SUL<String, Word<String>> sulSim = new MealySimulatorSUL<>(mealyss, Utils.OMEGA_SYMBOL);

        //////////////////////////////////
        // Setup objects related to MQs //
        //////////////////////////////////

        // Counters for MQs
        StatisticSUL<String, Word<String>> mq_sym = new SymbolCounterSUL<>("MQ", sulSim);
        StatisticSUL<String, Word<String>> mq_rst = new ResetCounterSUL<>("MQ", mq_sym);

        // SUL for counting queries wraps sul
        SUL<String, Word<String>> mq_sul = mq_rst;

        // use caching to avoid duplicate queries
        // SULs for associating the IncrementalMealyBuilder 'mq_cbuilder' to MQs
        mq_sul = SULCache.createDAGCache(alphabet, mq_rst);

        MembershipOracle<String, Word<Word<String>>> mqOracle = new SULOracle<String, Word<String>>(mq_sul);

        //////////////////////////////////
        // Setup objects related to EQs //
        //////////////////////////////////

        // Counters for EQs
        StatisticSUL<String, Word<String>> eq_sym = new SymbolCounterSUL<>("EQ", sulSim);
        StatisticSUL<String, Word<String>> eq_rst = new ResetCounterSUL<>("EQ", eq_sym);

        // SUL for counting queries wraps sul
        SUL<String, Word<String>> eq_sul = eq_rst;

        // SULs for associating the IncrementalMealyBuilder 'cbuilder' to EQs
        eq_sul = SULCache.createDAGCache(alphabet, eq_rst);

        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle = null;
        eqOracle = buildEqOracle(eq_sul);


        MealyLearnInParts LIP = new MealyLearnInParts(alphabet, mqOracle, eqOracle);
        CompactMealy result = LIP.run();



//        // statistics array
        data[ROUNDS] = String.valueOf(LIP.getRound_counter().getCount());
        data[LIP_MQ_RST] = String.valueOf(ExtractValue(mq_rst.getStatisticalData().getSummary()));
        data[LIP_MQ_SYM] = String.valueOf(ExtractValue(mq_sym.getStatisticalData().getSummary()));
        data[LIP_EQ_RST] = String.valueOf(ExtractValue(eq_rst.getStatisticalData().getSummary()));
        data[LIP_EQ_SYM] = String.valueOf(ExtractValue(eq_sym.getStatisticalData().getSummary()));
        data[LIP_EQs] = String.valueOf(LIP.getEq_counter().getCount());
        data[LIP_STATES] = String.valueOf(result.size());
        data[LIP_TOTAL_RST] = String.valueOf(ExtractValue(mq_rst.getStatisticalData().getSummary())+ ExtractValue(eq_rst.getStatisticalData().getSummary()));
        data[LIP_TOTAL_SYM] = String.valueOf(ExtractValue(mq_sym.getStatisticalData().getSummary())+ ExtractValue(eq_sym.getStatisticalData().getSummary()));
        data[COMPONENTS] = String.valueOf(LIP.getSigmaFamily().size());
        // learning statistics
        System.out.println("Rounds: " + LIP.getRound_counter().getCount());
        System.out.println("#EQs: " + LIP.getEq_counter().getCount());
        System.out.println(mq_rst.getStatisticalData());
        System.out.println(mq_sym.getStatisticalData());
        System.out.println(eq_rst.getStatisticalData());
        System.out.println(eq_sym.getStatisticalData());


        // profiling
        SimpleProfiler.logResults();
    }

    public static void learnProductMealy(CompactMealy mealyss, Alphabet<String> alphabet){

        Utils.getInstance();
        // SUL simulator
        SUL<String, Word<String>> sulSim = new MealySimulatorSUL<>(mealyss, Utils.OMEGA_SYMBOL);

        //////////////////////////////////
        // Setup objects related to MQs //
        //////////////////////////////////

        // Counters for MQs
        StatisticSUL<String, Word<String>> mq_sym = new SymbolCounterSUL<>("MQ", sulSim);
        StatisticSUL<String, Word<String>> mq_rst = new ResetCounterSUL<>("MQ", mq_sym);

        // SUL for counting queries wraps sul
        SUL<String, Word<String>> mq_sul = mq_rst;

        // use caching to avoid duplicate queries

        // SULs for associating the IncrementalMealyBuilder 'mq_cbuilder' to MQs
        mq_sul = SULCache.createDAGCache(alphabet, mq_rst);


        MembershipOracle<String, Word<Word<String>>> mqOracle = new SULOracle<String, Word<String>>(mq_sul);


        //////////////////////////////////
        // Setup objects related to EQs //
        //////////////////////////////////

        // Counters for EQs
        StatisticSUL<String, Word<String>> eq_sym = new SymbolCounterSUL<>("EQ", sulSim);
        StatisticSUL<String, Word<String>> eq_rst = new ResetCounterSUL<>("EQ", eq_sym);

        // SUL for counting queries wraps sul
        SUL<String, Word<String>> eq_sul = eq_rst;

        // SULs for associating the IncrementalMealyBuilder 'cbuilder' to EQs
        eq_sul = SULCache.createDAGCache(alphabet, eq_rst);

        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle = null;
        eqOracle = buildEqOracle(eq_sul);

        StatisticSUL<String, Word<String>> eq_new = new SymbolCounterSUL<>("Cache EQ", eq_sul);

        Experiment experiment = learningLStarM(alphabet, mealyss, mqOracle, eqOracle);


//        // statistics array

        data[LSTAR_MQ_RST] = String.valueOf(ExtractValue(mq_rst.getStatisticalData().getSummary()));
        data[LSTAR_MQ_SYM] = String.valueOf(ExtractValue(mq_sym.getStatisticalData().getSummary()));
        data[LSTAR_EQ_RST] = String.valueOf(ExtractValue(eq_rst.getStatisticalData().getSummary()));
        data[LSTAR_EQ_SYM] = String.valueOf(ExtractValue(eq_sym.getStatisticalData().getSummary()));
        data[LSTAR_EQs] = String.valueOf(experiment.getRounds());
        data[LSTAR_STATES] = String.valueOf(((CompactMealy<?, ?>) experiment.getFinalHypothesis()).size());
        data[LSTAR_TOTAL_RST] = String.valueOf(ExtractValue(mq_rst.getStatisticalData().getSummary())+ ExtractValue(eq_rst.getStatisticalData().getSummary()));
        data[LSTAR_TOTAL_SYM] = String.valueOf(ExtractValue(mq_sym.getStatisticalData().getSummary())+ ExtractValue(eq_sym.getStatisticalData().getSummary()));


        // learning statistics
        System.out.println("Rounds: " + experiment.getRounds().getCount());
        System.out.println(mq_rst.getStatisticalData());
        System.out.println(mq_sym.getStatisticalData());
        System.out.println(eq_rst.getStatisticalData());
        System.out.println(eq_sym.getStatisticalData());
        System.out.println(eq_new.getStatisticalData());


        // profiling
        SimpleProfiler.logResults();
    }

    private static Experiment<MealyMachine<?, String,?, Word<String>>> learningLStarM(Alphabet<String> alphabet,
                                                                                      CompactMealy<String, Word<String>> mealyss,
                                                                                      MembershipOracle<String, Word<Word<String>>> mqOracle,
                                                                                      EquivalenceOracle<? super MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle){
//        initPrefixes.add(Word.epsilon());
//        List<Word<String>> initSuffixes = new ArrayList<>();
//        Word<String> word = Word.epsilon();
//        for (String symbol : mealyss.getInputAlphabet()) {
//            initSuffixes.add(word.append(symbol));
//        }

        // construct standard L*M instance
        ExtensibleLStarMealyBuilder<String, Word<String>> builder = new ExtensibleLStarMealyBuilder<String, Word<String>>();
        builder.setAlphabet(alphabet);
        builder.setOracle(mqOracle);
//        builder.setCexHandler(handler);
//        builder.setClosingStrategy(strategy);

        ExtensibleLStarMealy<String, Word<String>> learner = builder.create();

        // The experiment will execute the main loop of active learning
        Experiment.MealyExperiment<String, Word<String>> experiment = new Experiment.MealyExperiment<String, Word<String>>(learner, eqOracle,
                alphabet);

        experiment.run();
        return experiment;
    }


    private static EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> buildEqOracle(
            SUL<String, Word<String>> eq_sul) {
        MembershipOracle<String, Word<Word<String>>> oracleForEQoracle = new SULOracle<>(eq_sul);

        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle;
        return new WpMethodEQOracle<>(oracleForEQoracle, 4);
    }


    private static CompactMealy<String, Word<String>> LoadMealy(File fsm_file) {
        // TODO Auto-generated method stub
        InputModelDeserializer<String, CompactMealy<String, Word<String>>> parser_1 = DOTParsers
                .mealy(MEALY_EDGE_WORD_STR_PARSER);
        CompactMealy<String, Word<String>> mealy = null;
        String file_name = fsm_file.getName();
        if (file_name.endsWith("txt")) {
            try {
                mealy = Utils.getInstance().loadMealyMachine(fsm_file);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return mealy;
        } else if (file_name.endsWith("dot")) {
            try {
                mealy = parser_1.readModel(fsm_file).model;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return mealy;
        }

        return null;
    }

    public static final Function<Map<String, String>, Pair<@Nullable String, @Nullable Word<String>>> MEALY_EDGE_WORD_STR_PARSER = attr -> {
        final String label = attr.get(VisualizationHelper.EdgeAttrs.LABEL);
        if (label == null) {
            return Pair.of(null, null);
        }

        final String[] tokens = label.split("/");

        if (tokens.length != 2) {
            return Pair.of(null, null);
        }

        Word<String> token2 = Word.epsilon();
        token2 = token2.append(tokens[1]);
        return Pair.of(tokens[0], token2);
    };

    private static int ExtractValue(String string_1) {
        // TODO Auto-generated method stub
        int value_1 = 0;
        int j = string_1.lastIndexOf(" ");
        String string_2 = "";
        if (j >= 0) {
            string_2 = string_1.substring(j + 1);
        }
        value_1 = Integer.parseInt(string_2);
        return value_1;
    }

}
