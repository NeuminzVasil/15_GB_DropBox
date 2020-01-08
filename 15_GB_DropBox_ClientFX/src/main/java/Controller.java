import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Controller {

    static String clientName = "ClientFXCommandLine";
    static ClientNetListener clientNetListener; // ссылка на клиентское подключение
    @FXML
    TextField fileNameTextField;
    @FXML
    Button getFileButton;
    @FXML
    Button sendFileButton;
    private StringBuilder commandFromUsersUI = new StringBuilder();
    private CommandAnswer commandForSend = new CommandsList(); //объект "команды пользователя"


    public void sendMessageObject() {

        commandFromUsersUI.append(fileNameTextField.getText());

// NL клиент. Любой интерфейс пользователя, для взаимодействя с сервером, обязан выполнить два пункта:
//  - 1) подготовить команду commandForSend.sendingSettings() принимает 3 параметра:
//      -- мнемокод команды с параметрами,
//      -- суффикс отравителя,
//      -- Регистационный номер клиента, полученный при аутентификации.
//  - 2) отправить команду в сеть writeAndFlush(String ПОДГОТОВЛЕННАЯ_КОМАНДА)
        try {
            commandForSend.sendingSettings(commandFromUsersUI.toString(), CommandAnswer.WhoIsSender.CLIENT);  // NL клиент. подготовка  команды
            clientNetListener.getSocketChannel().writeAndFlush(commandForSend); // NL клиент. отправка команды в сеть.
        } catch (IOException e) {
            e.printStackTrace();
        }

        commandFromUsersUI.setLength(0);
    }

}
