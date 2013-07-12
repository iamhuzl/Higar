package cn.emagsoftware.rrms.udp;

/**
 * HTTP调用
 *
 * @author huzl
 * @version 1.0.0
 */
public interface EventListener {
    public void messageReceive(InvokeEvent message);
}
