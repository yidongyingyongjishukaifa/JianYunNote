package com.henu.jianyunnote.Util;

public class ArrayUtil {
    public static int[] insert2Array(int[] oldArray, int id) {
        int[] newArray;
        if (oldArray == null || oldArray.length == 0) {
            newArray = new int[1];
            newArray[0] = id;
        } else {
            newArray = new int[oldArray.length + 1];
            for (int i = 0; i < newArray.length; i++) {
                if (i == 0) {
                    newArray[i] = id;
                } else {
                    newArray[i] = oldArray[i - 1];
                }
            }
        }
        return newArray;
    }

    public static int[] deleteIdInArray(int[] oldArray, int position) {
        int[] newArray;
        newArray = new int[oldArray.length - 1];
        for (int i = 0; i < position; i++) {
            newArray[i] = oldArray[i];
        }
        for (int i = position; i < newArray.length; i++) {
            newArray[i] = oldArray[i + 1];
        }
        return newArray;
    }
}
