package Shared;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyInboundInformer extends ChannelInboundHandlerAdapter {
    String handlerName;

    public MyInboundInformer(String handlerName) {
        this.handlerName = handlerName;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(handlerName +
                ".MyInboundInformer.channelRegistered(): " +
                "(remoteAddress: " + ctx.channel().remoteAddress() + ")"); // log
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(handlerName +
                ".MyInboundInformer.channelRead(): " +
                "(msg.getClass(): " + msg.getClass() + ") " +
                "(remoteAddress: " + ctx.channel().remoteAddress() + ")"); // log
        //ctx.write(msg); // NL отправляет сообщение в сторону OUT
        //ctx.writeAndFlush(msg); // NL отправляет сообщение в сторону OUT
        //ctx.fireChannelRead(msg); //ctx.flush(); // NL отправляет сообщение в сторону IN
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println(handlerName +
                ".MyInboundInformer.exceptionCaught(): " +
                "cause:" + cause.getMessage() + ". " +
                "(remoteAddress: " + ctx.channel().remoteAddress() + ")"); // log
        cause.printStackTrace();
    }
}
