import com.google.common.collect.Lists;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFA;
import de.learnlib.algorithms.lstar.dfa.ClassicLStarDFABuilder;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.datastructure.observationtable.OTUtils;
import de.learnlib.datastructure.observationtable.writer.ObservationTableASCIIWriter;
import de.learnlib.filter.statistic.Counter;
import de.learnlib.filter.statistic.oracle.DFACounterOracle;
import de.learnlib.util.Experiment;
import de.learnlib.util.statistics.SimpleProfiler;
import net.automatalib.automata.fsa.DFA;
import net.automatalib.automata.fsa.impl.compact.CompactDFA;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.*;

public class LearnInParts<I> {
    private Alphabet<I> alphabet;
//    private MembershipOracle.DFAMembershipOracle<I> sul;
    private EquivalenceOracle.DFAEquivalenceOracle<I> eqOracle;
    private final DFACounterOracle<I> mqOracle;
//    private ClassicLStarDFA<I> lstar;
    private List<Alphabet<I>> sigmaFamily;

    public Alphabet<I> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Alphabet<I> alphabet) {
        this.alphabet = alphabet;
    }

    public EquivalenceOracle.DFAEquivalenceOracle<I> getEqOracle() {
        return eqOracle;
    }

    public void setEqOracle(EquivalenceOracle.DFAEquivalenceOracle<I> eqOracle) {
        this.eqOracle = eqOracle;
    }

    public DFACounterOracle<I> getMqOracle() {
        return mqOracle;
    }

    public List<Alphabet<I>> getSigmaFamily() {
        return sigmaFamily;
    }

    public void setSigmaFamily(List<Alphabet<I>> sigmaFamily) {
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

    private Counter round_counter;

    private Counter eq_counter;


    public LearnInParts(Alphabet<I> alphabet,
                        DFACounterOracle<I> mqOracle,
                        EquivalenceOracle.DFAEquivalenceOracle<I> eqOracle){
        this.eqOracle = eqOracle;
//        this.sul = sul;
        this.alphabet = alphabet;

        // oracle for counting queries wraps SUL
//        DFACounterOracle<I> mqOracle = new DFACounterOracle<>(sul, "membership queries");
        this.mqOracle = mqOracle;

        // construct L* instance
//        ClassicLStarDFA<I> lstar =
//                new ClassicLStarDFABuilder<I>().withAlphabet(alphabet) // input alphabet
//                        .withOracle(mqOracle) // membership oracle
//                        .create();
//        this.lstar = lstar;
        this.round_counter = new Counter("Decomposed Learning  rounds", "#");
        this.eq_counter = new Counter("Total number of equivalence queries", "#");
        this.mq_counter = mqOracle.getStatisticalData();
    }

//    public LearnInParts(Alphabet<I> alphabet,
//                        DFACounterOracle<I> mqOracle,
//                        DFACounterOracle<I> cachedMq,
//                        EquivalenceOracle.DFAEquivalenceOracle<I> eqOracle){
//        this.eqOracle = eqOracle;
////        this.sul = sul;
//        this.alphabet = alphabet;
//
//        // oracle for counting queries wraps SUL
////        DFACounterOracle<I> mqOracle = new DFACounterOracle<>(sul, "membership queries");
//        this.mqOracle = mqOracle;
//
//        // construct L* instance
////        ClassicLStarDFA<I> lstar =
////                new ClassicLStarDFABuilder<I>().withAlphabet(alphabet) // input alphabet
////                        .withOracle(mqOracle) // membership oracle
////                        .create();
////        this.lstar = lstar;
//        this.round_counter = new Counter("Decomposed Learning  rounds", "#");
//        this.eq_counter = new Counter("Total number of equivalence queries", "#");
//        this.mq_counter = mqOracle.getStatisticalData();
//    }

    public CompactDFA<I> run(List<Alphabet<I>> sigmaF){
        sigmaFamily = sigmaF;
        round_counter.increment();
        List<CompactDFA<I>> learnedParts = new ArrayList<>();
        ProductDFA<I> productDFA = null;
        for(Alphabet<I> sigmai : sigmaFamily ){
            ClassicLStarDFA<I> partialLstar = new ClassicLStarDFABuilder<I>()
                    .withAlphabet(sigmai) // input alphabet
                    .withOracle(mqOracle) // membership oracle
                    .create();
            Experiment.DFAExperiment<I> experiment = new Experiment.DFAExperiment<>(partialLstar, eqOracle, sigmai);
            // turn on time profiling, log, and run
            experiment.setProfile(true);
            experiment.setLogModels(true);
            experiment.run();
            // get learned model
            CompactDFA<I> partialH = (CompactDFA<I>) experiment.getFinalHypothesis();

            eq_counter.increment(experiment.getRounds().getCount());

            learnedParts.add(partialH);
            if (productDFA== null){
                productDFA = new ProductDFA<>(partialH);
            }
            else productDFA.interleaving_parallel_composition(partialH);
        }

        DFA<?, I> hypothesis = (DFA<?, I>) productDFA.getDfa();
        @Nullable DefaultQuery<I, Boolean> ce;

        while ((ce = eqOracle.findCounterExample(hypothesis,
                alphabet)) != null) {
            System.out.println("******************$$$$$$$$$$$$$$$$$$************$$$$$$$$$$$$************");
            System.out.println(ce);
            System.out.println();
            System.out.println();
            round_counter.increment();
            eq_counter.increment();
            List<Alphabet<I>> dependentSets = dependent_sets(ce.getInput(), sigmaFamily, hypothesis);

//            sigmaFamily = composition(sigmaFamily, dependentSets);
            ArrayList<I> mergedSet = new ArrayList<>();
            ArrayList<CompactDFA<I>> trashParts = new ArrayList<>();
            for (Alphabet<I> sigmai : dependentSets){
                int i = sigmaFamily.indexOf(sigmai);
                sigmaFamily.remove(sigmai);
                trashParts.add(learnedParts.remove(i));
                mergedSet.addAll(sigmai);
            }
            Alphabet<I> mergedAlphabet = Alphabets.fromList(mergedSet);

            // Learn the single merged component
            ClassicLStarDFA<I> partialLstar = new ClassicLStarDFABuilder<I>()
                    .withAlphabet(mergedAlphabet) // input alphabet
                    .withOracle(mqOracle) // membership oracle
                    .create();
            Experiment.DFAExperiment<I> experiment = new Experiment.DFAExperiment<>(partialLstar, eqOracle, mergedAlphabet);
            // turn on time profiling, log, and run
            experiment.setProfile(true);
            experiment.setLogModels(true);
            experiment.run();
            // get learned model
            CompactDFA<I> partialH = (CompactDFA<I>) experiment.getFinalHypothesis();
            eq_counter.increment(experiment.getRounds().getCount());


            sigmaFamily.add(mergedAlphabet);
            learnedParts.add(partialH);

            productDFA = null;
            for (CompactDFA<I> component : Lists.reverse(learnedParts)){
//                Visualization.visualize(component, component.getInputAlphabet());

                if (productDFA== null){
                    productDFA = new ProductDFA<>(component);
                }
                else productDFA.interleaving_parallel_composition(component);
            }
            hypothesis = (DFA<?, I>) productDFA.getDfa();
//            Visualization.visualize(productDFA.getDfa(), productDFA.getDfa().getInputAlphabet());

        }
        System.out.println(sigmaFamily.toString());
        return productDFA.getDfa();
    }

    private void computeCounters(Experiment.DFAExperiment<I> experiment, ClassicLStarDFA<I> lstar ) {
        // profiling
        System.out.println(" --------- simple profiler: ---------");
        System.out.println(SimpleProfiler.getResults());

        // learning statistics
        System.out.println("------ learning statistics---------- ");
        if (experiment != null)
            System.out.println(experiment.getRounds().getSummary());
        System.out.println(mqOracle.getStatisticalData().getSummary());
        if (lstar != null) {
            System.out.println("Final observation table:");
                    new ObservationTableASCIIWriter<>().write(lstar.getObservationTable(), System.out);
            try {
                OTUtils.displayHTMLInBrowser(lstar.getObservationTable());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("       **********************************************************");

    }

    private List<Alphabet<I>> composition (List<Alphabet<I>> sigmaFamily, List<Alphabet<I>> dependentSets){
        ArrayList<I> mergedSet = new ArrayList<>();
        for (Alphabet<I> sigmai : dependentSets){
            int i = sigmaFamily.indexOf(sigmai);
            sigmaFamily.remove(sigmai);

            mergedSet.addAll(sigmai);
        }
        Alphabet mergedAlphabet = Alphabets.fromList(mergedSet);
        sigmaFamily.add(mergedAlphabet);
        return sigmaFamily;
    }

    private List<Alphabet<I>> dependent_sets(Word<I> ce, List<Alphabet<I>> sigmaFamily, DFA hypothesis){
        ce = this.cut_ce(ce, hypothesis);
        List<Alphabet<I>>  involvedSets = involved_sets(ce, sigmaFamily);
        List<ArrayList> subsets = new ArrayList();

        for(int k=2; k<involvedSets.size(); k++){
            subsets = k_combinations(k, involvedSets);
            for(ArrayList list: subsets){
                Alphabet<I> merged_list = merge_parts(list);
                Word<I> ce_prime = projection(ce, merged_list);
                if (check_for_ce(ce_prime, hypothesis)){
                    return list;
                }

            }
        }
        return involvedSets;
    }

    private List<Alphabet<I>> involved_sets(Word<I> ce, List<Alphabet<I>> sigmaFamily){
        List<Alphabet<I>> dependentSets = new ArrayList<>();

        List<I> ceList = ce.asList();
        for (Alphabet<I> sigmai : sigmaFamily){
            for (I action : sigmai){
                if (ceList.contains(action)){
                    dependentSets.add(sigmai);
                    break;
                }
            }
        }
        return dependentSets;
    }

    private Word<I> cut_ce(Word<I> ce, DFA hypothesis){
        for(Word<I> prefix: ce.prefixes(false) ){
            if (check_for_ce(prefix, hypothesis)){
                return prefix;
            }
        }
        return ce;
    }

    private Word<I> projection(Word<I> word, Alphabet<I> alphabet){
        ArrayList<I> input = new ArrayList<>();
        for (I action: word ){
            if (alphabet.contains(action)){
                input.add(action);
            }
        }
        return Word.fromList(input);
    }

    private Boolean check_for_ce(Word<I> ce, DFA hypothesis){
        Boolean sul_answer = this.mqOracle.answerQuery(ce);
        Boolean hypothesis_answer = hypothesis.computeStateOutput(hypothesis.getInitialState(), ce);
        if (sul_answer != hypothesis_answer){
            return true;
        }
        return false;
    }

    private Alphabet<I> merge_parts(List<Alphabet<I>> list){
        ArrayList<I> mergedSet = new ArrayList<>();
        for (Alphabet<I> sigmai : list){
            mergedSet.addAll(sigmai);
        }
        return Alphabets.fromList(mergedSet);
    }

    private List<ArrayList> k_combinations(int k, List<Alphabet<I>> input){
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

}
