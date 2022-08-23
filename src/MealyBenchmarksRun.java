//
///5wise_18.txt
///4wise_19.txt
///5wise_20.txt
///5wise_1.txt
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
import de.learnlib.oracle.equivalence.RandomWMethodEQOracle;
import de.learnlib.oracle.equivalence.RandomWordsEQOracle;
import de.learnlib.oracle.equivalence.WMethodEQOracle;
import de.learnlib.oracle.equivalence.WpMethodEQOracle;
import de.learnlib.oracle.equivalence.mealy.RandomWalkEQOracle;
import de.learnlib.oracle.membership.SULOracle;
import de.learnlib.util.Experiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.commons.util.Pair;
import net.automatalib.serialization.InputModelDeserializer;
import net.automatalib.serialization.dot.DOTParsers;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.visualization.Visualization;
import net.automatalib.visualization.VisualizationHelper;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MealyBenchmarksRun {

    private static final String[] big_fsms = {
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_14.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/4wise_15.txt ",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_7.txt ",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_12.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/6wise_9.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/5wise_4.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_19.txt",
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_18.txt",
    };
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
            "Benchmarks/BCS_SPL/Complete_FSM_files/products/9wise_20.txt",
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
    public static int METHOD = 21;
    public static int CACHE = 22;
    public static int DATA_LEN = 23;
    public static String[] data;


    private static Boolean CACHE_ENABLE = true;
    private static String EQ_METHOD = "wp";
    private static final String RESULTS_PATH = "Results/Final_run/rnd_wp700_treak.csv";
    private static Logger logger;


    public static void main(String[] args) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        logger = Logger.getLogger(dtf.format(now).toString());
        FileHandler fh;
        try {
            String path = "logs/" + dtf.format(now) + ".log";
            fh = new FileHandler(path);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String c : benchmarks){
            data = new String[DATA_LEN];
            File file = new File(c);
            data[FILE_NAME] = c;
            CompactMealy<String, Word<String>> target;
            try {
                target = Utils.getInstance().loadProductMealy(file, "BCS_SPL/Complete_FSM_files/").fsm;
//                Visualization.visualize(target, target.getInputAlphabet());
            } catch (Exception e) {
                logger.warning("problem in loading file");
                logger.warning(c);
                continue;
//                throw new RuntimeException(e);
            }
            if (target.size()>700){
//                if (target.size()<1000)
//                System.out.println("");
//                System.out.println("------------------- BIG FSM FILE _____________________________ ");
//                System.out.println(c);
//                System.out.println();
//                    Utils.writeFile("Benchmarks/mediumFSMs.txt", c  + "   " +  target.size() + "\n"  );
//                    logger.info("BIG FSM:   " + c  + "   " +  target.size() + "\n"  );
                continue;
            }
            logger.info("FSM from : " + c);
            logger.info("#States: " + target.size());
            data[STATES] = Integer.toString(target.size());
            data[INPUTS] = Integer.toString(target.numInputs());
            Alphabet<String> alphabet = target.getInputAlphabet();

            String[] eq_methods = {"rndWalk" ,"rndWalk", "rndWalk", "rndWalk" };
            for(String method:eq_methods){
                logger.info("Partial EQ : " + method);
                logger.info("EQ : " + EQ_METHOD);

                //          // Shuffle the alphabet
                String[] alphArr =  alphabet.toArray(new String[alphabet.size()]);
                Collections.shuffle(Arrays.asList(alphArr));
                alphabet = Alphabets.fromArray(alphArr);
                data[METHOD] = method;
                data[CACHE] = CACHE_ENABLE.toString();
                learnProductMealy(target, alphabet);
                learnMealyInParts(target, alphabet, method );
                Utils.writeDataLineByLine(RESULTS_PATH, data);
            }
        }
    }

    public static void learnMealyInParts(CompactMealy mealyss, Alphabet<String> alphabet, String partial_eq_method){

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
        // SULs for associating the IncrementalMealyBuilder 'mq_builder' to MQs
        if(CACHE_ENABLE){
            mq_sul = SULCache.createDAGCache(alphabet, mq_rst);
        }

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
        if (CACHE_ENABLE){
            eq_sul = SULCache.createDAGCache(alphabet, eq_rst);
        }

        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle = null;
        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> partialEqOracle = null;
        partialEqOracle = buildEqOracle(eq_sul, partial_eq_method);
        eqOracle = buildEqOracle(eq_sul, EQ_METHOD);


        MealyLearnInParts LIP = new MealyLearnInParts(alphabet, mqOracle, eqOracle, partialEqOracle, logger);
        CompactMealy result = LIP.run(eq_sym);



        logger.info("Rounds: " + LIP.getRound_counter().getCount());
        logger.info("#EQs: " + LIP.getEq_counter().getCount());
        logger.info(mq_rst.getStatisticalData().toString());
        logger.info(mq_sym.getStatisticalData().toString());
        logger.info(eq_rst.getStatisticalData().toString());
        logger.info(eq_sym.getStatisticalData().toString());
//        // statistics array
        data[ROUNDS] = String.valueOf(LIP.getRound_counter().getCount());
        data[LIP_MQ_RST] = Utils.ExtractValue(mq_rst.getStatisticalData().getSummary());
        data[LIP_MQ_SYM] = Utils.ExtractValue(mq_sym.getStatisticalData().getSummary());
        data[LIP_EQ_RST] = Utils.ExtractValue(eq_rst.getStatisticalData().getSummary());
        data[LIP_EQ_SYM] = Utils.ExtractValue(eq_sym.getStatisticalData().getSummary());
        data[LIP_EQs] = String.valueOf(LIP.getEq_counter().getCount());
        data[LIP_STATES] = String.valueOf(result.size());
        data[LIP_TOTAL_RST] = String.valueOf(Long.parseLong(Utils.ExtractValue(mq_rst.getStatisticalData().getSummary()))+ Long.parseLong(Utils.ExtractValue(eq_rst.getStatisticalData().getSummary())));
        data[LIP_TOTAL_SYM] = String.valueOf(Long.parseLong(Utils.ExtractValue(mq_sym.getStatisticalData().getSummary()))+ Long.parseLong(Utils.ExtractValue(eq_sym.getStatisticalData().getSummary())));
        data[COMPONENTS] = String.valueOf(LIP.getSigmaFamily().size());
        // learning statistics


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

        // SULs for associating the IncrementalMealyBuilder 'mq_builder' to MQs
        if (CACHE_ENABLE){
            mq_sul = SULCache.createDAGCache(alphabet, mq_rst);
        }

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
        if(CACHE_ENABLE){
            eq_sul = SULCache.createDAGCache(alphabet, eq_rst);
        }

        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle = null;
        eqOracle = buildEqOracle(eq_sul, EQ_METHOD);


        Experiment experiment = learningLStarM(alphabet, mealyss, mqOracle, eqOracle);


        // learning statistics
        logger.info("Rounds: " + experiment.getRounds().getCount());
        logger.info(mq_rst.getStatisticalData().toString());
        logger.info(mq_sym.getStatisticalData().toString());
        logger.info(eq_rst.getStatisticalData().toString());
        logger.info(eq_sym.getStatisticalData().toString());


//        // statistics array
        data[LSTAR_MQ_RST] = Utils.ExtractValue(mq_rst.getStatisticalData().getSummary());
        data[LSTAR_MQ_SYM] = Utils.ExtractValue(mq_sym.getStatisticalData().getSummary());
        data[LSTAR_EQ_RST] = Utils.ExtractValue(eq_rst.getStatisticalData().getSummary());
        data[LSTAR_EQ_SYM] = Utils.ExtractValue(eq_sym.getStatisticalData().getSummary());
        data[LSTAR_EQs] = String.valueOf(experiment.getRounds().getCount());
        data[LSTAR_STATES] = String.valueOf(((CompactMealy<?, ?>) experiment.getFinalHypothesis()).size());
        data[LSTAR_TOTAL_RST] = String.valueOf(Long.parseLong(Utils.ExtractValue(mq_rst.getStatisticalData().getSummary()))+ Long.parseLong(Utils.ExtractValue(eq_rst.getStatisticalData().getSummary())));
        data[LSTAR_TOTAL_SYM] = String.valueOf(Long.parseLong(Utils.ExtractValue(mq_sym.getStatisticalData().getSummary()))+ Long.parseLong(Utils.ExtractValue(eq_sym.getStatisticalData().getSummary())));



        // profiling
        SimpleProfiler.logResults();
    }

    private static Experiment<MealyMachine<?, String,?, Word<String>>> learningLStarM(Alphabet<String> alphabet,
                                                                                      CompactMealy<String, Word<String>> mealyss,
                                                                                      MembershipOracle<String, Word<Word<String>>> mqOracle,
                                                                                      EquivalenceOracle<? super MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle){

        ExtensibleLStarMealyBuilder<String, Word<String>> builder = new ExtensibleLStarMealyBuilder<String, Word<String>>();
        builder.setAlphabet(alphabet);
        builder.setOracle(mqOracle);


        ExtensibleLStarMealy<String, Word<String>> learner = builder.create();

        // The experiment will execute the main loop of active learning
        Experiment.MealyExperiment<String, Word<String>> experiment = new Experiment.MealyExperiment<String, Word<String>>(learner, eqOracle,
                alphabet);

        experiment.run();
        return experiment;
    }


    private static EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> buildEqOracle(
            SUL<String, Word<String>> eq_sul, String eq_method) {
        MembershipOracle<String, Word<Word<String>>> oracleForEQoracle = new SULOracle<>(eq_sul);

        EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle;
        double restartProbability;
        int maxSteps, maxTests, maxLength, minLength, maxDepth, minimalSize, rndLength, bound;
        long rnd_long;
        boolean resetStepCount;
        long tstamp = System.currentTimeMillis();
        Random rnd_seed = new Random(tstamp);

        LearnLibProperties learn_props = LearnLibProperties.getInstance();

        switch (eq_method) {
            case "rndWalk":
                // create RandomWalkEQOracle
                restartProbability = learn_props.getRndWalk_restartProbability();
                maxSteps = learn_props.getRndWalk_maxSteps();
                resetStepCount = learn_props.getRndWalk_resetStepsCount();

                eqOracle = new RandomWalkEQOracle<String, Word<String>>(eq_sul, // sul
                        restartProbability, // reset SUL w/ this probability before a step
                        maxSteps, // max steps (overall)
                        resetStepCount, // reset step count after counterexample
                        rnd_seed // make results reproducible
                );
                logger.info("EquivalenceOracle: RandomWalkEQOracle(" + restartProbability + "," + maxSteps + ","
                        + resetStepCount + ")");
                break;
            case "rndWords":
                // create RandomWordsEQOracle
                maxTests = learn_props.getRndWords_maxTests();
                maxLength = learn_props.getRndWords_maxLength();
                minLength = learn_props.getRndWords_minLength();
                rnd_long = rnd_seed.nextLong();
                rnd_seed.setSeed(rnd_long);

                eqOracle = new RandomWordsEQOracle<>(oracleForEQoracle, minLength, maxLength, maxTests, rnd_seed);
                logger.info("EquivalenceOracle: RandomWordsEQOracle(" + minLength + ", " + maxLength + ", " + maxTests
                        + ", " + rnd_long + ")");
                break;
            case "wp":
                maxDepth = learn_props.getW_maxDepth();
                eqOracle = new WpMethodEQOracle<>(oracleForEQoracle, maxDepth);
                logger.info("EquivalenceOracle: WpMethodEQOracle(" + maxDepth + ")");
                break;
            case "w":
                maxDepth = learn_props.getW_maxDepth();
                eqOracle = new WMethodEQOracle<>(oracleForEQoracle, maxDepth);
                logger.info("EquivalenceOracle: WMethodQsizeEQOracle(" + maxDepth + ")");
                break;
            case "wrnd":
                minimalSize = learn_props.getWhyp_minLen();
                rndLength = learn_props.getWhyp_rndLen();
                bound = learn_props.getWhyp_bound();
                rnd_long = rnd_seed.nextLong();
                rnd_seed.setSeed(rnd_long);

                eqOracle = new RandomWMethodEQOracle<>(oracleForEQoracle, minimalSize, rndLength, bound, rnd_seed, 1);
                logger.info("EquivalenceOracle: RandomWMethodEQOracle(" + minimalSize + "," + rndLength + "," + bound
                        + "," + rnd_long + ")");
                break;
            default:
                maxDepth = 2;
                eqOracle = new WMethodEQOracle<>(oracleForEQoracle, maxDepth);
                logger.info("EquivalenceOracle: WMethodEQOracle(" + maxDepth + ")");
                break;
        }
        return eqOracle;//        return new WpMethodEQOracle<>(oracleForEQoracle, 4);
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

//    private static String ExtractValue(String string_1) {
//        // TODO Auto-generated method stub
//        int value_1 = 0;
//        int j = string_1.lastIndexOf(" ");
//        String string_2 = "";
//        if (j >= 0) {
//            string_2 = string_1.substring(j + 1);
//        }
////        value_1 = Integer.parseInt(string_2);
//        return string_2;
//    }

}
