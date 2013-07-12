/*
 * @(#)InvokeEventServer.java 1.0.0 13/07/12
 * Copyright 2013© Emagsoftware Technology Co., Ltd. All Rights reserved.
 */

package cn.emagsoftware.rrms.server;

import cn.emagsoftware.rrms.udp.UDPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Server Daemon 启动UDP Server
 *
 * @author huzl
 * @version 1.0.0
 */
public class ServerDaemon {
    public static final Logger logger = LoggerFactory.getLogger(ServerDaemon.class);
    public static void main(String[] args) throws Exception{
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UDPServer server = (UDPServer) context.getBean("server");
        server.start();
        logger.info("启动调用统计UDP服务成功，监听端口 {}",server.getPort());
    }
}
