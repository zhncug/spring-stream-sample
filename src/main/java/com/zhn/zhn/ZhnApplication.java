package com.zhn.zhn;

import com.zhn.stream.BounceShell;
import com.zhn.stream.BounceShellReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@SpringBootApplication
public class ZhnApplication {

    private static Logger logger = LoggerFactory.getLogger(ZhnApplication.class);

//    @StreamListener(BounceShell.INPUT)
//    public void receive(String playload) {
//        System.out.println(playload);
//        logger.info("Received:" + playload);
//    }

    public static void main(String[] args) {

        SpringApplication.run(ZhnApplication.class, args);
    }
}
