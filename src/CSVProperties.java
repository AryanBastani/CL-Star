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
}

