package com.zhn.zhn;

import com.zhn.stream.BounceShellReceiver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(BounceShellReceiver.class)
public class ZhnApplication {

    public static void main(String[] args) {

        SpringApplication.run(ZhnApplication.class, args);
    }
}
