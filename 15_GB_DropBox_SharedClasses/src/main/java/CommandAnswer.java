import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.io.Serializable;

public interface CommandAnswer extends Serializable {

    void reflection(ChannelHandlerContext ctx) throws IOException;

    WhoIsSender SENDER_TYPE = WhoIsSender.NULL;

    void sendingSettings(String usersCommand, WhoIsSender whoIsSender) throws IOException;

    WhoIsSender getWhoIsSender();

    String getRegisteredUserID();

    enum WhoIsSender {
        SERVER, CLIENT, NULL
    }
}
