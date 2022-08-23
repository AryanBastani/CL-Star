//import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import net.automatalib.words.impl.Alphabets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Main {
    public static void main(String[] args) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        Logger logger = Logger.getLogger(dtf.format(now).toString());
        FileHandler fh;
        try {
            String path = "logs/" + dtf.format(now) + ".log";
            // This block configure the logger with handler and formatter
            fh = new FileHandler(path);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            myLog(logger);
            myLog(logger);
            // the following statement is used to log any messages
            logger.info("My first log");

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void myLog(Logger logger) {
        logger.info("Hi How r u?");
    }

    private static Word<String> projection(Word<String> word, Alphabet<String> alphabet){
        List<String> input = new ArrayList<>();
        for (String action: word ){
            System.out.println(action);
            if (alphabet.contains(action)){
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
