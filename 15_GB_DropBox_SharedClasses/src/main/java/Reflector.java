import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


public class Reflector extends ChannelInboundHandlerAdapter {
    private String handlerName;

    public Reflector(String handlerName) {
        this.handlerName = handlerName;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg == null) {
            System.out.println("Reflector.channelRead().msg = NULL");//log
            return;
        }

        try {
            ((CommandsList) msg).reflection(ctx, msg, ((CommandsList) msg).getWhoIsSender()); // NL обработка входящего объекта. Объект сам знает что от него требуется. см. Reflector()
        } catch (Exception e) {
            e.getMessage();
        } finally {
            ReferenceCountUtil.release(msg); // можно использовать in.release() // TODO: спросить Тренера в чем разница.
        }
    }

    /**
     * метод обработки ошибок при передаче сообщений В ЭТОМ ХЕНДЛЕРЕ
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        System.err.println(
                handlerName + ".Reflector.channelRead().error:  " + ctx.channel().remoteAddress() + "/" + ctx.channel().id().asShortText());//log
        cause.getMessage();

    }

}
