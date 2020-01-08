import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

/**
 * copy to console for testing
 * ~si ~s
 * ~si ~c
 * ~lu l1 p1
 * ~df demo.bmp
 * ~sf demo.bmp
 * ~gf demo.bmp
 * ~rf demo2.bmp demo1.bmp
 */

/**
 * Класс поступающей команды
 */
public class CommandsList implements CommandAnswer {

    // to get this information type ~? at console
    final String commandsInfoRegistered = "Commands available: \n\t~si ~s  - to get server storage (files) info. \n\t" +
            "~si ~c  - to get client storage (files) info. \n\t~gf NAME - to get file NAME from server to local. \n\t" +
            "~sf NAME - to sent file NAME from local to server storage. \n\t~df NAME - to delete file NAME from server storage. \n\t" +
            "~rf NAME - to renaming file NAME from server storage. \n____________________________________________________________";
    final String commandsInfoShort = "Commands available: \n\t~lu LOGIN PASSWORD - to login IN. \n\t" +
            "~ru LOGIN PASSWORD  - to register new user. \n\t" +
            "~?  - to get help.\n____________________________________________________________";

    private CommandAnswer.WhoIsSender whoIsSender;
    private String registeredUserID = null;
    private String mnemonicCode;
    private String mnemonicParameterFirst;
    private String mnemonicParameterSecond;
    private List<File> files;
    private byte[] fileData;

    public CommandsList() {
        clearCommand();
    }

    public CommandsList(String usersCommand, CommandAnswer.WhoIsSender whoIsSender) {
        this.sendingSettings(usersCommand, whoIsSender);
    }

    /**
     * Метод обработки входящего объекта-команды
     */
    @Override
    public void reflection(ChannelHandlerContext ctx) {

        if (this.mnemonicCode.toLowerCase().startsWith("~?"))
            getCommandsList(ctx); // NL  0) если запрос списка команд = выполнить запрос
        else if (this.mnemonicCode.toLowerCase().startsWith("~lu")) //NL  1) если нет регистрации то запустить регистрацию пользователя
            logonUser(ctx);
        else if (this.mnemonicCode.toLowerCase().startsWith("~ru"))
            registerNewUser(ctx);
        else if (this.registeredUserID != null) { // NL 2) если есть регистация = выоплнить команду.
            if (this.mnemonicCode.toLowerCase().startsWith("~si"))
                getServerInfo(ctx);
            else if (this.mnemonicCode.toLowerCase().startsWith("~df")) deleteFile(ctx);
            else if (this.mnemonicCode.toLowerCase().startsWith("~sf")) sendFile(ctx);
            else if (this.mnemonicCode.toLowerCase().startsWith("~gf")) getFile(ctx);
            else if (this.mnemonicCode.toLowerCase().startsWith("~rf")) renameFile(ctx);
        } else
            System.err.println("Reflection. unknown incoming command or no user`s authority. incoming mnemonicCode: (" + mnemonicCode + ")");
    }

    private void registerNewUser(ChannelHandlerContext ctx) {
        switch (this.whoIsSender) {

            case CLIENT: // я на стороне сервера, хочу создать нового пользователя в БД
                this.whoIsSender = WhoIsSender.SERVER;
                DBConnect dbConnect = new DBConnect();
                if (dbConnect.setNewUserID(this.mnemonicParameterFirst, this.mnemonicParameterSecond) > 0) {
                    this.mnemonicParameterSecond = " зарегистрирован";
                } else this.mnemonicParameterSecond = " не зарегистрирован";
                dbConnect.disconnect();
                ctx.writeAndFlush(this);
                break;

            case SERVER: // я на стороне клиента фиксирую себя в листе пользователей
                System.out.println("Пользователь " + this.mnemonicParameterFirst + " " + this.mnemonicParameterSecond);
                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("RenamingFile.Reflection. Укажите отправителя.");
                break;
        }
    }

