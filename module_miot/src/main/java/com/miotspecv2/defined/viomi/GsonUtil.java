package com.miotspecv2.defined.viomi;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by Ljh on 2020/2/3.
 */
public class GsonUtil {

    public static Gson gson = new Gson();

    public static <T> T fromJson(String result, Class<T> clazz){
        try{
            if(gson==null){
                gson = new Gson();
            }
            return gson.fromJson(result, clazz);
        }catch (Exception e){

            return null;
        }
    }

    public static <T> T fromJson(String result, Type type){
        try{
            if(gson==null){
                gson = new Gson();
            }
            return gson.fromJson(result, type);
        }catch (Exception e){

            return null;
        }
    }


    public static String toJson(Object obj){
        if(null == gson){
            gson = new Gson();
        }
        return gson.toJson(obj);
    }

}
