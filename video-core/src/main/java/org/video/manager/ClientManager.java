package org.video.manager;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import org.video.exception.BaseException;
import org.video.netty.Client;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientManager {
    // <clientId,client> forbid null key and value
    private static ConcurrentMap<String, Client> clientMap = new ConcurrentHashMap();

    public static Client put(String clientId, Client client) {
        return clientMap.putIfAbsent(clientId, client);
    }

    public static Client get(String clientId) {
        return clientMap.get(clientId);
    }

    public static Channel channel(String channelId) {
        if (StrUtil.isBlank(channelId)) throw new BaseException("channelId is null");
        Iterator<Map.Entry<String, Client>> it = clientMap.entrySet().iterator();
        while (it.hasNext()) {
            Channel c = it.next().getValue().channel();
            if (channelId.equals(c.id().asLongText())) {
                return c;
            }
        }
        throw new BaseException("根据channelId" + channelId + "获取不到channel");
    }

    public static Client client(String channelId) {
        if (StrUtil.isBlank(channelId))  throw new BaseException("channelId is null");
        Iterator<Map.Entry<String, Client>> it = clientMap.entrySet().iterator();
        while (it.hasNext()) {
            Client client = it.next().getValue();
            Channel c = client.channel();
            if (channelId.equals(c.id().asLongText())) {
                return client;
            }
        }
        throw new BaseException("根据channelId" + channelId + "获取不到client");
    }

    public static Client remove(String clientId) {
        return clientMap.remove(clientId);
    }

}
