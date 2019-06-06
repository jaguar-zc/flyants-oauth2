package org.flyants.chat.dto.app;

import lombok.Getter;
import lombok.Setter;
import org.flyants.chat.domain.message.MessageType;

import java.util.Date;

/**
 * @Author zhangchao
 * @Date 2019/6/6 10:38
 * @Version v1.0
 */
@Getter
@Setter
public class MessageDto {
    private String id;
    private MessageType messageType;
    private String body;
    private Date sendTime;
    private Integer view;
}