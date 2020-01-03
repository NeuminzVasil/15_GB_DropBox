import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ServerNetListener implements Runnable {
    private String handlerName;
    private EventLoopGroup bossGroup = new NioEventLoopGroup(); // пул потоков для организации подключения
    private EventLoopGroup workerGroup = new NioEventLoopGroup(); // пул потоков для обработки данных
    private ServerBootstrap serverBootstrap = new ServerBootstrap(); //ServerBootstrap - для предварительных настроек сервера
    private SocketChannel socketChannel;
    private ChannelFuture channelFuture;
    private ChannelInitializer channelInitializer;

    public ServerNetListener(String handlerName) {
        this.handlerName = handlerName;
    }

    public void run() {

        try {

            channelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) { // настройка конвеера для каждого подключившегося клиента
                    socketChannel = ch;
                    //socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
//                    socketChannel.pipeline().addLast(handlerName + ".AfterEncodingInformer", new MyOutboundInformer(handlerName.concat(".AfterEncodingInformer")));
                    socketChannel.pipeline().addLast(handlerName + ".ObjectEncoder", new ObjectEncoder());
//                    socketChannel.pipeline().addLast(handlerName + ".BeforeEncodingInformer", new MyOutboundInformer(handlerName.concat(".BeforeEncodingInformer")));
//                    socketChannel.pipeline().addLast(handlerName + ".BeforeDecodingInformer", new MyInboundInformer(handlerName.concat(".BeforeDecodingInformer")));
                    socketChannel.pipeline().addLast(handlerName + ".ObjectDecoder", new ObjectDecoder(Settings.MAX_OBJECT_SIZE, ClassResolvers.cacheDisabled(null)));
//                    socketChannel.pipeline().addLast(handlerName + ".AfterDecodingInformer", new MyInboundInformer(handlerName.concat(".AfterDecodingInformer")));
                    socketChannel.pipeline().addLast(new Reflector(handlerName));
                }
            };

            serverBootstrap.group(bossGroup, workerGroup) // указание пулов потоков для работы сервера
                    .channel(NioServerSocketChannel.class) // указание канала для подключения новых клиентов
                    .childHandler(channelInitializer).childOption(ChannelOption.SO_KEEPALIVE, true);

            channelFuture = serverBootstrap.bind(Settings.HOST_PORT).sync();// запуск прослушивания порта 8189 для подключения клиентов
            System.out.println("ServerNetListener.Works.");
//             NL - сервер. здесь происходит магия.
//                - блокирующая команда.

            channelFuture.channel().closeFuture().sync();// ожидание завершения работы сервера
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    @Override
    public String toString() {
        String result = String.format("ServerNetListener state: \n\t" +
                        "serverName: %s\n\t" +
                        "bossGroup: %s\n\t" +
                        "workerGroup: %s\n\t" +
                        "serverBootstrap: %s\n\t" +
                        "channelFuture: %s\n\t" +
                        "socketChannel: %s\n\t" +
                        "channelInitializer: %s",
                this.handlerName,
                this.bossGroup,
                this.workerGroup,
                this.serverBootstrap,
                this.channelFuture,
                this.socketChannel,
                this.channelInitializer);
        return result;
    }
}