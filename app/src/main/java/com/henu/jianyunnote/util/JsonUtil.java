package com.henu.jianyunnote.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class JsonUtil {
    /**
     * 把json字符串变成map
     * @param json
     * @return
     */
    public static HashMap<String, Object> parseJsonToMap(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        HashMap<String, Object> map = null;
        try {
            map = gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
