import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface CommandAnswer extends Serializable {

    void reflection(ChannelHandlerContext ctx) throws IOException;

    List<File> getFiles();

    WhoIsSender SENDER_TYPE = WhoIsSender.NULL;

    void sendingSettings(String usersCommand, WhoIsSender whoIsSender) throws IOException;

    WhoIsSender getWhoIsSender();

    String getRegisteredUserID();

    enum WhoIsSender {
        SERVER, CLIENT, NULL
    }
}
