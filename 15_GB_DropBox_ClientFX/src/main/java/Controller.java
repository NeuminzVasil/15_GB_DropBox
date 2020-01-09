import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

// пример создания рука и ноды
        TreeItem<String> root = new TreeItem<>("root");
        TreeItem<String> node1 = new TreeItem<>("node1");
        TreeItem<String> node2 = new TreeItem<>("node2");
        TreeItem<String> node3 = new TreeItem<>("node3");

// вдобавления нода в рут
        root.getChildren().add(node1);
        root.getChildren().add(node2);
        root.getChildren().addAll(node3);


// добавления рута в тривью.
        treeViewClient.setRoot(root);

        root.setExpanded(true);


    }


    /**
     * Метод отправки сообщеиня в сторону сервера.
     */
    public void sendMessageObject() {

        ClientSharedVariables.commandFromUsersUI.append(fileNameTextField.getText());

// NL клиент. Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)
        try {
            ClientSharedVariables.commandForSend.sendingSettings(ClientSharedVariables.commandFromUsersUI.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            ClientSharedVariables.clientNetListener.getSocketChannel().writeAndFlush(ClientSharedVariables.commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientSharedVariables.commandFromUsersUI.setLength(0);
    }
}
