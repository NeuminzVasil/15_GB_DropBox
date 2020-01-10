import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Date;

public class DBConnect {

    private static Connection connection; // ссылка на подлкючение к бд
    private Statement statement; // подготовка текста запроса к БД
    private ResultSet resultSet; // ссылка на "таблицу-результат" запроса к БД

    /**
     * Констуктор объекта с подключением к базе данных
     */
    public DBConnect() {
        connection = connect();
    }

    /**
     * метод подключения к БД
     *
     * @return ссылка на выполненное подключение.
     * @throws ClassNotFoundException - ошибка подключения
     * @throws SQLException           - ошибка SQL запроса
     */
    private Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + CommonVar.DB_DESTINATION); // NL БД. если подключаемся удаленно то в этом месте указываем IP и PORT
            statement = connection.createStatement();
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("DBConnect.connectToDB(): Не могу подключиться к БД." + e.getMessage());
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        String registeredUserID = null;
        DBConnect dbConnect = new DBConnect();
        registeredUserID = dbConnect.getRegisteredUserID("l1", "p1"); // 2) присвоить полю команды this.registeredUserID полезное значение либо null  +
        System.out.println(registeredUserID);
        dbConnect.disconnect();
    }

    /**
     * метод отключения от базы даннх
     */
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("DBConnect.disconnectFromDB(): Не могу отключиться от базы данных." + e.getMessage());
            System.err.println(e.getMessage());
        }
    }

    /**
     * метод добавления нового пользователя в БД.
     *
     * @param login    логин
     * @param password пароль
     * @return результат выполнения - Int = колличеству вставленных строк (0\1)
     */
    public Integer setNewUserID(String login, String password) {
        String registeredUserID = login + new Date().getTime();
        Integer result = -1;
        try {
            result = statement.executeUpdate("INSERT INTO USERS (LOGIN, PASSWORD, registeredUserID) VALUES ('" + login + "' , '" + password + "' , '" + registeredUserID + "')");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    /**
     * метод получения RegisteredUserID( пользователя по логину и паролю.
     *
     * @param login    - входящий логин
     * @param password - входящий пароль
     * @return
     * @throws SQLException
     */
    public String getRegisteredUserID(String login, String password) {

        String result = null;

        try {
            resultSet = statement.executeQuery(String.format(
                    "SELECT registeredUserID FROM USERS where login = '%s' and password = '%s'", login, password));

            if (resultSet.next()) {
                UsersOnLineList.addUser(resultSet.getString("registeredUserID"), login); // добавляем в список пользователей онлайн
                if (!Files.exists(Paths.get(CommonVar.SERVER_PATH.toString(), login))) // проверяем наличие/создаем папку на сервере с именем логина
                    Files.createDirectories(Paths.get(CommonVar.SERVER_PATH.toString(), login));
                result = resultSet.getString("registeredUserID");
            } else System.err.println("в БД нет информации о логине: " + login);
        } catch (SQLException | IOException e) {
            System.err.println("не могу подключиться к БД для получения ID пользователя.");
            System.err.println(e.getMessage());
        }

        return result;
    }

}
