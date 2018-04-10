package com.zhn.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;


/**
 * Created by nan.zhang on 18-4-8.
 */
@SpringBootApplication
@EnableBinding(BounceShell.class)
public class BounceShellReceiver {

    private static Logger logger = LoggerFactory.getLogger(BounceShellReceiver.class);

    @StreamListener(BounceShell.INPUT)
    public void receive(String playload) {
        System.out.println(playload);
        logger.info("Received:" + playload);
    }
}
