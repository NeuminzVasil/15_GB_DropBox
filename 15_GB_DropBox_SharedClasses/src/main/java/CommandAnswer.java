import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface CommandAnswer {

    WhoIsSender SENDER_TYPE = WhoIsSender.NULL;

    void reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) throws IOException;

    WhoIsSender getWhoIsSender();

    void sendingSettings(String usersCommand, WhoIsSender whoIsSender);

    enum WhoIsSender {
        SERVER, CLIENT, NULL
    }
}
