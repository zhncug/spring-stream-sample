package com.zhn.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;


/**
 * Created by nan.zhang on 18-4-8.
 */
@Component
@EnableBinding(BounceShell.class)
public class BounceShellReceiver {

    private static Logger logger = LoggerFactory.getLogger(BounceShellReceiver.class);
    private BounceShell bounceShell;

    @Autowired
    public BounceShellReceiver(BounceShell bounceShell) {
        this.bounceShell = bounceShell;
    }

    /**
     * 接受input消息，并发送一条消息到output
     *
     * @param playload
     */
    @StreamListener(BounceShell.INPUT)
    @SendTo(BounceShell.OUTPUT)
    public String receive(String playload) {
        logger.info("Received:" + playload);
        return send("Java received a message");
    }


//    @StreamListener(BounceShell.OUTPUT)
//    public void receive2(String msg) {
//        logger.info("receive msg:" + msg);
//    }

    public String send(String message) {
        logger.info("send message:" + message);
        MessageChannel messageChannel = bounceShell.output();
        messageChannel.send(MessageBuilder
                .withPayload(message)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());

        return message;
    }
}
