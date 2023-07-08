package personal.couponmgmt.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesManager {

    private static final Logger LOG = LogManager.getLogger(PropertiesManager.class);

    private static final PropertiesManager INSTANCE = new PropertiesManager();

    private Properties properties;

    public static PropertiesManager getInstance() {
        return PropertiesManager.INSTANCE;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getNumThreads() {
        return Integer.parseInt(getProperty(Constants.NUM_THREADS));
    }

    private PropertiesManager() {

        properties = new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesManager.class.getClassLoader().getResourceAsStream("properties.txt")));
        } catch (Exception e) {
            LOG.error("Exception Reading Properties", e);
        }


    }
}
