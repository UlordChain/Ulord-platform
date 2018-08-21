/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import java.util.List;
import org.springframework.util.StringUtils;

/**
 * google.gson帮助类
 *
 * @author chenxin
 * @since 2018-08-10
 */
public class JsonUtil {

    private static Gson gson;

    public JsonUtil() {
    }

    public static final String toJson(Object pObject) {
        String jsonString = "";
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        jsonString = gson.toJson(pObject);
        return jsonString;
    }

    public static final String toJson(Object pObject, String pDateFormat) {
        String jsonString = "";
        if (StringUtils.isEmpty(pDateFormat)) {
            pDateFormat = "yyyy-MM-dd HH:mm:ss";
        }

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.setDateFormat(pDateFormat);
        Gson gson = builder.create();
        jsonString = gson.toJson(pObject);
        return jsonString;
    }

    public static final <T> T fromJson(String json, Type type) {
        T list = gson.fromJson(json, type);
        return list;
    }

    public static final List<Object> fromJson(String json) {
        List<Object> list = (List)fromJson(json, (new TypeToken<List<Object>>() {
        }).getType());
        return list;
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }
}