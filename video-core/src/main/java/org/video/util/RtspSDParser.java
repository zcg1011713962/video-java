package org.video.util;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class RtspSDParser implements Serializable {

    private StringBuffer sdp = new StringBuffer();
    private String cseq;
    private String contentType;
    private String contentBase;
    private int contentLength;
    private int vPort;
    private int aPort;
    private int trackID;

    public RtspSDParser append(String str) {
        sdp.append(str);
        return this;
    }


    public boolean parser() {
        String content = sdp.toString();
        if (StrUtil.isBlank(content)) {
            return false;
        }
        if (success(content)) {
            return true;
        }
        String[] line = content.split("\r\n");
        for (String c : line) {
            if (c == null) continue;
            if (c.contains("a=")) {
                if (c.contains("trackID=")) {
                    trackID = Integer.parseInt(c.split("trackID=")[1].trim());
                }
            } else if (c.contains("b=")) {

            } else if (c.contains("c=")) {

            } else if (c.contains("m=")) {
                if (c.contains("audio")) {

                } else if (c.contains("video")) {

                }
            }
        }
        return success(content);
    }

    private boolean success(String content) {
        if (StrUtil.isNotBlank(content) && contentLength > 0 && content.length() == contentLength) {
            return true;
        }
        return false;
    }


}
