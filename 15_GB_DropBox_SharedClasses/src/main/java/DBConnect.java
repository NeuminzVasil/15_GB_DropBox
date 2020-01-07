import org.sqlite.SQLiteException;

import java.sql.*;

public class DBConnect {

    private static Connection connection; // ссылка на подлкючение к бд
    private static String registeredUserID; // здесь будет храниться запрашиваемый ID пользователя // ID пользователя мы получаем от программы
    private Statement statement; // подготовка текста запроса к БД
    private ResultSet resultSet; // ссылка на "таблицу-результат" запроса к БД
    private String login;
    private String password;

    public DBConnect() {
        connection = connect();
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * метод обновления всех ID пользователей на новые
     */
    public void resetAllRegisteredID() {
        //statement.executeUpdate() // NL БД. написать метод генерации случайных строковых значений и запись их во все значения в таблице.
    }

    /**
     * метод получения RegisteredUserID( пользователя по логину и паролю.
     *
     * @param login    - входящий логин
     * @param password - входящий пароль
     * @return
     * @throws SQLException
     */
    public String getRegisteredUserID(String login, String password) throws SQLException {
        this.login = login;
        this.password = password;
        resultSet = statement.executeQuery(String.format(
                "SELECT registeredUserID FROM USERS where login = '%s' and password = '%s'", this.login, this.password));
        System.out.println("resultSet.getString(\"registeredUserID\"): " + resultSet.getString("registeredUserID"));
        //disconnectFromDB();
        return resultSet.getString("registeredUserID");
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
            connection = DriverManager.getConnection("jdbc:sqlite:" + SettingsServer.DB_DESTINATION); // NL БД. если подключаемся удаленно то в этом месте указываем IP и PORT
            statement = connection.createStatement();
            System.out.println("DBConnect.connectToDB(): подключился к БД.");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("DBConnect.connectToDB(): Не могу подключиться к БД." + e.getMessage());
            e.getMessage();
        }
        return null;
    }

    /**
     * метод отключения от базы даннх
     */
    public void disconnect() {
        try {
            connection.close();
            System.out.println("DBConnect.disconnectFromDB(): отключилься от базы данных.");
        } catch (SQLException e) {
            System.err.println("DBConnect.disconnectFromDB(): Не могу отключиться от базы данных." + e.getMessage());
            e.getMessage();
        }
    }

    /**
     * метод добавления нового пользователя в БД.
     *
     * @param val1 логин
     * @param val2 пароль
     * @return результат выполнения - Int = колличеству вставленных строк (0\1)
     */
    private Integer addNewUser(String val1, String val2) {
        Integer result = 0;
        try {
            result = statement.executeUpdate("INSERT INTO USERS (LOGIN, PASSWORD) VALUES ('" + val1 + "' , '" + val2 + "')");
        } catch (SQLiteException e) {
            System.err.println(e.getMessage());
        } finally {
            return result;
        }

    } // NL БД. доработать вставку случайного registeredUserID

}
