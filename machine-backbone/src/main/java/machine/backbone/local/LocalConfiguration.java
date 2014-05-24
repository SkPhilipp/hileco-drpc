package machine.backbone.local;

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
 * Setters and getters on this method actually write and load files, a {@link LocalConfiguration} object is
 * created with an initial basePath and should be {@link #initialize()}'d before use, to create the basePath.
 * <p/>
 * {@link IOException}s are usually only thrown when privileges are missing, getters on missing files will return null.
 */
public class LocalConfiguration {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String PROPERTY_SERVER = "local.json";
    private static final String PROPERTY_MANAGEMENT_URL = "management-url.json";
    private final String basePath;

    public LocalConfiguration(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Creates all missing directories in {@link #basePath}.
     *
     * @throws IOException if creating the directories erred, or the path is not read & writeable.
     */
    public void initialize() throws IOException {
        Path path = Paths.get(basePath);
        Files.createDirectories(path);
        if(!Files.isWritable(path)){
            throw new IOException("The configuration directory is not writeable.");
        }
        if(!Files.isReadable(path)){
            throw new IOException("The configuration directory is not readable.");
        }
    }

    private <T extends Serializable> T readFromFile(String filename, Class<T> type) {
        try {
            Path path = Paths.get(basePath, filename);
            if (Files.exists(path)) {
                InputStream fileStream = Files.newInputStream(path);
                return OBJECT_MAPPER.readValue(fileStream, type);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write to a configuration file; This should have been caught during initialization.");
        }
    }

    private <T extends Serializable> void writeToFile(String filename, T value) {
        try {
            Path path = Paths.get(basePath, filename);
            OutputStream fileStream = Files.newOutputStream(path);
            OBJECT_MAPPER.writeValue(fileStream, value);
            fileStream.close();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write to a configuration file; This should have been caught during initialization.");
        }
    }

    public Server getServer() {
        return readFromFile(PROPERTY_SERVER, Server.class);
    }

    public void setServer(Server localMachine) {
        writeToFile(PROPERTY_SERVER, localMachine);
    }

    public String getManagementURL() {
        return readFromFile(PROPERTY_MANAGEMENT_URL, String.class);
    }

    public void setManagementURL(String managementURL) {
        writeToFile(PROPERTY_MANAGEMENT_URL, managementURL);
    }

}
