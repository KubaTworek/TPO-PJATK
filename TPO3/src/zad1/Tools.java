/**
 *
 *  @author Tworek Jakub S25646
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tools {

    public static Options createOptionsFromYaml(String fileName) {
        Yaml yaml = new Yaml();
        try (InputStream input = Files.newInputStream(Paths.get(fileName));) {
            Map<String, Object> obj = yaml.load(input);
            Map<String, List<String>> clientsMap = new LinkedHashMap<>();
            Map<String, Object> clientsObj = (Map<String, Object>) obj.get("clientsMap");
            for (String key : clientsObj.keySet()) {
                List<String> requests = (List<String>) clientsObj.get(key);
                clientsMap.put(key, requests);
            }
            return new Options(
                    (String) obj.get("host"),
                    (int) obj.get("port"),
                    (boolean) obj.get("concurMode"),
                    (boolean) obj.get("showSendRes"),
                    clientsMap
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
