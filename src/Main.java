//import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Main {
    public static void main(String[] args) throws IOException {
        Utils.writeDataLineByLine("/results.csv");
        Utils.writeDataLineByLine("/results.csv");


    }

    private static Word<String> projection(Word<String> word, Alphabet<String> alphabet){
        List<String> input = new ArrayList<>();
        for (String action: word ){
            System.out.println(action);
            if (alphabet.contains(action)){
                System.out.println("Keeping this action");
                input.add(action);
            }
        }
        Word<String> ce = Word.fromList(input);
        return ce;
    }

    private static List k_combinations(int k, List input){
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

    // generate actual subset by index sequence
    private static List getSubset(List input, int[] subset) {
        List result = new ArrayList();
        for (int i = 0; i < subset.length; i++)
            result.add(input.get(subset[i])) ;
        return result;
    }
}
