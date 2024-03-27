import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class LearnLibProperties {

    public static final String MAX_STEPS = "maxSteps";
    public static final String RESET_STEPS_COUNT = "resetStepsCount";

    public static final String RESTART_PROBABILITY = "restartProbability";

    public static final String MAX_TESTS = "maxTests";
    public static final String BOUND = "bound";
    public static final String MAX_LENGTH = "maxLength";

    public static final String MIN_LENGTH = "minLength";
    public static final String RND_LENGTH = "rndLength";
    public static final String MAX_DEPTH = "maxDepth";

    public static final String REVAL_MODE = "revalUsing";
    public static final String REVAL_OT = "OT";
    public static final String REVAL_LEARNER = "Learner";

    public static final String PROJECTION = "projection";

    public static final String RND_WALK = "rndWalk_";
    public static final String RND_WORDS = "rndWords_";
    public static final String WP = "wp_";
    public static final String W = "w_";
    public static final String WEQ = "whyp_";
    public static final String WRND = "wrndhyp_";

    private Properties props;

    private static LearnLibProperties instance;

    private int rndWalkMaxSteps;
    private int rndWordsMinLength;
    private double rndWalkRestartProbability;
    private boolean rndWalkResetStepCount;

    private String revalMode;

    private int rndWordsMaxTests;
    private int rndWordsMaxLength;

    private int whypMinLen;
    private int whypRndLen;
    private int whypBound;

    private int wMaxDepth;

    private boolean projection;

    private LearnLibProperties() { loadProperties(); }

    public static LearnLibProperties getInstance() {
        if(instance == null) instance = new LearnLibProperties();
        return instance;
    }

    public void loadProperties(){
        File f = new File("resources/.learnlib");
        loadProperties(f);
    }

    public void loadProperties(File f){
        initializeProps();

        if(f.exists()){
            loadTheFile(f);
        }
        
        initializeVariables();
    }

    private void initializeVariables(){
        projection = Boolean.valueOf(props.getProperty(PROJECTION, "false"));

        rndWalkRestartProbability = Double.valueOf(props.getProperty(RND_WALK+RESTART_PROBABILITY, "0.03"));
        rndWalkMaxSteps = Integer.valueOf(props.getProperty(RND_WALK+MAX_STEPS, "10000"));
        rndWalkResetStepCount = Boolean.valueOf(props.getProperty(RND_WALK+RESET_STEPS_COUNT, "true"));

        rndWordsMinLength = Integer.valueOf(props.getProperty(RND_WORDS+MIN_LENGTH, "100"));
        rndWordsMaxLength = Integer.valueOf(props.getProperty(RND_WORDS+MAX_LENGTH, "200"));
        rndWordsMaxTests = Integer.valueOf(props.getProperty(RND_WORDS+MAX_TESTS, "500"));

        wMaxDepth = Integer.valueOf(props.getProperty(W+MAX_DEPTH,"2"));

        revalMode = String.valueOf(props.getProperty(REVAL_MODE,REVAL_LEARNER));
    }

    private void initializeProps(){
        if(props!=null){
            props.clear();
        }
        else{
            props = new Properties();
        }
    }

    private void loadTheFile(File f){
        InputStream in;
        try {
            in = new FileInputStream(f);
            props.load(in);
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getRndWalkRestartProbability() {
    return rndWalkRestartProbability;
    }

    public int getRndWalkMaxSteps() {
        return rndWalkMaxSteps;
    }

    public boolean getRndWalkResetStepsCount() {
        return rndWalkResetStepCount;
    }

    public int getRndWordsMinLength() {
        return rndWordsMinLength;
    }

    public int getRndWordsMaxTests() {
        return rndWordsMaxTests;
    }

    public int getRndWordsMaxLength() {
        return rndWordsMaxLength;
    }

    public int getWMaxDepth() {
        return wMaxDepth;
    }

    public String getRevalMode() {
        return revalMode;
    }

    public int getWhypRndLen() {
        return whypRndLen;
    }

    public int getWhypBound() {
        return whypBound;
    }

    public int getWhypMinLen() {
        return whypMinLen;
    }

    public void setProjection(boolean projection) {
        this.projection = projection;
    }

    public boolean getProjection() {
        return this.projection;
    }
}