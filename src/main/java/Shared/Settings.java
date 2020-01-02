package Shared;


import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {

    public static int hostPort = 8189;
    public static String hostName = "localhost";
    public static Path SERVER_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageRemote")).toAbsolutePath();
    public static Path CLIENT_PATH = Paths.get(System.getProperty("user.dir").concat("\\StorageLocal")).toAbsolutePath();
    public static Integer MAX_OBJECT_SIZE = 2047 * 1024 * 1024;


    public static void main(String[] args) {

        System.out.println(Integer.MAX_VALUE);
        System.out.println(MAX_OBJECT_SIZE);

    }

}
