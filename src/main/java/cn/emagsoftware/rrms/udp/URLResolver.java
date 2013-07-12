package cn.emagsoftware.rrms.udp;

/**
 * resolve ulr to function name;
 *
 * @author huzl
 * @version 1.0.0
 */
public interface URLResolver {
    Destination resolve(String url);
    class Destination {
        public static final Destination EMPTY_DESTINATION = new Destination();
        public String function;
        public String destSystem;
        public String destModule;
    }
}

