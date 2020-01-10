import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonVar {

    public final static Path SERVER_PATH = Paths.get(System.getProperty("user.dir").concat("/StorageRemote")).toAbsolutePath();
    public final static String DB_DESTINATION = Paths.get(System.getProperty("user.dir").concat("/15_GB_DropBox_Server/src/main/resources/DB/usersDB.db")).toAbsolutePath().toString();
    public final static Path CLIENT_PATH = Paths.get(System.getProperty("user.dir").concat("/StorageLocal")).toAbsolutePath();
    public static int HOST_PORT = 8189;
    public static Integer MAX_OBJECT_SIZE = 2047 * 1024 * 1024;
    public static String HOST_NAME = "localhost";


    /**
     * TODO не уверен что это правильное место но другого варианта достать из хендлера данные не нашел.
     */

    public static CommandAnswer commandForSend = new CommandsList(); //объект "команды пользователя"
    public static String clientName = "ClientFX";
    public static ClientNetListener clientNetListener; // ссылка на клиентское подключение к серверу
    public static StringBuilder commandFromUsersUI = new StringBuilder(); // команда пользователя которая будет упакована в оъект и отправлена в сторону сервера.

    public static Path getPathForUser(String usersLogin) {
        return Paths.get(SERVER_PATH + "/" + usersLogin);
    }


}
