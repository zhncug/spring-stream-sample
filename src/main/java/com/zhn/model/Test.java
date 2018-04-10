package com.zhn.model;

import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Field;

/**
 * Created by nan.zhang on 18-4-4.
 */
public class Test<T> {
    private Class<T> type;

    public Test() {
        try {
            Field field = this.getClass().getField("type");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public Class<T> getDataClass() {
        try {
            return (Class<T>) this.getClass().getField("type").getType();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Test<String> t = new Test<>();
        System.out.println(t.getDataClass());
    }
}
