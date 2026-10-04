import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Utility class that contains methods to read a CSV file and a properties file.
 */
public class IOUtils {

    /**
     * Reads a CSV file and returns its contents as a list of String arrays,
     * one array per non-empty line, split on commas.
     *
     * @param csvFile the path to the CSV file
     * @return a list of String arrays, each representing one row of the CSV file
     */
    public static List<String[]> readCsv(String csvFile) {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                rows.add(line.split(","));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        return rows;
    }

    /**
     * Reads a properties file and returns a Properties object.
     *
     * @param configFile the path to the properties file
     * @return Properties object populated from the given file
     */
    public static Properties readPropertiesFile(String configFile) {
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(configFile));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        return appProps;
    }
}
