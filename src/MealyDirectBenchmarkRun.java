
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static java.lang.System.Logger.Level.ERROR;

public class MealyDirectBenchmarkRun {

    private static final String[] benchmarks = {
//            "00001_fsm.dot",
//            "00016_fsm.dot",
//            "00015_fsm.dot",
//            "00014_fsm.dot",
//            "00013_fsm.dot",
//            "00012_fsm.dot",
//            "00010_fsm.dot",
//            "00009_fsm.dot",
//            "00008_fsm.dot",
//            "00007_fsm.dot",
//            "00005_fsm.dot",
            "00003_fsm.dot",
            "00002_fsm.dot",
            "00004_fsm.dot",
            "00011_fsm.dot",
            "00006_fsm.dot",
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
    private static final String BENCHMARK_BASE_PATH = "Benchmarks/BCS_SPL/products_3wise/";
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
            data = new String[21];
            File file = new File(BENCHMARK_BASE_PATH + c);
            data[FILE_NAME] = c;
            CompactMealy<String, Word<String>> target;
            try {
                target = Utils.getInstance().loadMealyMachineFromDot(file);
//                Visualization.visualize(target, target.getInputAlphabet());
            } catch (Exception e) {
                logger.warning("problem in loading file");
                logger.warning(c);
                continue;
//                throw new RuntimeException(e);
            }
            logger.info("FSM from : " + c);
            logger.info("#States: " + target.size());
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
        // SULs for associating the IncrementalMealyBuilder 'mq_builder' to MQs
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

//TODO: change to two separate eq queries
        MealyLearnInParts LIP = new MealyLearnInParts(alphabet, mqOracle, eqOracle, eqOracle, logger);
        CompactMealy result = LIP.run(eq_sym);


        logger.info("Rounds: " + LIP.getRound_counter().getCount());
        logger.info("#EQs: " + LIP.getEq_counter().getCount());
        logger.info( mq_rst.getStatisticalData().toString());
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
        data[LIP_TOTAL_RST] = String.valueOf(Long.parseLong(Utils.ExtractValue(mq_rst.getStatisticalData().getSummary()))+
                Long.parseLong(Utils.ExtractValue(eq_rst.getStatisticalData().getSummary())));
        data[LIP_TOTAL_SYM] = String.valueOf(Long.parseLong(Utils.ExtractValue(mq_sym.getStatisticalData().getSummary()))+
                Long.parseLong(Utils.ExtractValue(eq_sym.getStatisticalData().getSummary())));
        data[COMPONENTS] = String.valueOf(LIP.getSigmaFamily().size());
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

        // log learning statistics
        logger.info("Rounds: " + experiment.getRounds().getCount());
        logger.info(mq_rst.getStatisticalData().toString());
        logger.info(mq_sym.getStatisticalData().toString());
        logger.info(eq_rst.getStatisticalData().toString());
        logger.info(eq_sym.getStatisticalData().toString());
        logger.info(eq_new.getStatisticalData().toString());

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

}
