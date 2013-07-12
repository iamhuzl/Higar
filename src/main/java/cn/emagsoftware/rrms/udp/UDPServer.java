package cn.emagsoftware.rrms.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * UDP DatagramPacket Server,receive UDP Message
 *
 * @author huzl
 * @version 1.0.0
 */
public class UDPServer implements Runnable {
    private String host;
    private int port;
    private DatagramSocket dataSocket;
    private DatagramPacket dataPacket;
    private byte receiveByte[];
    private volatile boolean interrupted = false;
    private  final Logger logger = LoggerFactory.getLogger(getClass());
    private MessageParser messageParser = new MessageParser();
    private EventListener eventListener;

    public UDPServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public UDPServer(int port) {
        this.port = port;
    }


    public String getHost() {
        return host;
    }


    public int getPort() {
        return port;
    }

    public void setMessageParser(MessageParser messageParser) {
        this.messageParser = messageParser;
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void start() throws SocketException {
        dataSocket = new DatagramSocket(port);
        dataSocket.setSoTimeout(5 * 1000);
        dataSocket.setReceiveBufferSize(Integer.MAX_VALUE);
        new Thread(this).start();
    }

    @Override
    public void run() {
        receiveByte = new byte[1024];
        dataPacket = new DatagramPacket(receiveByte, receiveByte.length);
        logger.info("接收UPD包线程启动成功,开始接收调用事件....");
        while (!interrupted) {
            try {
                dataSocket.receive(dataPacket);
                if (dataPacket.getLength() <= 0) continue;
                InvokeEvent message = messageParser.parse(dataPacket.getData(), dataPacket.getOffset(), dataPacket.getLength());
                if (message == null) continue;
                eventListener.messageReceive(message);
            } catch (SocketTimeoutException e) {
                //不需要处理读超时
            }catch (Exception e) {
                logger.warn("receive message exception",e);
            }
        }
        logger.warn("接收UPD包线程停止");
    }

    public void stop() {
        interrupted = true;
        dataSocket.close();
        logger.warn("停止UDP接收线程");
    }
}
