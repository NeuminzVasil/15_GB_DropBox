import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    Button getFileButton;
    @FXML
    Button sendFileButton;
    @FXML
    Button deleteFileButton;
    @FXML
    TreeView<String> treeViewServer;
    @FXML
    TreeView<String> treeViewClient;

    /**
     * метод инициализации окна перед после его создания и перед его отображением
     *
     * @param location  - какое то поле
     * @param resources - какое то поле
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateStorageView("Client Storage", treeViewClient);
        updateStorageView("Server Storage", treeViewServer);
    }

    /**
     * метод обновления экрана файлов на сервере
     *
     * @param treeLabel      - название корневой папки для treeView
     * @param treeViewServer - экземпляр обновляемого treeView
     */
    public void updateStorageView(String treeLabel, TreeView<String> treeViewServer) {

        // todo ждем когда данные вернуться с ссервера - не поянл как организовать красиво
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (CommonVar.commandForSend.getWhoIsSender() == CommandAnswer.WhoIsSender.NULL)
                break;
        }


//        отправляем команду запроса списка файлов в харнилице
        try {
            if (treeLabel.startsWith("Server"))
                CommonVar.commandForSend.sendingSettings("~si ~s", CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            if (treeLabel.startsWith("Client"))
                CommonVar.commandForSend.sendingSettings("~si ~c", CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            CommonVar.clientNetListener.getSocketChannel().writeAndFlush(CommonVar.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // todo ждем когда данные вернуться с ссервера - не поянл как организовать красиво
        while (true) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (CommonVar.commandForSend.getWhoIsSender() == CommandAnswer.WhoIsSender.NULL)
                break;
        }

        // рисую полученный лист файлов после того как команда вернулась c сервера
        Image iconFolder = new Image(getClass().getResourceAsStream("iconFolderRemote.png"));
        if (treeLabel.startsWith("Client"))
            iconFolder.getClass().getResourceAsStream("iconFolderLocal.png");
        Image iconFile = new Image(getClass().getResourceAsStream("iconFile.png"));
        TreeItem<String> root = new TreeItem<>(treeLabel, new ImageView(iconFolder));
        CommonVar.commandForSend.getFiles().forEach(file -> {
            root.getChildren().add(new TreeItem<>(file.getName(), new ImageView(iconFile)));
        });
        treeViewServer.setRoot(root);
        root.setExpanded(true);
    }

    /**
     * Метод получения файла с сервера.
     */
    public void getFileBtn() {

        try {
            treeViewServer.getSelectionModel().getSelectedItem().getValue();
            CommonVar.commandForSend.sendingSettings("~gf " + treeViewServer.getSelectionModel().getSelectedItem().getValue(),
                    CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            CommonVar.clientNetListener.getSocketChannel().writeAndFlush(CommonVar.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateStorageView("Client", treeViewClient);
    }

    /**
     * Метод отправики файла на сервера.
     */
    public void sendFileBtn() {

        try {
            CommonVar.commandForSend.sendingSettings("~sf " + treeViewClient.getSelectionModel().getSelectedItem().getValue(),
                    CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            CommonVar.clientNetListener.getSocketChannel().writeAndFlush(CommonVar.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateStorageView("Server", treeViewServer);
    }

    public void deleteFileOnServer() {

        try {
            CommonVar.commandForSend.sendingSettings("~df " + treeViewServer.getSelectionModel().getSelectedItem().getValue(),
                    CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            CommonVar.clientNetListener.getSocketChannel().writeAndFlush(CommonVar.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateStorageView("Server", treeViewServer);
    }

    /**
     * метод закрытия основного окна программы
     *
     * @param actionEvent - экземпляр события
     */
    public void signOut(ActionEvent actionEvent) {
//      Закрываем соединение
        CommonVar.clientNetListener.closeConnection();

//      Закрываем окно и возвращаемся в окно ввода логина и пароля
        try {
            ((Node) actionEvent.getSource()).getScene().getWindow().hide(); // скрываем текущее окно
            Stage loginWindowStage = new Stage(); // создаем экземпляр сцены в которую хотим переключиться
            FXMLLoader loader = new FXMLLoader(); // создаем экземпляр FXMLLoader-а сцены в которую хотим переключиться
            Pane root = loader.load(getClass().getResource("loginWindow.fxml").openStream()); // создаем экземпляр корневой "панели??" сцены в которую хотим переключиться
            loginWindowStage.setScene(new Scene(root)); // создаем "комплект" нового окна сцены в которую хотим переключиться
            loginWindowStage.show(); // переключаемся в окно
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectFile(MouseEvent actionEvent) {
        if (actionEvent.getSource() == treeViewServer) {
            treeViewClient.getSelectionModel().clearSelection();
        }
        if (actionEvent.getSource() == treeViewClient) {
            treeViewServer.getSelectionModel().clearSelection();
        }
    }

    public void renameFileOnServer(ActionEvent actionEvent) {

    }
}
