package machine.backbone.configuration;

import machine.management.api.entities.Server;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Setters and getters on this method actually write and load files, a {@link Configuration} object is
 * created with an initial basePath and should be {@link #initialize()}'d before use, to create the basePath.
 * <p/>
 * {@link IOException}s are usually only thrown when privileges are missing, getters on missing files will return null.
 */
public class Configuration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String PROPERTY_SERVER = "local-server.json";
    private final String basePath;

    public Configuration(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Creates all missing directories in {@link #basePath}.
     *
     * @throws IOException
     */
    public void initialize() throws IOException {
        Path path = Paths.get(basePath);
        Files.createDirectories(path);
    }

    private <T extends Serializable> T readFromFile(String filename, Class<T> type) throws IOException {
        Path path = Paths.get(basePath, filename);
        if (Files.exists(path)) {
            InputStream fileStream = Files.newInputStream(path);
            return OBJECT_MAPPER.readValue(fileStream, type);
        } else {
            return null;
        }
    }

    private <T extends Serializable> void writeToFile(String filename, T value) throws IOException {
        Path path = Paths.get(basePath, filename);
        OutputStream fileStream = Files.newOutputStream(path);
        OBJECT_MAPPER.writeValue(fileStream, value);
        fileStream.close();
    }

    public Server getServer() throws IOException {
        return readFromFile(PROPERTY_SERVER, Server.class);
    }

    public void setServer(Server localMachine) throws IOException {
        writeToFile(PROPERTY_SERVER, localMachine);
    }

}
