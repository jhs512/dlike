package com.ll.dlike;

import com.ll.dlike.standard.util.Ut;

public class TestUt {
    public static boolean setFieldValue(Object o, String fieldName, Object value) {
        return Ut.reflection.setFieldValue(o, fieldName, value);
    }
}