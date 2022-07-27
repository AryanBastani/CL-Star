import com.google.common.collect.Lists;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.datastructure.observationtable.OTUtils;
import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;
import de.learnlib.filter.statistic.Counter;
import de.learnlib.util.Experiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;
import net.automatalib.words.impl.ListAlphabet;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.learnlib.api.oracle.MembershipOracle;

import java.util.*;
public class MealyLearnInParts {
    private Alphabet<String> alphabet;
    //    private MembershipOracle.DFAMembershipOracle<String> sul;
    private EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle ;

    private MembershipOracle<String, Word<Word<String>>> mqOracle;
    //    private ClassicLStarDFA<String> lstar;
    private List<Alphabet<String>> sigmaFamily;
    private Counter round_counter;
    private Counter eq_counter;


    public MealyLearnInParts(Alphabet<String> alphabet,
                             MembershipOracle<String, Word<Word<String>>> mqOracle,
                             EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle ){
        this.eqOracle = eqOracle;
        this.alphabet = alphabet;
        this.mqOracle = mqOracle;

        this.round_counter = new Counter("Decomposed Learning  rounds", "#");
        this.eq_counter = new Counter("Total number of equivalence queries", "#");
//        this.mq_counter = mqOracle.getStatisticalData();
    }

    public CompactMealy<String, Word<String>> run(){
        List<Alphabet<String>> initialSimaF = new ArrayList<>();
        for (String action : this.alphabet) {
            Alphabet<String> sigmai = new ListAlphabet<String>(Arrays.asList(action));
            initialSimaF.add(sigmai);
        }
        sigmaFamily = initialSimaF;
        round_counter.increment();
        List<CompactMealy<String, Word<String>>> learnedParts = new ArrayList<>();
        ProductMealy productMealy = null;
        for(Alphabet<String> sigmai : sigmaFamily ){
            ExtensibleLStarMealyBuilder<String, Word<String>> builder = new ExtensibleLStarMealyBuilder<String, Word<String>>();
            builder.setAlphabet(sigmai);
            builder.setOracle(mqOracle);
            ExtensibleLStarMealy<String, Word<String>> learner = builder.create();
            // The experiment will execute the main loop of active learning
            Experiment.MealyExperiment<String, Word<String>> experiment = new Experiment.MealyExperiment<String, Word<String>>(learner, eqOracle,
                    sigmai);
            experiment.run();

            // get learned model
            CompactMealy<String, Word<String>> partialH = (CompactMealy<String, Word<String>>) experiment.getFinalHypothesis();

            eq_counter.increment(experiment.getRounds().getCount());

            learnedParts.add(partialH);
            if (productMealy== null){
                productMealy = new ProductMealy(partialH);
            }
            else productMealy.mergeFSMs(partialH);
        }

        CompactMealy<String, Word<String>> hypothesis = productMealy.getMachine();
        @Nullable DefaultQuery<String, Word<Word<String>>> ce;;

        while ((ce = eqOracle.findCounterExample(hypothesis,
                alphabet)) != null) {
            System.out.println("******************$$$$$$$$$$$$$$$$$$************$$$$$$$$$$$$************");
            System.out.println(ce);
            System.out.println();
            System.out.println();
            round_counter.increment();
            eq_counter.increment();
            List<Alphabet<String>> dependentSets = dependent_sets(ce.getInput(), sigmaFamily, hypothesis);

//            sigmaFamily = composition(sigmaFamily, dependentSets);
            ArrayList<String> mergedSet = new ArrayList<>();
            ArrayList<CompactMealy<String, Word<String>>> trashParts = new ArrayList<>();
            for (Alphabet<String> sigmai : dependentSets){
                int i = sigmaFamily.indexOf(sigmai);
                sigmaFamily.remove(sigmai);
                trashParts.add(learnedParts.remove(i));
                mergedSet.addAll(sigmai);
            }
            Alphabet<String> mergedAlphabet = Alphabets.fromList(mergedSet);

            // Learn the single merged component
            ExtensibleLStarMealyBuilder<String, Word<String>> builder = new ExtensibleLStarMealyBuilder<String, Word<String>>();
            builder.setAlphabet(mergedAlphabet);
            builder.setOracle(mqOracle);
            ExtensibleLStarMealy<String, Word<String>> learner = builder.create();
            // The experiment will execute the main loop of active learning
            Experiment.MealyExperiment<String, Word<String>> experiment = new Experiment.MealyExperiment<String, Word<String>>(learner, eqOracle,
                    mergedAlphabet);
            experiment.run();
            // get learned model
            CompactMealy<String, Word<String>> partialH = (CompactMealy<String, Word<String>>) experiment.getFinalHypothesis();
            eq_counter.increment(experiment.getRounds().getCount());

            sigmaFamily.add(mergedAlphabet);
            learnedParts.add(partialH);

            productMealy = null;
            for (CompactMealy<String, Word<String>>component : Lists.reverse(learnedParts)){
//                Visualization.visualize(component, component.getInputAlphabet());

                if (productMealy== null){
                    productMealy = new ProductMealy(component);
                }
                else productMealy.mergeFSMs(component);
            }
            hypothesis = productMealy.getMachine();
//            Visualization.visualize(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());

        }
        System.out.println(sigmaFamily.toString());
        return productMealy.getMachine();
    }

//    private void computeCounters(Experiment.DFAExperiment<String> experiment, ClassicLStarDFA<String> lstar ) {
//        // profiling
//        System.out.println(" --------- simple profiler: ---------");
//        System.out.println(SimpleProfiler.getResults());
//
//        // learning statistics
//        System.out.println("------ learning statistics---------- ");
//        if (experiment != null)
//            System.out.println(experiment.getRounds().getSummary());
//        System.out.println(mqOracle.getStatisticalData().getSummary());
//        if (lstar != null) {
//            System.out.println("Final observation table:");
//            new ObservationTableASCIIWriter<>().write(lstar.getObservationTable(), System.out);
//            try {
//                OTUtils.displayHTMLInBrowser(lstar.getObservationTable());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        System.out.println("       **********************************************************");
//
//    }

