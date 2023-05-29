package org.video.util;

import java.net.URI;

public class RtspUrlParser {

    private String url;
    private String ip;
    private int port;

    public RtspUrlParser(String url) {
        this.url = url;
    }

    public boolean parse() {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || !scheme.equals("rtsp")) {
                return false;
            }
            String host = uri.getHost();
            int p = uri.getPort();
            if (p == -1) {
                p = 554;
            }
            ip = host;
            port = p;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
