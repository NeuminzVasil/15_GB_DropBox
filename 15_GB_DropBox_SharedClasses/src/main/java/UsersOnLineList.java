import java.util.HashMap;

public class UsersOnLineList {

    public static String MyID = null; // NL поле ID для Клиента. Используется для запоминания реститационного номера текущего клиента
    private static HashMap<String, String> usersOnLineList = new HashMap(); // NL поле для Сервера. список зарегистрировавшихся пользователей онлайн

    public static void addUser(String registeredUserID, String login) {
        usersOnLineList.put(registeredUserID, login);// проработать получение dataBaseID из БД
    }

    public static void getUsersOnLineList() {
        usersOnLineList.forEach((k, v) -> System.out.println(k + "=" + v + "\n"));
    }

    public static String getMyFolderName(String registeredUserID) {
        return "/" + usersOnLineList.getOrDefault(registeredUserID, null) + "/";
    }

}

