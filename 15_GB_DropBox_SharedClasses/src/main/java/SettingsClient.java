import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsClient {

    public SettingsClient() throws IOException {
    }

    //    public final static Path SERVER_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageRemote")).toAbsolutePath();
    public final static Path CLIENT_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageLocal")).toAbsolutePath();
    public static int HOST_PORT = 8189;
    public static String HOST_NAME = "localhost";

    public static Integer MAX_OBJECT_SIZE = 2047 * 1024 * 1024;


}
