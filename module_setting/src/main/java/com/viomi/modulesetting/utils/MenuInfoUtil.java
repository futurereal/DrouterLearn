package com.viomi.modulesetting.utils;

import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.viomi.common.ApplicationUtils;
import com.viomi.modulesetting.R;
import com.viomi.modulesetting.entity.SettingMenuEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuInfoUtil {
    private static final String TAG = "MenuInfoUtil";
    static Map<String, SettingMenuEntity> menuMap;

    public static List<SettingMenuEntity> getMenuList(int menuArrayId) {
        if (menuArrayId == 0) {
            return null;
        }
        String[] menuWaterKeys = ApplicationUtils.getContext().getResources().getStringArray(menuArrayId);
        menuMap = parseAllMenuInfo();
        List<SettingMenuEntity> menuEntities = new ArrayList<>(menuWaterKeys.length);
        for (String menuKey : menuWaterKeys) {
            SettingMenuEntity menuEntity = menuMap.get(menuKey);
            if (menuEntity == null) {
                throw new NullPointerException("menu set error");
            }
            menuEntities.add(menuEntity);
        }
        Log.i(TAG, "getWaterMenuEntity: " + menuEntities.size());
        return menuEntities;
    }

    public static SettingMenuEntity getMenuEntityByKey(String menuKey) {
        Log.i(TAG, "getMenuEntityByKey: menuKey : " + menuKey);
        menuMap = parseAllMenuInfo();
        SettingMenuEntity menuEntity = menuMap.get(menuKey);
        Log.i(TAG, "getMenuEntityByKey: " + menuEntity.toString());
        return menuEntity;
    }

    /**
     * 解析菜单，解析一次放在内存里面
     *
     * @return
     */
    private static Map<String, SettingMenuEntity> parseAllMenuInfo() {
        if (menuMap != null) {
            return menuMap;
        }
        menuMap = new HashMap<>();
        XmlResourceParser menuParser = ApplicationUtils.getContext().getResources().getXml(R.xml.menus_info);
        AttributeSet menuAttr = Xml.asAttributeSet(menuParser);
        final int depth = menuParser.getDepth();
        Log.i(TAG, "parseAllMenuInfo: depth: " + depth);
        int type = 0;
        while (true) {
            try {
                if (!(((type = menuParser.next()) != XmlPullParser.END_TAG || menuParser.getDepth() > depth)
                        && type != XmlPullParser.END_DOCUMENT)) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            if (type != XmlPullParser.START_TAG) {
                continue;
            }
            // 标签的名字
            final String name = menuParser.getName();
            Log.i(TAG, "parseAllMenuInfo: name : " + name);
            // attrs 是从 xmlParse 获取的。
            final TypedArray typedArray = ApplicationUtils.getContext().obtainStyledAttributes(menuAttr, R.styleable.MenuView);
            final String menuIcon = typedArray.getString(R.styleable.MenuView_menuIcon);
            final String menuKey = typedArray.getString(R.styleable.MenuView_menuKey);
            final String menuName = typedArray.getString(R.styleable.MenuView_menuName);
            final String menuRoutPath = typedArray.getString(R.styleable.MenuView_menuRoutePath);
            final String menuStatus = typedArray.getString(R.styleable.MenuView_menuStatus);
            SettingMenuEntity settingMenuEntity = new SettingMenuEntity(menuName, menuIcon, menuStatus, menuRoutPath);
            menuMap.put(menuKey, settingMenuEntity);
        }
        return menuMap;
    }
}
