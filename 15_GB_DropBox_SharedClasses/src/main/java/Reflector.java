import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


public class Reflector extends ChannelInboundHandlerAdapter {
    private String handlerName;

    public Reflector(String handlerName) {
        this.handlerName = handlerName;
    }

    private CommandAnswer.WhoIsSender reversWhoIsSender(CommandAnswer.WhoIsSender whoIsSender) {
        if (whoIsSender == CommandAnswer.WhoIsSender.SERVER) return CommandAnswer.WhoIsSender.CLIENT;
        else return CommandAnswer.WhoIsSender.SERVER;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {


        try {
            if (msg == null) {
                System.out.println("Reflector.channelRead().msg = NULL");//log
                return;
            }

            // NL обработка входящего объекта. Объект сам знает что от него требуется. см. Reflector()

            if (msg instanceof CommandsList.GetStorageInfo) {
                ((CommandsList.GetStorageInfo) msg).reflection(ctx, msg, ((CommandsList.GetStorageInfo) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.SendFileToServer) {
                ((CommandsList.SendFileToServer) msg).reflection(ctx, msg, ((CommandsList.SendFileToServer) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.UserRegistering) {
                ((CommandsList.UserRegistering) msg).reflection(ctx, msg, ((CommandsList.UserRegistering) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.GetFileFromServer) {
                ((CommandsList.GetFileFromServer) msg).reflection(ctx, msg, ((CommandsList.GetFileFromServer) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.DeleteFile) {
                ((CommandsList.DeleteFile) msg).reflection(ctx, msg, ((CommandsList.DeleteFile) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.RenamingFile) {
                ((CommandsList.RenamingFile) msg).reflection(ctx, msg, ((CommandsList.RenamingFile) msg).getWhoIsSender());

            } else {
                System.out.println("Reflector: unknown command!");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        cause.printStackTrace();

    }


}
