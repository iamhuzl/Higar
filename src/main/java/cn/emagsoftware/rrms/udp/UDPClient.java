package cn.emagsoftware.rrms.udp;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * function description
 *
 * @author huzl
 * @version 1.0.0
 */
public class UDPClient {
    private DatagramSocket dataSocket;
    private MessageParser messageParser = new MessageParser();
    private URLResolver urlResolver;
    private String host;
    private int port;
    private String sourceSystem;
    private String sourceModule;
    private ThreadLocal local = new ThreadLocal();
    private boolean enabled = true;
    public UDPClient(String host, int port) throws SocketException {
        this();
        this.host = host;
        this.port = port;
    }

    public UDPClient(String host, int port, String sourceSystem, String sourceModule)throws SocketException  {
        this();
        this.host = host;
        this.port = port;
        this.sourceSystem = sourceSystem;
        this.sourceModule = sourceModule;
    }

    public UDPClient() throws SocketException {
        dataSocket = new DatagramSocket();
        dataSocket.setSendBufferSize(Integer.MAX_VALUE);
    }

    public void setMessageParser(MessageParser messageParser) {
        this.messageParser = messageParser;
    }

    public void setUrlResolver(URLResolver urlResolver) {
        this.urlResolver = urlResolver;
    }

    public void send(byte[] bytes) throws IOException {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port);
        dataSocket.send(packet);
    }

    public void send(InvokeEvent message) throws IOException {
        if(!enabled)return;
        if(StringUtils.isEmpty(message.function)){
            URLResolver.Destination destination = urlResolver.resolve(message.url);
            if(destination == null)return;
            message.function = destination.function;
            message.destSystem = destination.destSystem;
            message.destModule = destination.destModule;
        }
        if(StringUtils.isEmpty(message.function))return;
        send(messageParser.toBytes(message));
    }

    public void sendQuiet(InvokeEvent message) {
        try {
            send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void begin() {
        if(!enabled)return;
        local.set(System.currentTimeMillis());
    }


 public void commitUrl( String url, int result) {

        commitMessage(null, null, url, null, result);

    }

    public void commitFunction(String destSystem, String destModule, String function, int result) {
        commitMessage(destSystem, destModule, null, function, result);
    }


    public void commitMessage(String destSystem, String destModule, String url, String function, int result) {
        if(!enabled)return;
        Long beginTime = (Long) local.get();
        if (beginTime == null) throw new IllegalStateException("Must invoke method begin() first");
        InvokeEvent message = new InvokeEvent(sourceSystem, sourceModule, destSystem, destModule);
        message.function = function;
        message.url = url;
        message.result = result;
        message.elapseMillSeconds = System.currentTimeMillis() - beginTime;
        sendQuiet(message);
        local.set(null);

    }
}
