package Client;

import Shared.Reflector;
import Shared.Settings;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;

import static Shared.Settings.MAX_OBJECT_SIZE;

public class ClientNetListener implements Runnable {

    private String handlerName;
    private EventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
    private Bootstrap clientBootstrap = new Bootstrap();
    private ChannelFuture channelFuture;
    private SocketChannel socketChannel;
    private ChannelInitializer channelInitializer;

    public ClientNetListener(String handlerName) {
        this.handlerName = handlerName;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public EventLoopGroup getNioEventLoopGroup() {
        return nioEventLoopGroup;
    }

    public Bootstrap getClientBootstrap() {
        return clientBootstrap;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    @Override
    public void run() {

        // NL клиент. подключемся к серверу
        try {

            clientBootstrap.group(nioEventLoopGroup);

            clientBootstrap.channel(NioSocketChannel.class);

            clientBootstrap.remoteAddress(new InetSocketAddress(Settings.hostName, Settings.hostPort));

            channelInitializer = new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    socketChannel = ch;
                    //ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
//                    socketChannel.pipeline().addLast(handlerName + ".AfterEncodingInformer", new MyOutboundInformer(handlerName.concat(".AfterEncodingInformer")));
                    socketChannel.pipeline().addLast(handlerName + ".ObjectEncoder", new ObjectEncoder());
//                    socketChannel.pipeline().addLast(handlerName + ".BeforeEncodingInformer", new MyOutboundInformer(handlerName.concat(".BeforeEncodingInformer")));
//                    socketChannel.pipeline().addLast(handlerName + ".BeforeDecodingInformer", new MyInboundInformer(handlerName.concat(".BeforeDecodingInformer")));
                    socketChannel.pipeline().addLast(handlerName + ".ObjectDecoder", new ObjectDecoder(MAX_OBJECT_SIZE, ClassResolvers.cacheDisabled(null)));
//                    socketChannel.pipeline().addLast(handlerName + ".AfterDecodingInformer", new MyInboundInformer(handlerName.concat(".AfterDecodingInformer")));
                    socketChannel.pipeline().addLast(new Reflector(handlerName));
                }
            };

            clientBootstrap.handler(channelInitializer);
            System.out.println("ClientNetListener.Works.");
            channelFuture = clientBootstrap.connect().sync();

//             NL - клиент. здесь происходит магия.
//                - блокирующая команда.

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                nioEventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        String result = String.format("ClientNetListener state: \n\t" +
                        "clientName: %s\n\t" +
                        "nioEventLoopGroup: %s\n\t" +
                        "clientBootstrap: %s\n\t" +
                        "channelFuture: %s\n\t" +
                        "socketChannel: %s\n\t" +
                        "channelInitializer: %s",
                this.handlerName,
                this.nioEventLoopGroup,
                this.clientBootstrap,
                this.channelFuture,
                this.socketChannel,
                this.channelInitializer);
        return result;
    }
}