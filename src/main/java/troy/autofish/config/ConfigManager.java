package troy.autofish.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import troy.autofish.FabricModAutofish;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConfigManager {

    private Config config;

    private FabricModAutofish modAutofish;
    private Gson gson;
    private File configFile;

    private Executor executor = Executors.newSingleThreadExecutor();

    public ConfigManager(FabricModAutofish modAutofish) {
        this.modAutofish = modAutofish;
        this.gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        this.configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "autofish.config");
        //run synchronously on first run so our options are available for the Autofish instance
        readConfig(false);
    }

    public void readConfig(boolean async) {

        Runnable task = () -> {
            try {
                //read if exists
                if (configFile.exists()) {
                    String fileContents = FileUtils.readFileToString(configFile, Charset.defaultCharset());
                    config = gson.fromJson(fileContents, Config.class);

                    //If there were any invalid options, write the fixed config
                    if (config.enforceConstraints()) writeConfig(true);

                } else { //write new if no config file exists
                    writeNewConfig();
                }

            } catch (Exception e) {
                e.printStackTrace();
                writeNewConfig();
            }
        };

        if (async) executor.execute(task);
        else task.run();
    }

    public void writeNewConfig() {
        config = new Config();
        writeConfig(false);
    }

    public void writeConfig(boolean async) {
        Runnable task = () -> {
            try {
                if (config != null) {
                    String serialized = gson.toJson(config);
                    FileUtils.writeStringToFile(configFile, serialized, Charset.defaultCharset());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        if (async) executor.execute(task);
        else task.run();
    }

    public Config getConfig() {
        return config;
    }
}
