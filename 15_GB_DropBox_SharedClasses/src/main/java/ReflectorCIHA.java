import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


public class ReflectorCIHA extends ChannelInboundHandlerAdapter {
    private String handlerName;

    public ReflectorCIHA(String handlerName) {
        this.handlerName = handlerName;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
/**
 *  NL reflector. Обработка входящего объекта-команды на уровне сети в ChannelInboundHandlerAdapter.
 *      Msg - это "объект-команда", со всеми параметрами в своих полях.
 *      Запусткаем у объекта метод .reflection - объект сам знает что от него хотят.
 */
        try {
            ((CommandsList) msg).reflection(ctx);
        } catch (Exception e) {
            e.getMessage();
        } finally {
// TODO: можно использовать in.release()
//  - спросить Тренера: - 1.в чем разница
//  - релиз происходит только для ЭТОГО хенлера или для всего пайпа?
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * метод обработки ошибок при передаче сообщений В ЭТОМ ХЕНДЛЕРЕ
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.getMessage();
    }

}
