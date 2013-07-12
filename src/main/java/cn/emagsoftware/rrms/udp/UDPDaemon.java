package cn.emagsoftware.rrms.udp;

import java.util.Properties;

/**
 * function description
 *
 * @author huzl
 * @version 1.0.0
 */
public class UDPDaemon {
    static int totalCount = 1;

    public static void main(String[] args) throws Exception {
        UDPServer udpServer = new UDPServer(9999);
        udpServer.setEventListener(new EventListener() {
            @Override
            public void messageReceive(InvokeEvent message) {
                System.out.println("messageReceive:" + message.seqId + "|" + message.sourceSystem + "->" +
                        message.destSystem + "|" + message.function + "|" + message.result + " : " + (totalCount++));
            }
        });
//        udpServer.start();
        URLResolverMapImpl urlResolver = new URLResolverMapImpl();
        Properties properties = new Properties() ;
        properties.put("/GameHall/Login","Login|HallPortal|GameHall");
        properties.put("/UCenter/Login","Login|UCenter|UCenter");
        urlResolver.setProperties(properties);
        final UDPClient client = new UDPClient("localhost", 9999, "GameHall", "UCenter");
        client.setUrlResolver(urlResolver);
        final String[] urls = {"http://192.168.169/GameHall/Login?uid=3","http://192.168.169/UCenter/Login?uid=3"};
        for (int j = 0; j < 10; j++) {

            new Thread("T" + j) {
                @Override
                public void run() {
                    long startTime = System.currentTimeMillis();
                    for (int i = 0; i < 10; i++) {
                        client.begin();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                             break;
                        }
                        client.commitUrl(urls[i%2], i%2);
                    }

                    System.out.println(Thread.currentThread().getName() + " take time " + (System.currentTimeMillis() - startTime));
                }
            }.start();

        }
    }


}
