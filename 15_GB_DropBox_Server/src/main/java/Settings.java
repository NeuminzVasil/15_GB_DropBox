import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {

    public static int HOST_PORT = 8189;
    public static String HOST_NAME = "localhost";
    public static Path SERVER_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageRemote")).toAbsolutePath();
    public static Path CLIENT_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageLocal")).toAbsolutePath();
    public static Integer MAX_OBJECT_SIZE = 2047 * 1024 * 1024;


    public static void main(String[] args) {

        System.out.println(CLIENT_PATH);
        System.out.println(SERVER_PATH);

    }

}
