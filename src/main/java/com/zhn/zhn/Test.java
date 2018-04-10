package com.zhn.zhn;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * Created by nan.zhang on 18-3-6.
 */
public class Test {

    public static void main(String[] args) throws UnsupportedEncodingException {
//        String name = "命令执行函数&amp;amp;GET|POST|REQUEST";
//        System.out.println(StringEscapeUtils.unescape(name));
        BigDecimal base = BigDecimal.valueOf(4800);
        BigDecimal crease = BigDecimal.valueOf(1.06);
        int yearLimit = 20;

        double allPay = 0d;
        for (int i = 0; i < yearLimit; i++) {
            allPay += base.multiply(crease.pow(i)).doubleValue()
                    + crease.pow(i).multiply(BigDecimal.valueOf(i)).multiply(BigDecimal.valueOf(600)).doubleValue()
                    - 98 * 12;
        }
        System.out.println(allPay);
    }
}
