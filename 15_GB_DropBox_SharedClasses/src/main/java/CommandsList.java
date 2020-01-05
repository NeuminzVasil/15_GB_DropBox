import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;


/**
 * Класс поступающей команды
 */
public class CommandsList implements Serializable, CommandAnswer {

    // to get this information type ~? at console
    static final String commandsInfo = "Commands available: \n\t" +
            "~si ~s  - to get server storage (files) info. \n\t" +
            "~si ~r  - to get client storage (files) info. \n\t" +
            "~gf NAME - to get file NAME from server to local. \n\t" +
            "~sf NAME - to sent file NAME from local to server storage. \n\t" +
            "~df NAME - to delete file NAME from server storage. \n\t" +
            "~rf NAME - to renaming file NAME from server storage. \n" +
            "____________________________________________________________";

    private CommandAnswer.WhoIsSender whoIsSender;
    private String mnemonicCode;
    private String mnemonicParameterFirst;
    private String mnemonicParameterSecond;
    private byte[] fileData;
    private String registeredID;
    private List<File> files;

    public CommandsList() {
        clearCommand();
    }

    public CommandsList(String usersCommand, CommandAnswer.WhoIsSender whoIsSender) {
        this.sendingSettings(usersCommand, whoIsSender);
    }

    /**
     * метод обнуления объекта команнды после выоплнения
     */
    private void clearCommand() {
        this.whoIsSender = CommandAnswer.WhoIsSender.NULL;
        this.files = null;
        this.fileData = null;
        this.registeredID = null;
        this.mnemonicCode = null; // команда
        this.mnemonicParameterFirst = null; // первый параметр ~s, ~name
        this.mnemonicParameterSecond = null; // второй параметр ~filename
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
        return "CommandsList{" +
                "whoIsSender=" + whoIsSender +
                ", fileData=" + Arrays.toString(fileData) +
                ", registeredID='" + registeredID + '\'' +
                ", files=" + files +
                ", mnemonicCode='" + mnemonicCode + '\'' +
                ", mnemonicParameterFirst='" + mnemonicParameterFirst + '\'' +
                ", mnemonicParameterSecond='" + mnemonicParameterSecond + '\'' +
                '}';
    }

