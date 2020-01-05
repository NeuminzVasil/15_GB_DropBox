import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MyInboundInformer extends ChannelInboundHandlerAdapter {
    String handlerName;

    public MyInboundInformer(String handlerName) {
        this.handlerName = handlerName;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //ctx.write(msg); // NL отправляет сообщение в сторону OUT
        //ctx.writeAndFlush(msg); // NL отправляет сообщение в сторону OUT
        //ctx.fireChannelRead(msg); //ctx.flush(); // NL отправляет сообщение в сторону IN
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.getMessage();
    }
}
