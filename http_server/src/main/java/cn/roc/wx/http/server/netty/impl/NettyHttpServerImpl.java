package cn.roc.wx.http.server.netty.impl;

import cn.roc.wx.http.server.netty.INettyHttpServer;
import cn.roc.wx.http.server.netty.handler.HttpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/10/23.
 */
@Component
public class NettyHttpServerImpl implements INettyHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyHttpServerImpl.class);

    @Value(value = "${port}")
    private int port;
    @Autowired
    private HttpServerHandler httpServerHandler;

    @Override
    public void run() {
        //线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
                            ch.pipeline().addLast(new HttpResponseEncoder())
                                    // server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
                                    .addLast(new HttpRequestDecoder())
                                    //服务端主业务
                                    .addLast(httpServerHandler);
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            logger.debug("HttpServer has started successfully, and is waiting for client connecting...");
            future.channel().closeFuture().sync();
        }catch (Exception e) {
            logger.error("HttpServer started failed, error is :{}", e.getMessage());
        }finally {
            //友好退出线程组
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}