import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {

    public static int HOST_PORT = 8189;
    public static String HOST_NAME = "localhost";
    public final static Path SERVER_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageRemote")).toAbsolutePath();
    public final static Path CLIENT_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageLocal")).toAbsolutePath();
    public static Integer MAX_OBJECT_SIZE = 2047 * 1024 * 1024;
    public final static String DB_DESTINATION = "D:\\Skills\\_Repository\\15_GB_DropBox\\15_GB_DropBox_Server\\src\\main\\resources\\DB\\usersDB.db";


    public static void main(String[] args) {

        System.out.println(CLIENT_PATH);
        System.out.println(SERVER_PATH);

    }

}