    /**
     * Метод регистрации клиента на сервере на стороне сервера
     */
    public void logonUser(ChannelHandlerContext ctx) {

        switch (this.whoIsSender) {

            case CLIENT: // я на стороне сервера, хочу получить идетификационный номер и занести себя в лист зарегистрированных пользователей.
                this.whoIsSender = WhoIsSender.SERVER;
                DBConnect dbConnect = new DBConnect();  // 1) подключиться к БД
                this.registeredUserID = dbConnect.getRegisteredUserID(this.mnemonicParameterFirst, this.mnemonicParameterSecond); // 2) присвоить полю команды this.registeredUserID полезное значение либо null  +
                dbConnect.disconnect();
                UsersOnLineList.addUser(this.mnemonicParameterFirst, this.registeredUserID); // 3) зарегистрировать в списке пользователей
                ctx.writeAndFlush(this);
                break;

            case SERVER: // я на стороне клиента фиксирую себя в листе пользователей

                if (this.registeredUserID != null) {
                    UsersOnLineList.MyID = this.registeredUserID;
                    System.out.println("Пользователь " + this.mnemonicParameterFirst + " вошел в систему.");
                } else System.err.println("Не удается получить ID для пользователя: " + this.mnemonicParameterFirst);

                this.whoIsSender = WhoIsSender.NULL;
                break;
            default:
                System.err.println("RenamingFile.Reflection. Укажите отправителя.");
                break;
        }
    }

    /**
     * Метод получеия списка хранилища файлов
     */
    public void getServerInfo(ChannelHandlerContext ctx) {
        switch (this.whoIsSender) {
            case CLIENT: //я на стороне сервера // если отправителем был клиент то выполнить логику обработки данных на стороне сервера

                if (this.mnemonicParameterFirst.equals("~s")) { // я хочу получить файлы на сервере
                    try {
                        this.files = Arrays.asList(new File(Settings.getPathForUser(UsersOnLineList.getMyFolderName(this.registeredUserID)).toString()).listFiles()); //  формируем список файлов сервера в LIST
                    } catch (Exception e) {
                        System.err.println("не могу получить список файлов в заданной папке:" + Settings.getPathForUser(UsersOnLineList.getMyFolderName(this.registeredUserID)).toString());
                        System.err.println(e.getMessage());
                    }
                } else if (!this.mnemonicParameterFirst.equals("~c"))
                    System.err.println("для команды ~si второй параметр должен быть либо ~s либо ~c");

                this.whoIsSender = WhoIsSender.SERVER;
                ctx.writeAndFlush(this);
                break;

            case SERVER: //я на стороне клиента // если отправителем был Сервер то выполнить логику обработки данных на стороне клиента

                if (this.mnemonicParameterFirst.equals("~c")) { // я хочу получить файлы на клиенте
                    try {
                        this.files = Arrays.asList(new File(Settings.CLIENT_PATH.toString()).listFiles());
                        this.getFiles().forEach(System.out::println); // формируем список файлов клиента в LIST
                    } catch (Exception e) {
                        System.err.println("не могу получить список файлов в заданной папке.");
                        System.err.println(e.getMessage());
                    }
                } else if (this.mnemonicParameterFirst.equals("~s")) {// я хочу получить файлы на сервере
                    this.getFiles().forEach(System.out::println);
                } else System.err.println("для команды ~si второй параметр должен быть либо ~s либо ~c");

                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("GetStorageInfo.Reflection. Не указан отрпавитель SERVER || CLIENT");
                break;
        }
    }

    /**
     * Метод удаления файлов на стороне сервера
     */
    public void deleteFile(ChannelHandlerContext ctx) {

        switch (this.whoIsSender) {
            case CLIENT: //я на стороне сервера
                try {
                    Files.delete(Paths.get(Settings.SERVER_PATH + "/" + this.mnemonicParameterFirst));
                    System.out.println("файл: " + this.mnemonicParameterFirst + " удален из хранилища на сервере.");
                    this.mnemonicParameterSecond = "true";
                } catch (IOException e) {
                    System.err.println("файл: " + this.mnemonicParameterFirst + " не возможно удалить из хранилища на сервере.");
                    this.mnemonicParameterSecond = "false";
                    System.err.println(e.getMessage());
                }

                this.whoIsSender = WhoIsSender.SERVER;
                ctx.writeAndFlush(this);
                break;

            case SERVER: //я на стороне клиента
                if (this.mnemonicParameterSecond.equals("false")) {
                    System.err.println("Невозможно удалить " + this.mnemonicParameterFirst + " в хранилище на сервере.");
                } else {
                    System.out.println("Файл " + this.mnemonicParameterFirst + " удален из хранилища на сервере.");
                }
                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("GetStorageInfo.Reflection. Не указан отрпавитель SERVER || CLIENT");
                break;
        }
    }

