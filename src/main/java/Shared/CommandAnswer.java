package Shared;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public interface CommandAnswer {

    WhoIsSender SENDER_TYPE = WhoIsSender.NULL;

    void Reflection(ChannelHandlerContext ctx, Object msg, WhoIsSender whoIsSender) throws IOException;

    WhoIsSender getWhoIsSender();

    enum WhoIsSender {
        SERVER, CLIENT, NULL
    }
}
