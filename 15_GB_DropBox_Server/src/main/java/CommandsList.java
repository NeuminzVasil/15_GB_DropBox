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
public class CommandsList {


    /**
     * Класс получения списка файлов
     * сторона получения файлов указывыается окончанием команды
     * ~si ~s получение списка файлов на стороне сервера
     * ~si ~r получение списка файлов на стороне клиента
     */
    public static class GetStorageInfo implements Serializable, CommandAnswer {

        private WhoIsSender whoIsSender;
        private File file;
        private List<File> files;

        /**
         * конструктор объекта с путем к хранилищу по умолчканию
         */
        public GetStorageInfo(String usersCommand, WhoIsSender whoIsSender) {

            this.whoIsSender = whoIsSender;

            if (usersCommand.split(" ")[1].equals("~s"))
                file = new File(Settings.SERVER_PATH.toString());
            else
                file = new File(Settings.CLIENT_PATH.toString());

            this.files = Arrays.asList(file.listFiles());
        }

        /**
         * Метод получения списка файлов в хранилище
         *
         * @return - List файлов
         */
        public List<File> getFiles() {
            updateFiles();
            return this.files;
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
         * метод обновления списка файлов в листе файлов "private List<File> files"
         */
        private void updateFiles() {
            this.files = Arrays.asList(file.listFiles());
        }

        /**
         * Переопределение toString для обекта.
         * @return строковое представление объекта
         */
        @Override
        public String toString() {
            StringBuffer res = new StringBuffer();
            files.forEach(file -> {
                res.append(file.getName() + "\n");
            });
            return res.toString();
        }

        @Override
        public void Reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) {
            switch (this.whoIsSender) {
                case CLIENT: // если отправителем был клиент то выполняем ответ от сервера
                    this.whoIsSender = WhoIsSender.SERVER;
                    ctx.writeAndFlush(this);
                    System.out.println("GetStorageInfo.request.done");
                    break;
                case SERVER: // если отправителем был Сервер то выполняем на клиенте то что нужно клиенту
                    System.out.println(this);
                    this.whoIsSender = WhoIsSender.NULL;
                    break;
                default:
                    System.err.println("GetStorageInfo.Reflection. Не указан отрпавитель SERVER || CLIENT");
            }
        }
    }

    /**
     * Класс передачи файла от сервера клиенту
     */
    public static class GetFileFromServer implements Serializable, CommandAnswer {

        private String fileName;
        private byte[] fileData;
        private WhoIsSender whoIsSender;

        public GetFileFromServer(String usersCommand, WhoIsSender whoIsSender) throws IOException {
            this.whoIsSender = whoIsSender;
            this.fileName = usersCommand.split(" ")[1];
            this.fileName = Settings.SERVER_PATH + "\\" + fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getFileData() {
            return fileData;
        }

        @Override
        public WhoIsSender getWhoIsSender() {
            return this.whoIsSender;
        }

        @Override
        public void Reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) throws IOException {

            switch (this.whoIsSender) {
                case CLIENT: // если отправителем был клиент то выполняем ответ от сервера
                    this.whoIsSender = WhoIsSender.SERVER;
                    fileData = Files.readAllBytes(Paths.get(this.fileName)); // NL записываем данные файла в объект\
                    ctx.writeAndFlush(this); // NL отправляем объект с данными файла в сторону клиента
                    break;

                case SERVER: // если отправителем был Сервер то выполняем на клиенте то что нужно клиенту

                    try {
                        this.fileName = Settings.CLIENT_PATH + "\\" + Paths.get(this.fileName).getFileName().toString();
                        Files.write(Paths.get(this.fileName), this.fileData, StandardOpenOption.CREATE_NEW); // NL создаем на клиенте файл из объекта
                    } catch (IOException e) {
                        System.err.println("не могу записать файл: " + this.getFileName());
                        e.printStackTrace();
                    }

                    System.out.println("Файл " + this.getFileName() + " сохранен на клиенте"); //log

                    this.whoIsSender = WhoIsSender.CLIENT;
                    break;

                default:
                    System.err.println("MessageCommand. Пожалуйста укажите отправителя"); //log
            }
        }
    }

    /**
     * Класс передачи файла от клиента серверу
     */
    public static class SendFileToServer implements Serializable, CommandAnswer {
        private String fileName;
        private byte[] fileData;

        private WhoIsSender whoIsSender;

        public SendFileToServer(String usersCommand, WhoIsSender whoIsSender) throws IOException {
            this.whoIsSender = whoIsSender;
            this.fileName = usersCommand.split(" ")[1];
            this.fileName = Settings.CLIENT_PATH + "\\" + fileName;
            fileData = Files.readAllBytes(Paths.get(this.fileName).toAbsolutePath()); // NL записываем данные файла в объект
        }

        public String getFileName() {
            return fileName;
        }

        @Override
        public WhoIsSender getWhoIsSender() {
            return this.whoIsSender;
        }

        @Override
        public void Reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) {
            try {
                this.fileName = Settings.SERVER_PATH + "\\" + Paths.get(this.fileName).getFileName();
                Files.write(Paths.get(this.fileName), this.fileData, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                System.err.println("не могу сохранить файл: " + this.getFileName() + " на сервере.");
                e.printStackTrace();
            }

            System.out.println("файл: " + this.getFileName() + " сохранен на сервере.");

        }
    }

    /**
     * Класс списка файлов на стороне сервера
     */
    public static class DeleteFile implements Serializable, CommandAnswer {

        String fileName;
        private WhoIsSender whoIsSender;

        /**
         * конструктор объекта с путем к хранилищу по умолчканию
         */
        public DeleteFile(String usersCommand, WhoIsSender whoIsSender) {
            this.whoIsSender = whoIsSender;
            this.fileName = usersCommand.split(" ")[1];
            this.fileName = Settings.SERVER_PATH + "\\" + fileName;
        }

        @Override
        public WhoIsSender getWhoIsSender() {
            return this.whoIsSender;
        }

        @Override
        public void Reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) {

            try {
                Files.delete(Paths.get(this.fileName));
            } catch (IOException e) {
                System.err.println("файл: " + this.fileName + " не возможно удалить хранилища на сервере.");
                e.printStackTrace();
            }
            System.out.println("файл: " + this.fileName + " удален из хранилища на сервере."); // log
        }
    }

    /**
     * Класс списка файлов на стороне сервера
     */
    public static class RenamingFile implements Serializable, CommandAnswer {

        String fileName;
        String newFileName;
        private WhoIsSender whoIsSender;

        /**
         * конструктор объекта с путем к хранилищу по умолчканию
         */
        public RenamingFile(String usersCommand, WhoIsSender whoIsSender) {
            this.whoIsSender = whoIsSender;
            this.fileName = usersCommand.split(" ")[1];
            this.newFileName = usersCommand.split(" ")[2];
            this.fileName = Settings.SERVER_PATH + "\\" + this.fileName;
            this.newFileName = Settings.SERVER_PATH + "\\" + this.newFileName;
        }

        @Override
        public WhoIsSender getWhoIsSender() {
            return this.whoIsSender;
        }

        @Override
        public void Reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) {

            try {
                Files.move(Paths.get(this.fileName), Paths.get(this.newFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("файл: " + this.fileName + " не возможно переименовать на сервере.");
                e.printStackTrace();
            }
            System.out.println("файл: " + this.fileName + " переименован в " + this.newFileName + " в хранилище на сервере."); // log
            //~rf demo.txt demo.bmp
        }
    }

}