    /**
     * Метод отправки файла на сервер
     */
    public void sendFile(ChannelHandlerContext ctx) {

        switch (this.whoIsSender) {
            case CLIENT: //я на стороне сервера. Хочу записать файл полученный от клиента
                try {
                    this.mnemonicParameterFirst = Paths.get(String.format("%s%s%s",
                            Settings.SERVER_PATH, UsersOnLineList.getMyFolderName(this.registeredUserID), Paths.get(this.mnemonicParameterFirst)
                                    .getFileName())).toAbsolutePath().toString();
                    System.out.println(this.mnemonicParameterFirst);
                    Files.write(Paths.get(this.mnemonicParameterFirst), this.fileData, StandardOpenOption.CREATE_NEW); // NL записываем данные объекта в файл
                    System.out.println("Файл " + this.mnemonicParameterFirst + " сохранен в хранилище на сервере.");
                    this.mnemonicParameterSecond = "done";
                } catch (IOException e) {
                    System.err.println("Невозможно записать " + this.mnemonicParameterFirst + " в хранилище на сервере.");
                    this.mnemonicParameterSecond = "false";
                    System.err.println(e.getMessage());
                }

                this.whoIsSender = WhoIsSender.SERVER;
                ctx.writeAndFlush(this);
                break;

            case SERVER: //я на стороне клиента
                if (this.mnemonicParameterSecond.equals("false")) {
                    System.err.println("Невозможно записать " + this.mnemonicParameterFirst + " в хранилище на сервере.");
                } else {
                    System.out.println("Файл " + this.mnemonicParameterFirst + " сохранен в хранилище на сервере.");
                }
                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("GetStorageInfo.Reflection. Не указан отрпавитель SERVER || CLIENT");
                break;
        }


    }

    /**
     * Метод получения файла с сервера
     */
    public void getFile(ChannelHandlerContext ctx) {

        switch (this.whoIsSender) {
            case CLIENT: // я на стороне сервера. Хочу получить файл из папки пользователя
                this.whoIsSender = WhoIsSender.SERVER;
                this.mnemonicParameterFirst = String.format("%s%s%s",
                        Settings.SERVER_PATH, UsersOnLineList.getMyFolderName(this.registeredUserID), this.mnemonicParameterFirst);
                this.mnemonicParameterFirst = Paths.get(this.mnemonicParameterFirst).toAbsolutePath().toString();
                System.out.println(this.mnemonicParameterFirst);
                try {
                    fileData = Files.readAllBytes(Paths.get(this.mnemonicParameterFirst)); // NL записываем данные файла в объект
                    this.mnemonicParameterSecond = "done";
                } catch (Exception e) {
                    this.mnemonicParameterSecond = "false";
                    System.err.println(e.getMessage());
                }

                ctx.writeAndFlush(this); // NL отправляем объект с данными файла в сторону клиента
                break;

            case SERVER: // я на стороне клиента
                this.mnemonicParameterFirst = Settings.CLIENT_PATH + "/" + Paths.get(this.mnemonicParameterFirst).getFileName().toString();
                try {
                    Files.write(Paths.get(this.mnemonicParameterFirst), this.fileData, StandardOpenOption.CREATE_NEW); // NL создаем на клиенте файл из объекта
                    System.out.println("Файл " + this.mnemonicParameterFirst + " сохранен в локальном хранилище");
                } catch (IOException e) {
                    System.err.println("не могу записать файл: " + this.mnemonicParameterFirst + " в локальном хранилище");
                    System.err.println(e.getMessage());
                }

                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("MessageCommand. Укажите отправителя");
                break;
        }
    }

