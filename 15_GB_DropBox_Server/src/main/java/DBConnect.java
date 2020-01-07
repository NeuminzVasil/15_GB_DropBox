import org.sqlite.SQLiteException;

import java.sql.*;

public class DBConnect {

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) {


        try {
            connectToDB(); // NL БД. подключеие к БД
            resultSet = getAllDataResultSet(); // чтение данных из БД
            while (resultSet.next()) { // вывод полученных данных
                System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
            }

            System.out.println(setNewDataIntoUsers("l1", "p1"));// внесение данных в БД

        } catch (ClassNotFoundException e) {
            System.err.println("DBConnect.connect(). Не удалось инициализировать драйвер подключения к СУБД");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("DBConnect.connect(). Не удалось подключиться к БД usersDB.db");
            e.printStackTrace();
        } finally {
            disconnectFromDB();
        }
    }

    public static ResultSet getAllDataResultSet() throws SQLException {
        return statement.executeQuery("SELECT *FROM USERS");
    }

    public static Integer setNewDataIntoUsers(String val1, String val2) {
        Integer result = 0;
        try {
            result = statement.executeUpdate("INSERT INTO USERS (LOGIN, PASSWORD) VALUES ('" + val1 + "' , '" + val2 + "')");
        } catch (SQLiteException e) {
            System.err.println(e.getMessage());
        } finally {
            return result;
        }

    }

    public static Connection connectToDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + SettingsServer.DB_DESTINATION); // NL БД. если подключаемся удаленно то в этом месте указываем IP и PORT
        statement = connection.createStatement();
        return connection;
    }

    public static void disconnectFromDB() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println("DBConnect.disconnectFromDB. Не могу отключиться от базы данных." + e.getMessage());
            e.printStackTrace();
        }
    }

}
