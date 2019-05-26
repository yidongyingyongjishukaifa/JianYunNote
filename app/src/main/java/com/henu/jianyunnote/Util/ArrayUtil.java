package com.henu.jianyunnote.Util;

import java.lang.reflect.Array;

public class ArrayUtil {
    public static Object arrayAddLength(Object oldArray, int addLength) {
        Class c = oldArray.getClass();
        if (!c.isArray()) return null;
        Class componentType = c.getComponentType();
        int length = Array.getLength(oldArray);
        int newLength = length + addLength;
        Object newArray = Array.newInstance(componentType, newLength);
        System.arraycopy(oldArray, 0, newArray, 1, length);
        return newArray;
    }
}
