package org.video.manager;

import com.google.common.collect.HashBasedTable;
import io.netty.channel.ChannelHandler;
import org.video.eum.Protocol;

public class CacheManager {
    private static HashBasedTable<Protocol, ChannelHandler, Boolean> protocolTable = HashBasedTable.create();

    public static HashBasedTable<Protocol, ChannelHandler, Boolean> protocolTable() {
        return protocolTable;
    }


}