    private List<Alphabet<String>> dependent_sets(Word<String> ce, List<Alphabet<String>> sigmaFamily, CompactMealy hypothesis){
        ce = this.cut_ce(ce, hypothesis);
        List<Alphabet<String>>  involvedSets = involved_sets(ce, sigmaFamily);
        List<ArrayList> subsets = new ArrayList();

        for(int k=2; k<involvedSets.size(); k++){
            subsets = k_combinations(k, involvedSets);
            for(ArrayList list: subsets){
                Alphabet<String> merged_list = merge_parts(list);
                Word<String> ce_prime = projection(ce, merged_list);
                if (check_for_ce(ce_prime, hypothesis)){
                    return list;
                }
            }
        }
        return involvedSets;
    }

    private List<Alphabet<String>> involved_sets(Word<String> ce, List<Alphabet<String>> sigmaFamily){
        List<Alphabet<String>> dependentSets = new ArrayList<>();

        List<String> ceList = ce.asList();
        for (Alphabet<String> sigmai: sigmaFamily){
            for (String action : sigmai){
                if (ceList.contains(action)){
                    dependentSets.add(sigmai);
                    break;
                }
            }
        }
        return dependentSets;
    }

    private Word<String> cut_ce(Word<String> ce, CompactMealy hypothesis){
        for(Word<String> prefix: ce.prefixes(false) ){
            if (check_for_ce(prefix, hypothesis)){
                return prefix;
            }
        }
        return ce;
    }

    private Word<String> projection(Word<String> word, Alphabet<String> alphabet){
        ArrayList<String> input = new ArrayList<>();
        for (String action: word ){
            if (alphabet.contains(action)){
                input.add(action);
            }
        }
        return Word.fromList(input);
    }

    private Boolean check_for_ce(Word<String> ce, CompactMealy hypothesis){
        Word<Word<String>> sul_answer = this.mqOracle.answerQuery(ce);
        Word<Word<String>> hypothesis_answer = hypothesis.computeStateOutput(hypothesis.getInitialState(), ce);
//        TODO check is equality works properly
        if (!sul_answer.equals(hypothesis_answer)){
            return true;
        }
        return false;
    }

    private Alphabet<String> merge_parts(List<Alphabet<String>> list){
        ArrayList<String> mergedSet = new ArrayList<>();
        for (Alphabet<String> sigmai : list){
            mergedSet.addAll(sigmai);
        }
        return Alphabets.fromList(mergedSet);
    }

    private List<ArrayList> k_combinations(int k, List<Alphabet<String>> input){
        List subsets = new ArrayList<>();

        int[] s = new int[k];                  // here we'll keep indices
        // pointing to elements in input array

        if (k <= input.size()) {
            // first index sequence: 0, 1, 2, ...
            for (int i = 0; (s[i] = i) < k - 1; i++);
            subsets.add(getSubset(input, s));
            for(;;) {
                int i;
                // find position of item that can be incremented
                for (i = k - 1; i >= 0 && s[i] == input.size() - k + i; i--);
                if (i < 0) {
                    break;
                }
                s[i]++;                    // increment this item
                for (++i; i < k; i++) {    // fill up remaining items
                    s[i] = s[i - 1] + 1;
                }
                subsets.add(getSubset(input, s));
            }
        }
        return subsets;
    }

    private List getSubset(List input, int[] subset) {
        List result = new ArrayList();
        for (int i = 0; i < subset.length; i++)
            result.add(input.get(subset[i])) ;
        return result;
    }

    public Alphabet<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Alphabet<String> alphabet) {
        this.alphabet = alphabet;
    }

    public EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> getEqOracle() {
        return eqOracle;
    }

    public void setEqOracle(EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle) {
        this.eqOracle = eqOracle;
    }

    public MembershipOracle<String, Word<Word<String>>> getMqOracle() {
        return mqOracle;
    }

    public void setMqOracle(MembershipOracle<String, Word<Word<String>>> mqOracle) {
        this.mqOracle = mqOracle;
    }

    public List<Alphabet<String>> getSigmaFamily() {
        return sigmaFamily;
    }

    public void setSigmaFamily(List<Alphabet<String>> sigmaFamily) {
        this.sigmaFamily = sigmaFamily;
    }

    private Counter mq_counter;

    public Counter getMq_counter() {
        return mq_counter;
    }

    public void setMq_counter(Counter mq_counter) {
        this.mq_counter = mq_counter;
    }

    public Counter getRound_counter() {
        return round_counter;
    }

    public void setRound_counter(Counter round_counter) {
        this.round_counter = round_counter;
    }

    public Counter getEq_counter() {
        return eq_counter;
    }

    public void setEq_counter(Counter eq_counter) {
        this.eq_counter = eq_counter;
    }

}
