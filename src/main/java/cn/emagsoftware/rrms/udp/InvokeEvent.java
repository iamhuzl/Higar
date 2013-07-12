package cn.emagsoftware.rrms.udp;

import java.util.UUID;

/**
 * function description
 *
 * @author huzl
 * @version 1.0.0
 */
public class InvokeEvent {
    public String seqId;
    public String sourceSystem;
    public String sourceModule;
    public String destSystem;
    public String destModule;
    public String function;
    public String url;
    public long elapseMillSeconds;
    public int result;
    public int count = 1;
    public InvokeEvent() {
    }

    public InvokeEvent(String sourceSystem, String sourceModule, String destSystem, String destModule) {
        this.sourceSystem = sourceSystem;
        this.sourceModule = sourceModule;
        this.destSystem = destSystem;
        this.destModule = destModule;
        this.seqId = UUID.randomUUID().toString().replace("-", "");
    }
}
