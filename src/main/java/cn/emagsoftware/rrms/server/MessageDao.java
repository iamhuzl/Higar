/*
 * @(#)MessageDao.java 1.0.0 13/07/12
 * Copyright 2013© Emagsoftware Technology Co., Ltd. All Rights reserved.
 */

package cn.emagsoftware.rrms.server;

import cn.emagsoftware.rrms.udp.InvokeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 保存调用事件统计信息
 *
 * @author huzl
 * @version 1.0.0
 */
@Component
public class MessageDao {
    private  final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public static final String INSERT_SQL = "insert into TB_RMS_INVOKE_LOG(source_system,source_module,dest_sytem,dest_mudule,function,count,elapse_mill_seconds,result,create_time)" +
            " values (?,?,?,?,?,?,?,?,now())";

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(Collection messages) {
        Collection oneMinuteMessages = mergeRepeated(messages);
        logger.info("Save Message,total messages count:{},merge repeated count:{}",messages.size(),oneMinuteMessages.size());
        List<Object[]> params = new ArrayList<Object[]>(oneMinuteMessages.size());
        for (java.util.Iterator iterator = oneMinuteMessages.iterator(); iterator.hasNext(); ) {
            InvokeEvent msg = (InvokeEvent) iterator.next();
            params.add(new Object[]{msg.sourceSystem,msg.sourceModule,msg.destSystem,msg.destModule,msg.function,msg.count,msg.elapseMillSeconds,msg.result});
        }
        jdbcTemplate.batchUpdate(INSERT_SQL,params);
    }

    /**
     * 合并相同调用接口（接口和成功字段),计算平均响应时间
     * @param messages
     * @return
     */
    private Collection mergeRepeated(Collection messages) {
        Map<String, InvokeEvent> map = new HashMap<String, InvokeEvent>(messages.size());
        for (Iterator iterator = messages.iterator(); iterator.hasNext(); ) {
            InvokeEvent message = (InvokeEvent) iterator.next();
            String key = buildKey(message);
            InvokeEvent existMsg = map.get(key);
            if (existMsg != null) {
                existMsg.elapseMillSeconds = calculateAverage(existMsg, message.elapseMillSeconds);
                existMsg.count = existMsg.count+1;
            } else {
                map.put(key, message);
            }
        }

        return map.values();
    }

    private long calculateAverage(InvokeEvent message, long elapseMillSeconds) {
        return (message.elapseMillSeconds * message.count + elapseMillSeconds) / (message.count + 1);
    }

    private String buildKey(InvokeEvent message) {
        StringBuilder buf = new StringBuilder(64);
        buf.append(message.sourceSystem).append(message.sourceModule)
                .append(message.destSystem).append(message.destModule)
                .append(message.function).append(message.result);
        return buf.toString();
    }
}
