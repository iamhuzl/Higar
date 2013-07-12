package cn.emagsoftware.rrms.udp;

import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 根据URL前缀匹配，把HTTP URL转成功能名称或标示
 *
 * @author huzl
 * @version 1.0.0
 */
public class URLResolverMapImpl implements URLResolver {
    private Properties properties;
    private Map<Pattern, Destination> rules = Collections.emptyMap();
    private Map<String, Destination> hitKeyCache = Collections.emptyMap();

    public void setProperties(Properties properties) {
        this.properties = properties;
        hitKeyCache = new ConcurrentHashMap<String, Destination>(256);
        rules = new HashMap<Pattern, Destination>(properties.size());
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String regexPattern = (String) keys.nextElement();
            String[] arr =  StringUtils.split(properties.getProperty(regexPattern), "|");
            Destination destination = new Destination();
            destination.function = arr[0];
            if(arr.length>1)destination.destSystem = arr[1];
            if(arr.length>2)destination.destModule = arr[2];
            rules.put(Pattern.compile(regexPattern),destination);
        }
    }

    @Override
    public Destination resolve(String url) {
        if (url == null) return null;
        int urlParameterPosition = url.indexOf('?');
        if (urlParameterPosition > 0) url = url.substring(0, urlParameterPosition);
        if(hitKeyCache.containsKey(url))return hitKeyCache.get(url);
        Set<Pattern> keys = rules.keySet();
        Destination destination = Destination.EMPTY_DESTINATION;
        for (Iterator<Pattern> iterator = keys.iterator(); iterator.hasNext(); ) {
            Pattern pattern = iterator.next();
            if(!pattern.matcher(url).find())continue;
            destination = rules.get(pattern);
            break;
        }
        hitKeyCache.put(url,destination);
        return destination;
    }

}
