package com.henu.jianyunnote.Util;

public class ArrayUtil {
    public static int[] insert2Array(int[] oldArray, int id) {
        int[] newArray = new int[oldArray.length + 1];
        for (int i = 0; i < newArray.length; i++) {
            if(i==0){
                newArray[i]=id;
            }else{
                newArray[i]=oldArray[i-1];
            }
        }
        return newArray;
    }
}