    /**
     * Метод смены имени файла на стороне сервера
     */
    public void renameFile(ChannelHandlerContext ctx) {


        switch (this.whoIsSender) {
            case CLIENT: // я на стороне сервера
                this.whoIsSender = WhoIsSender.SERVER;
                this.mnemonicParameterFirst = Settings.SERVER_PATH + "/" + this.mnemonicParameterFirst;
                this.mnemonicParameterSecond = Settings.SERVER_PATH + "/" + this.mnemonicParameterSecond;

                try {
                    Files.move(Paths.get(this.mnemonicParameterFirst), Paths.get(this.mnemonicParameterSecond), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("файл: " + this.mnemonicParameterFirst + " переименован в " + this.mnemonicParameterSecond + " в хранилище на сервере.");
                } catch (IOException e) {
                    System.err.println("файл: " + this.mnemonicParameterFirst + " не возможно переименовать на сервере в " + this.mnemonicParameterSecond);
                    this.mnemonicParameterSecond = "false";
                    System.err.println(e.getMessage());
                }
                ctx.writeAndFlush(this); //
                break;

            case SERVER: // я на стороне клиента

                if (this.mnemonicParameterSecond.equals("false")) {
                    System.err.println("файл: " + this.mnemonicParameterFirst + " не возможно переименовать на сервере");
                } else
                    System.out.println("файл: " + this.mnemonicParameterFirst + " переименован в " + this.mnemonicParameterSecond + " в хранилище на сервере.");

                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("MessageCommand. Укажите отправителя");
                break;

        }


    }

    /**
     * Метод получения доступных команд
     */
    public void getCommandsList(ChannelHandlerContext ctx) {

        switch (this.whoIsSender) {
            case CLIENT: // я на стороне сервера
                this.whoIsSender = WhoIsSender.SERVER;

                if (this.registeredUserID == null) this.mnemonicParameterFirst = commandsInfoShort;
                else this.mnemonicParameterFirst = commandsInfoRegistered;
                ctx.writeAndFlush(this);
                break;

            case SERVER: // я на стороне клиента
                System.out.println(this.mnemonicParameterFirst);
                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("MessageCommand. Укажите отправителя");
                break;
        }
    }

    /**
     * Метод получения отправителя пакета
     *
     * @return - отправитель
     */
    @Override
    public WhoIsSender getWhoIsSender() {
        return this.whoIsSender;
    }

    /**
     * Метод получения списка файлов в хранилище
     *
     * @return - List файлов
     */
    public List<File> getFiles() {
        return this.files;
    }

    /**
     * Переопределение toString для обекта.
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Command: " + "\n\t" +
                "whoIsSender=" + this.whoIsSender + "\n\t" +
                "registeredID=" + this.registeredUserID + "\n\t" +
                "mnemonicCode=" + this.mnemonicCode + "\n\t" +
                "mnemonicParameterFirst=" + mnemonicParameterFirst + "\n\t" +
                "mnemonicParameterSecond=" + mnemonicParameterSecond + "\n\t" +
                "files=" + files + "\n\t" +
                "fileData=" + Arrays.toString(fileData);
    }

    /**
     * Метод подготовки объекта команды к отправке в сеть
     *
     * @param usersCommand - команда введенная в консоль
     * @param whoIsSender  - кто отправитель (отправка начинается со стороны клиента)
     */
    @Override
    public void sendingSettings(String usersCommand, WhoIsSender whoIsSender) {
        this.whoIsSender = whoIsSender;
        this.registeredUserID = UsersOnLineList.MyID;

        switch (usersCommand.split(" ").length) {
            case 3: {
                this.mnemonicParameterSecond = usersCommand.split(" ")[2];
            }
            case 2: {
                this.mnemonicParameterFirst = usersCommand.split(" ")[1];
            }
            case 1: {
                this.mnemonicCode = usersCommand.split(" ")[0];
                break;
            }
            default:
                System.out.println("wrong command");
                break;
        }

        if (this.mnemonicCode.equals("~sf")) { // отправляемый в сеть объект-файл уже должен быть с данными поэтому заполняем здесь.
            try {
                this.mnemonicParameterFirst = Settings.CLIENT_PATH + "/" + this.mnemonicParameterFirst;
                this.fileData = Files.readAllBytes(Paths.get(this.mnemonicParameterFirst).toAbsolutePath()); // NL записываем данные файла в объект
            } catch (Exception e) {
                System.err.println("не могу прочитать файл: " + this.mnemonicParameterFirst + " на локальном хранилище.");
            }
        }
    }

    /**
     * метод обнуления объекта команнды после выоплнения
     */
    private void clearCommand() {
        this.whoIsSender = CommandAnswer.WhoIsSender.NULL; // SERVER\CLIENT
        this.registeredUserID = null;
        this.files = null; // список файлов на клиенте \сервере
        this.fileData = null; // данные передаваемого файла
        this.mnemonicCode = null; // команда
        this.mnemonicParameterFirst = null; // первый параметр ~s, ~name
        this.mnemonicParameterSecond = null; // второй параметр ~filename
    }

    @Override
    public String getRegisteredUserID() {
        return registeredUserID;
    }
}
