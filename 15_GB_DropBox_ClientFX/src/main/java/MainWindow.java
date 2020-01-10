import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {

    @FXML
    TextField fileNameTextField;
    @FXML
    Button getFileButton;
    @FXML
    Button sendFileButton;
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


        try {
            // nl пробуем отправить команду серверу при инициалиации основного окна программы
            CommonVar.commandFromUsersUI.append("~si ~s");
            System.out.println(CommonVar.commandFromUsersUI); // DM
            CommonVar.commandForSend.sendingSettings(CommonVar.commandFromUsersUI.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            CommonVar.clientNetListener.getSocketChannel().writeAndFlush(CommonVar.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        // todo ждем когда данные вернуться с ссервера - не поянл как организовать красиво
        while (true) {
            System.out.println(CommonVar.commandForSend.getWhoIsSender() + " " +
                    CommonVar.commandForSend.getRegisteredUserID()); //DM
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (CommonVar.commandForSend.getWhoIsSender() == CommandAnswer.WhoIsSender.NULL) break;
        }

// после того как команда вернулась - отрисовывают полученный лист файлов пользователю
// __________________________________________________________________
// пример создания рута и ноды
        TreeItem<String> root = new TreeItem<>("ServerFiles");
        CommonVar.commandForSend.getFiles().forEach(file -> {
            root.getChildren().add(new TreeItem<>(file.getName()));
        });

// добавления рута в тривью.
        treeViewServer.setRoot(root);
        root.setExpanded(true);
// __________________________________________________________________


    }


    /**
     * Метод отправки сообщеиня в сторону сервера.
     */
    public void sendMessageObject() {

        CommonVar.commandFromUsersUI.append(fileNameTextField.getText());

// NL клиент. Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)
        try {
            System.out.println(CommonVar.commandFromUsersUI); // DM
            CommonVar.commandForSend.sendingSettings(CommonVar.commandFromUsersUI.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            CommonVar.clientNetListener.getSocketChannel().writeAndFlush(CommonVar.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommonVar.commandFromUsersUI.setLength(0);
    }

    /**
     * метод закрытия основного окна программы
     *
     * @param actionEvent - экземпляр события
     */
    public void SignOut(ActionEvent actionEvent) {
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
}
