package Shared;

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

            // NL обработка входящего объекта. Объект команды сам знает что от него требуется. см. Reflector()

            if (msg instanceof CommandsList.ClientStorageInfo) {
                ((CommandsList.ClientStorageInfo) msg).Reflection(ctx, msg, ((CommandsList.ClientStorageInfo) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.SendFileToServer) {
                ((CommandsList.SendFileToServer) msg).Reflection(ctx, msg, ((CommandsList.SendFileToServer) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.ServerStorageInfo) {
                ((CommandsList.ServerStorageInfo) msg).Reflection(ctx, msg, ((CommandsList.ServerStorageInfo) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.GetFileFromServer) {
                ((CommandsList.GetFileFromServer) msg).Reflection(ctx, msg, ((CommandsList.GetFileFromServer) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.DeleteFile) {
                ((CommandsList.DeleteFile) msg).Reflection(ctx, msg, ((CommandsList.DeleteFile) msg).getWhoIsSender());

            } else if (msg instanceof CommandsList.RenamingFile) {
                ((CommandsList.RenamingFile) msg).Reflection(ctx, msg, ((CommandsList.RenamingFile) msg).getWhoIsSender());

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
