package org.video.util;

import java.net.URI;

public class RtspUrlParser {

    private String url;
    private String ip;
    private int port;
    private String username;
    private String password;
    private String uri;

    public RtspUrlParser(String url) {
        this.url = url;
    }

    public boolean parse() {
        try {
            URI u = new URI(url);
            String scheme = u.getScheme();
            if (scheme == null || !scheme.equals("rtsp")) {
                return false;
            }
            ip = u.getHost();
            port = u.getPort();
            if (port == -1) {
                port = 554;
            }
            uri = new StringBuffer().append(u.getScheme()).append("://").append(ip).append(":").append(port).append(u.getPath()).toString();
            username = u.getUserInfo().split(":")[0].trim();
            password = u.getUserInfo().split(":")[1].trim();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUri() {
        return uri;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
