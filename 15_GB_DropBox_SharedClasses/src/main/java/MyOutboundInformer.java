import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class MyOutboundInformer extends ChannelOutboundHandlerAdapter {
    String handlerName;

    public MyOutboundInformer(String handlerName) {
        this.handlerName = handlerName;
    }


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        System.out.println(handlerName +
                ".MyOutboundInformer.write(): " +
                "(msg.getClass():" + msg.getClass() +
                ") (remoteAddress: " + ctx.channel().remoteAddress() + ")");

        //ctx.write(msg); // NL отправляет сообщение в сторону OUT
        //ctx.writeAndFlush(msg); // NL отправляет сообщение в сторону OUT
        //ctx.fireChannelRead(msg); //ctx.flush(); // NL отправляет сообщение в сторону IN

        super.write(ctx, msg, promise);
    }

}
