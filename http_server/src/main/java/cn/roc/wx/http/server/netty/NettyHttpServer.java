package cn.roc.wx.http.server.netty;

import cn.roc.wx.http.server.netty.impl.NettyHttpServerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2017/10/23.
 */
public class NettyHttpServer {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-context.xml");
        NettyHttpServerImpl httpServer = (NettyHttpServerImpl) context.getBean("nettyHttpServerImpl");
        httpServer.run();
    }

}
