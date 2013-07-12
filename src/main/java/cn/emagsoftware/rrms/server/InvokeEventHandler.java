/*
 * @(#)InvokeEventHandler.java 1.0.0 13/07/12
 * Copyright 2013© Emagsoftware Technology Co., Ltd. All Rights reserved.
 */

package cn.emagsoftware.rrms.server;

import cn.emagsoftware.rrms.udp.EventListener;
import cn.emagsoftware.rrms.udp.InvokeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 保存调用事件信息到队列中，每分钟进行一次批量保存
 *
 * @author huzl
 * @version 1.0.0
 */
@Component(value = "eventListener")
public class InvokeEventHandler extends TimerTask implements EventListener, InitializingBean {
    private  final Logger logger = LoggerFactory.getLogger(getClass());
    private volatile LinkedList messages = new LinkedList();
    private Timer timer;
    @Autowired
    private MessageDao messageDao;

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public void messageReceive(InvokeEvent message) {
        if(logger.isDebugEnabled())
        logger.debug("Receive InvokeEvent seqId:{},function:{},destSystem:{},destModule:{}," +
                "sourceSystem:{},sourceModule:{},count:{},result:{},",new Object[]{
                message.seqId,message.function,message.destSystem,message.destModule,
                message.sourceSystem,message.sourceModule,
                message.count,message.result});
        messages.add(message);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean isDaemon = true;
        timer = new Timer("message", isDaemon);
        timer.schedule(this, 0, 60 * 1000L);
    }

    @Override
    public void run() {
        new Thread() {
            @Override
            public void run() {
                saveMessages();
            }
        }.start();

    }

    /**
     * 保存调用信息到数据库，重新清除数据
     */
    public void saveMessages() {
        List messages = this.messages;
        this.messages = new LinkedList();
        messageDao.saveAll(messages);
    }


}
