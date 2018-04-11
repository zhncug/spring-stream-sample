package com.zhn.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by nan.zhang on 18-4-8.
 */
public interface BounceShell {

    String INPUT = "bounce_shell_input";

    String OUPUT = "bounce_output";

    @Input(INPUT)
    SubscribableChannel input();

    @Output(OUPUT)
    MessageChannel output();
}
