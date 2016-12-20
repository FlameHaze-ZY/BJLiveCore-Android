package com.baijiahulian.bjhl_liveplayer.utils;

import android.util.Log;

import com.baijiahulian.livecore.context.LPConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by bjhl on 16/6/23.
 */
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();
    public static Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LPConstants.LPClassType.class, new LPClassTypeDeserializer());
        gsonBuilder.registerTypeAdapter(Date.class, new LPDateDeserializer());
        gson = gsonBuilder.create();
    }

    private static class LPDateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsLong() * 1000);
        }

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime() / 1000);
        }
    }


    private static class LPClassTypeDeserializer implements JsonDeserializer<LPConstants.LPClassType>, JsonSerializer<LPConstants.LPClassType> {

        @Override
        public LPConstants.LPClassType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            LPConstants.LPClassType[] classTypes = LPConstants.LPClassType.values();
            for (LPConstants.LPClassType classType : classTypes) {
                if (json.getAsInt() == classType.getType()) {
                    return classType;
                }
            }
            return null;
        }

        @Override
        public JsonElement serialize(LPConstants.LPClassType classType, Type arg1,
                                     JsonSerializationContext arg2) {
            return new JsonPrimitive(classType.getType());
        }
    }

    public static final JsonParser jsonParser = new JsonParser();

    public static <T> T parseString(String result, Class<T> classOfT) {
        if (result != null && classOfT != null) {
            try {
                return gson.fromJson(result, classOfT);
            } catch (JsonSyntaxException var3) {
                Log.e(TAG, "catch exception when format json str:" + result);
                throw var3;
            }
        } else {
            return null;
        }
    }

    public static String toString(Object obj) {
        return obj == null ? "" : gson.toJson(obj);
    }

    public static <T> T parseJsonObject(JsonObject result, Class<T> classOfT) {
        return gson.fromJson(result, classOfT);
    }

    // add ljz
    public static JsonObject toJsonObject(Object obj) {
        return jsonParser.parse(toString(obj)).getAsJsonObject();
    }
}

