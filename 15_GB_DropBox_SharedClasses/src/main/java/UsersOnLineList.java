import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UsersOnLineList {

    private static List<User> usersOnLineList = new LinkedList<>();// список зарегистрировавшихся пользователей онлайн

    public static void main(String[] args) throws InterruptedException {


        for (int i = 0; i < 10; i++) {
            usersOnLineList.add(new User("id" + i, "login" + i, i));
            Thread.sleep(2000);
        }


        usersOnLineList.forEach(user -> System.out.println(user.toString()));


    }

    public static void addRegisteredUserID(String login, String registeredUserID) {
        usersOnLineList.add(new User(registeredUserID, login, 1)); // проработать получение dataBaseID из БД
    }

    private static class User {
        private String registeredUserID;
        private String login;
        private Integer dataBaseID;
        private Date lestOperationTime; // фиксации даты последней операции для отключения пользователя по простою.

        public User(String registeredID, String login, Integer dataBaseID) {
            this.registeredUserID = registeredID;
            this.login = login;
            this.dataBaseID = dataBaseID;
            this.lestOperationTime = new Date(System.currentTimeMillis());
        }

        public Date getLestOperationTime() {
            return lestOperationTime;
        }

        public void setLestOperationTime(Date lestOperationTime) {
            this.lestOperationTime = lestOperationTime;
        }

        @Override
        public String toString() {
            return "registeredID='" + registeredUserID + '\'' +
                    ", login='" + login + '\'' +
                    ", dataBaseID=" + dataBaseID +
                    ", lestOperationTime=" + lestOperationTime;
        }
    }
}
