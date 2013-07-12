package cn.emagsoftware.rrms.udp;

/**
 * function description
 *
 * @author huzl
 * @version 1.0.0
 */
public class MessageParser {
    public InvokeEvent parse(byte[] body, int offset, int length) {
        String str = new String(body, offset, length);
        String[] arr = str.split("\\^");
        if (arr.length != 9) throw new IllegalArgumentException("Wrong Message Format:" + str);
        InvokeEvent message = new InvokeEvent();
        message.seqId = arr[0];
        message.sourceSystem = arr[1];
        message.sourceModule = arr[2];
        message.destSystem = arr[3];
        message.destModule = arr[4];
        message.function = arr[5];
        message.url = arr[6];
        message.elapseMillSeconds = Integer.valueOf(arr[7]);
        message.result = Integer.valueOf(arr[8]);
        return message;
    }

    public byte[] toBytes(InvokeEvent message) {
        StringBuilder builder = new StringBuilder(70);
        builder.append(trim(message.seqId)).append("^");
        builder.append(trim(message.sourceSystem)).append("^");
        builder.append(trim(message.sourceModule)).append("^");
        builder.append(trim(message.destSystem)).append("^");
        builder.append(trim(message.destModule)).append("^");
        builder.append(trim(message.function)).append("^");
        builder.append(trim(message.url)).append("^");
        builder.append(message.elapseMillSeconds).append("^");
        builder.append(message.result);
        return builder.toString().getBytes();
    }

    private static String trim(String str) {
        return str == null ? "" : str;
    }

    public static void main(String[] args) {
        MessageParser parser = new MessageParser();
        InvokeEvent message = new InvokeEvent();
        message.seqId = "123";
        message.function = "method";
        byte[] bytes = parser.toBytes(message);
        message = parser.parse(bytes, 0, bytes.length);
        System.out.println("message = " + message.function);
    }
}
