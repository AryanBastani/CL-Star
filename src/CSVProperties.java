import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class CSVProperties {

    private Properties props;

    private static CSVProperties instance;
    private CSVProperties() { loadProperties(); }

    public static CSVProperties getInstance() {
        if(instance == null) instance = new CSVProperties();
        return instance;
    }

    public void loadProperties(){
        File f = new File(".csvProps");
        loadProperties(f);
    }
    public void loadProperties(File f) {
        if (props != null) {
            props.clear();
        } else {
            props = new Properties();
        }

        if (f.exists()) {
            InputStream in;
            try {
                in = new FileInputStream(f);
                props.load(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getIndex(String key){
        String default_value = props.getProperty("defaultValue");
        return Integer.valueOf(props.getProperty(key, default_value));
    }

    public String[] getResults_header(){
        String [] hearder = new String[]{
                "FILE_NAME", "STATES", "INPUTS",
                "LSTAR_rndWords_MQ_SYM", "LSTAR_rndWords_MQ_RST", "LSTAR_rndWords_EQ_SYM", "LSTAR_rndWords_EQ_RST", "LSTAR_rndWords_TOTAL_SYM",
                "LSTAR_rndWords_TOTAL_RST", "LSTAR_rndWords_EQs",
                "LSTAR_rndWalk_MQ_SYM", "LSTAR_rndWalk_MQ_RST", "LSTAR_rndWalk_EQ_SYM", "LSTAR_rndWalk_EQ_RST",
                "LSTAR_rndWalk_TOTAL_SYM", "LSTAR_rndWalk_TOTAL_RST", "LSTAR_rndWalk_EQs",
                "LSTAR_wp_MQ_SYM", "LSTAR_wp_MQ_RST", "LSTAR_wp_EQ_SYM", "LSTAR_wp_EQ_RST",
                "LSTAR_wp_TOTAL_SYM", "LSTAR_wp_TOTAL_RST", "LSTAR_wp_EQs",
                "LIP_wp_rndWalk_MQ_SYM", "LIP_wp_rndWalk_MQ_RST", "LIP_wp_rndWalk_EQ_SYM", "LIP_wp_rndWalk_EQ_RST",
                "LIP_wp_rndWalk_TOTAL_SYM", "LIP_wp_rndWalk_TOTAL_RST", "LIP_wp_rndWalk_EQs", "LIP_wp_rndWalk_COMPONENTS",
                "LIP_wp_rndWalk_ROUNDS", "LIP_wp_rndWords_MQ_SYM", "LIP_wp_rndWords_MQ_RST", "LIP_wp_rndWords_EQ_SYM", "LIP_wp_rndWords_EQ_RST",
                "LIP_wp_rndWords_TOTAL_SYM", "LIP_wp_rndWords_TOTAL_RST", "LIP_wp_rndWords_EQs", "LIP_wp_rndWords_COMPONENTS",
                "LIP_wp_rndWords_ROUNDS", "LIP_rndWalk_rndWalk_MQ_SYM", "LIP_rndWalk_rndWalk_MQ_RST", "LIP_rndWalk_rndWalk_EQ_SYM",
                "LIP_rndWalk_rndWalk_EQ_RST", "LIP_rndWalk_rndWalk_TOTAL_SYM", "LIP_rndWalk_rndWalk_TOTAL_RST", "LIP_rndWalk_rndWalk_EQs",
                "LIP_rndWalk_rndWalk_COMPONENTS", "LIP_rndWalk_rndWalk_ROUNDS", "LIP_rndWords_rndWords_MQ_SYM", "LIP_rndWords_rndWords_MQ_RST",
                "LIP_rndWords_rndWords_EQ_SYM", "LIP_rndWords_rndWords_EQ_RST", "LIP_rndWords_rndWords_TOTAL_SYM", "LIP_rndWords_rndWords_TOTAL_RST",
                "LIP_rndWords_rndWords_EQs", "LIP_rndWords_rndWords_COMPONENTS", "LIP_rndWords_rndWords_ROUNDS", "LIP_wp_wp_MQ_SYM", "LIP_wp_wp_MQ_RST",
                "LIP_wp_wp_EQ_SYM", "LIP_wp_wp_EQ_RST", "LIP_wp_wp_TOTAL_SYM", "LIP_wp_wp_TOTAL_RST", "LIP_wp_wp_EQs", "LIP_wp_wp_COMPONENTS",
                "LIP_wp_wp_ROUNDS", "CACHE"};
        return hearder;
    }
}

