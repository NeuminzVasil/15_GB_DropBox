public class ClientSharedVariables {
    public static String clientName = "ClientFX";
    public static ClientNetListener clientNetListener; // ссылка на клиентское подключение
    public static StringBuilder commandFromUsersUI = new StringBuilder();
    public static CommandAnswer commandForSend = new CommandsList(); //объект "команды пользователя"
}
