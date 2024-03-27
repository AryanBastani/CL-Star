import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class LearnLibProperties {

    public static final String MAX_STEPS 		 = "maxSteps";
    public static final String RESET_STEPS_COUNT = "resetStepsCount";

    public static final String RESTART_PROBABILITY = "restartProbability";

    public static final String MAX_TESTS  			= "maxTests";
    public static final String BOUND  				= "bound";
    public static final String MAX_LENGTH 			= "maxLength";

    public static final String MIN_LENGTH 			= "minLength";
    public static final String RND_LENGTH 			= "rndLength";
    public static final String MAX_DEPTH 			= "maxDepth";

    public static final String REVAL_MODE 			= "reval_using";
    public static final String REVAL_OT 			= "OT";
    public static final String REVAL_LEARNER		= "Learner";

    public static final String PROJECTION = "projection";

    public static final String RND_WALK = "rndWalk_";
    public static final String RND_WORDS = "rndWords_";
    public static final String WP 	 = "wp_";
    public static final String W 	 = "w_";
    public static final String WEQ 	 = "whyp_";
    public static final String WRND 	 = "wrndhyp_";

    private Properties props;

    private static LearnLibProperties instance;

    private int rndWalk_maxSteps;
    private int rndWords_minLength;
    private double rndWalk_restartProbability;
    private boolean rndWalk_resetStepCount;

    private String revalMode;

    private int rndWords_maxTests;
    private int rndWords_maxLength;

    private int whyp_minLen;
    private int whyp_rndLen;
    private int whyp_bound;

    private int w_maxDepth;

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

        rndWalk_restartProbability 	= Double .valueOf(props.getProperty(RND_WALK+RESTART_PROBABILITY, "0.03"));
        rndWalk_maxSteps 			= Integer.valueOf(props.getProperty(RND_WALK+MAX_STEPS, "10000"));
        rndWalk_resetStepCount 		= Boolean.valueOf(props.getProperty(RND_WALK+RESET_STEPS_COUNT, "true"));

        rndWords_minLength 			= Integer.valueOf(props.getProperty(RND_WORDS+MIN_LENGTH, "100"));
        rndWords_maxLength 			= Integer.valueOf(props.getProperty(RND_WORDS+MAX_LENGTH, "200"));
        rndWords_maxTests  			= Integer.valueOf(props.getProperty(RND_WORDS+MAX_TESTS, "500"));


        w_maxDepth 				= Integer.valueOf(props.getProperty(W+MAX_DEPTH,"2"));

        revalMode				= String.valueOf(props.getProperty(REVAL_MODE,REVAL_LEARNER));
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

    public double getRndWalk_restartProbability() {
        return rndWalk_restartProbability;
    }

    public int getRndWalk_maxSteps() {
        return rndWalk_maxSteps;
    }

    public boolean getRndWalk_resetStepsCount() {
        return rndWalk_resetStepCount;
    }

    public int getRndWords_minLength() {
        return rndWords_minLength;
    }

    public int getRndWords_maxTests() {
        return rndWords_maxTests;
    }

    public int getRndWords_maxLength() {
        return rndWords_maxLength;
    }

    public int getW_maxDepth() {
        return w_maxDepth;
    }

    public String getRevalMode() {
        return revalMode;
    }

    public int getWhyp_rndLen() {
        return whyp_rndLen;
    }

    public int getWhyp_bound() {
        return whyp_bound;
    }

    public int getWhyp_minLen() {
        return whyp_minLen;
    }

    public void setProjection(boolean projection) {
        this.projection = projection;
    }

    public boolean getProjection() {
        return this.projection;
    }


}