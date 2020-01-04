import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UsersOnLineList {

    static List<User> usersOnLineList = new LinkedList<>();

    public static void main(String[] args) throws InterruptedException {


        for (int i = 0; i < 10; i++) {
            usersOnLineList.add(new User("id" + i, "login" + i, i));
            Thread.sleep(2000);
        }


        usersOnLineList.forEach(user -> System.out.println(user.toString()));


    }

    private static class User {
        private String registeredID;
        private String login;
        private Integer dataBaseID;
        private Date lestOperationTime; // фиксации даты последней операции для отключения пользователя по простою.

        public User(String registeredID, String login, Integer dataBaseID) {
            this.registeredID = registeredID;
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
            return "registeredID='" + registeredID + '\'' +
                    ", login='" + login + '\'' +
                    ", dataBaseID=" + dataBaseID +
                    ", lestOperationTime=" + lestOperationTime;
        }
    }
}

