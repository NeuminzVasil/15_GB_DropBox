import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ClientConsoleCommandLine implements Runnable {

    private String clientName = "ClientConsoleCommandLine";
    private ClientNetListener clientNetListener; // ссылка на клиентское подключение
    private InputStreamReader inputStream = new InputStreamReader(System.in); // объект чтения данных из потока консоли
    private BufferedReader bufferedReader = new BufferedReader(inputStream); // объект чтения данных из консоли
    private StringBuilder commandFromUsersConsole = new StringBuilder(); // введенная в консоль пользователем
    private CommandAnswer commandForSend = new CommandsList(); //объект "команды пользователя"

    /**
     * констуктор консольной программы с созданием клиентского подключения
     */
    public ClientConsoleCommandLine(String hostName, int port) {

        Settings.HOST_NAME = hostName;
        Settings.HOST_PORT = port;

        // запускаем клиентское подлючение к серверу
        clientNetListener = new ClientNetListener(clientName);
        Thread clientNetListenerThread = new Thread(clientNetListener);
        clientNetListenerThread.start();
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            Settings.HOST_NAME = args[0];
            Settings.HOST_PORT = Integer.parseInt(args[1]);
        }

        // запускаем консоль с внутренним запуском клиентского подключения
        ClientConsoleCommandLine clientConsoleCommandLine = new ClientConsoleCommandLine(Settings.HOST_NAME, Settings.HOST_PORT);
        Thread clientConsoleCommandLineThread = new Thread(clientConsoleCommandLine);
        clientConsoleCommandLineThread.start();
    }

    /**
     * метод чтения введенных данных их консоли
     */
    @Override
    public void run() {

        System.out.println("ClientConsoleCommandLine.started");
        try {
            while (true) { // NL клиент. цикл чтения команд с консоли пользователя

                if (!!(commandFromUsersConsole.insert(0, bufferedReader.readLine()).toString().toLowerCase().equals("~#stop"))) // NL клиент. обработка команды "стоп консоль"
                    break;

                if (commandFromUsersConsole.toString().toLowerCase().startsWith("~lu") || // NL клиент. обработка дргих команд
                        commandFromUsersConsole.toString().toLowerCase().startsWith("~?") ||
                        commandFromUsersConsole.toString().toLowerCase().startsWith("~si") ||
                        commandFromUsersConsole.toString().toLowerCase().startsWith("~gf") ||
                        commandFromUsersConsole.toString().toLowerCase().startsWith("~sf") ||
                        commandFromUsersConsole.toString().toLowerCase().startsWith("~df") ||
                        commandFromUsersConsole.toString().toLowerCase().startsWith("~rf")) {

// NL клиент. Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)

                    commandForSend.sendingSettings(commandFromUsersConsole.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
                    clientNetListener.getSocketChannel().writeAndFlush(commandForSend); // NL клиент. отправка команды в сеть.

                } else { // NL клиент. обработка неизвестной команды
                    System.err.println("Console: unknown command!");
                }
                commandFromUsersConsole.setLength(0);
            }

            clientNetListener.getSocketChannel().closeFuture();
            clientNetListener.getSocketChannel().close();

        } catch (
                IOException e) {
            e.getMessage();
        }

    }

}