    @Override
    public void sendingSettings(String usersCommand, CommandAnswer.WhoIsSender whoIsSender) {
        this.whoIsSender = whoIsSender;
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
                this.mnemonicParameterFirst = SettingsClient.CLIENT_PATH + "\\" + this.mnemonicParameterFirst;
                this.fileData = Files.readAllBytes(Paths.get(this.mnemonicParameterFirst).toAbsolutePath()); // NL записываем данные файла в объект
            } catch (Exception e) {
                System.err.println("не могу прочитать файл: " + this.mnemonicParameterFirst + " на локальном хранилище.");
            }
        }
    }

    /**
     * Метод получения списка файлов
     * сторона получения файлов указывыается первым параметорм команды
     * ~si ~s получение списка файлов на стороне сервера
     * ~si ~с получение списка файлов на стороне клиента
     */
    @Override
    public void reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) {

        System.out.println("incoming command: " + this.mnemonicCode + " " + this.mnemonicParameterFirst + " " + this.mnemonicParameterSecond);

        if (this.mnemonicCode.toLowerCase().startsWith("~si")) getServerInfo(ctx);
        else if (this.mnemonicCode.toLowerCase().startsWith("~lu")) logonUser(ctx);
        else if (this.mnemonicCode.toLowerCase().startsWith("~df")) deleteFile(ctx);
        else if (this.mnemonicCode.toLowerCase().startsWith("~sf")) sendFile(ctx);
        else if (this.mnemonicCode.toLowerCase().startsWith("~gf")) getFile(ctx);
        else if (this.mnemonicCode.toLowerCase().startsWith("~rf")) renameFile(ctx);
        else System.err.println("Reflection. unknown incoming command. \n" + commandsInfo);

        if (whoIsSender.equals(WhoIsSender.NULL)) this.clearCommand();
    }


    /**
     * Метод получеия списка хранилища файлов
     */
    public void getServerInfo(ChannelHandlerContext ctx) {
        switch (this.whoIsSender) {
            case CLIENT: //я на стороне сервера // если отправителем был клиент то выполнить логику обработки данных на стороне сервера

                if (this.mnemonicParameterFirst.equals("~s")) { // я хочу получить файлы на сервере
                    try {
                        this.files = Arrays.asList(new File(SettingsServer.SERVER_PATH.toString()).listFiles()); // формируем список файлов сервера в LIST
                    } catch (Exception e) {
                        System.err.println("не могу получить список файлов в заданной папке.");
                        e.getMessage();
                    }
                } else if (!this.mnemonicParameterFirst.equals("~c"))
                    System.err.println("для команды ~si второй параметр должен быть либо ~s либо ~c");

                this.whoIsSender = WhoIsSender.SERVER;
                ctx.writeAndFlush(this);
                break;

            case SERVER: //я на стороне клиента // если отправителем был Сервер то выполнить логику обработки данных на стороне клиента

                if (this.mnemonicParameterFirst.equals("~c")) { // я хочу получить файлы на клиенте
                    try {
                        this.files = Arrays.asList(new File(SettingsClient.CLIENT_PATH.toString()).listFiles());
                        this.getFiles().forEach(System.out::println); // формируем список файлов клиента в LIST
                    } catch (Exception e) {
                        System.err.println("не могу получить список файлов в заданной папке.");
                        e.getMessage();
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
     * Метод регистрации клиента на сервере на стороне сервера
     */
    public void logonUser(ChannelHandlerContext ctx) {

        switch (this.whoIsSender) {

            case CLIENT: // я на стороне сервера
                this.whoIsSender = WhoIsSender.SERVER;
                System.out.println("Регистрация пользователя: " + this.mnemonicParameterFirst);
                this.registeredID = ctx.pipeline().channel().id().asShortText();
                ctx.writeAndFlush(this); // NL отправляем объект с данными файла в сторону клиента
                break;

            case SERVER: // я на стороне клиента
                System.out.println("Регистрация пользователя " + this.mnemonicParameterFirst + " прошла успешно: userID: " + this.registeredID);
                this.whoIsSender = WhoIsSender.NULL;
                break;
            default:
                System.err.println("RenamingFile.Reflection. Укажите отправителя"); //log
                break;
        }

    }

    /**
     * Класс списка файлов на стороне сервера
     */
    public void deleteFile(ChannelHandlerContext ctx) {

        switch (this.whoIsSender) {
            case CLIENT: //я на стороне сервера
                try {
                    Files.delete(Paths.get(SettingsServer.SERVER_PATH + "\\" + this.mnemonicParameterFirst));
                    System.out.println("файл: " + this.mnemonicParameterFirst + " удален из хранилища на сервере."); // log
                    this.mnemonicParameterSecond = "true";
                } catch (IOException e) {
                    System.err.println("файл: " + this.mnemonicParameterFirst + " не возможно удалить из хранилища на сервере.");
                    this.mnemonicParameterSecond = "false";
                    e.getMessage();
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
            case CLIENT: //я на стороне сервера
                try {
                    this.mnemonicParameterFirst = SettingsServer.SERVER_PATH + "\\" + Paths.get(this.mnemonicParameterFirst).getFileName();
                    Files.write(Paths.get(this.mnemonicParameterFirst), this.fileData, StandardOpenOption.CREATE_NEW); // NL записываем данные объекта в файл
                    System.out.println("Файл " + this.mnemonicParameterFirst + " сохранен в хранилище на сервере.");
                    this.mnemonicParameterSecond = "done";
                } catch (IOException e) {
                    System.err.println("Невозможно записать " + this.mnemonicParameterFirst + " в хранилище на сервере.");
                    this.mnemonicParameterSecond = "false";
                    e.getMessage();
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
            case CLIENT: // я на стороне сервера
                this.whoIsSender = WhoIsSender.SERVER;
                this.mnemonicParameterFirst = SettingsServer.SERVER_PATH + "\\" + this.mnemonicParameterFirst;

                try {
                    fileData = Files.readAllBytes(Paths.get(this.mnemonicParameterFirst)); // NL записываем данные файла в объект
                    this.mnemonicParameterSecond = "done";
                } catch (Exception e) {
                    this.mnemonicParameterSecond = "false";
                    e.getMessage();
                }

                ctx.writeAndFlush(this); // NL отправляем объект с данными файла в сторону клиента
                break;

            case SERVER: // я на стороне клиента
                this.mnemonicParameterFirst = SettingsClient.CLIENT_PATH + "\\" + Paths.get(this.mnemonicParameterFirst).getFileName().toString();
                try {
                    Files.write(Paths.get(this.mnemonicParameterFirst), this.fileData, StandardOpenOption.CREATE_NEW); // NL создаем на клиенте файл из объекта
                    System.out.println("Файл " + this.mnemonicParameterFirst + " сохранен в локальном хранилище"); //log
                } catch (IOException e) {
                    System.err.println("не могу записать файл: " + this.mnemonicParameterFirst + " в локальном хранилище");
                    e.getMessage();
                }

                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("MessageCommand. Укажите отправителя"); //log
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
                this.mnemonicParameterFirst = SettingsServer.SERVER_PATH + "\\" + this.mnemonicParameterFirst;
                this.mnemonicParameterSecond = SettingsServer.SERVER_PATH + "\\" + this.mnemonicParameterSecond;

                try {
                    Files.move(Paths.get(this.mnemonicParameterFirst), Paths.get(this.mnemonicParameterSecond), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("файл: " + this.mnemonicParameterFirst + " переименован в " + this.mnemonicParameterSecond + " в хранилище на сервере."); // log
                } catch (IOException e) {
                    System.err.println("файл: " + this.mnemonicParameterFirst + " не возможно переименовать на сервере в " + this.mnemonicParameterSecond);
                    this.mnemonicParameterSecond = "false";
                    e.getMessage();
                }
                ctx.writeAndFlush(this); // NL отправляем объект с данными файла в сторону клиента
                break;

            case SERVER: // я на стороне клиента

                if (this.mnemonicParameterSecond.equals("false")) {
                    System.err.println("файл: " + this.mnemonicParameterFirst + " не возможно переименовать на сервере");
                } else
                    System.out.println("файл: " + this.mnemonicParameterFirst + " переименован в " + this.mnemonicParameterSecond + " в хранилище на сервере."); // log

                this.whoIsSender = WhoIsSender.NULL;
                break;

            default:
                System.err.println("MessageCommand. Укажите отправителя"); //log
                break;

        }


    }

    // ~si ~s +
    // ~si ~c +
    // ~lu l1 p1 +
    // ~df demo.bmp +
    // ~sf demo.bmp +
    // ~gf demo.bmp +
    // ~rf demo2.bmp demo1.bmp +
}
