package com.zhn.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by nan.zhang on 18-4-8.
 */
public class BounceShellBean {

    private BounceShell bounceShell;

    public BounceShellBean(BounceShell bounceShell) {

        this.bounceShell = bounceShell;
    }
}
