package com.viomi.ovenso.helper;

import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import com.viomi.common.ApplicationUtils;
import com.viomi.ovenso.bean.ModeTypeEntity;
import com.viomi.ovenso.custommode.CustomeModeUtils;
import com.viomi.ovenso.microwave.R;
import com.viomi.ovensocommon.db.CookParamEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModesHelper {
    private static final String TAG = "ModesHelper";
    static Map<Integer, ModeTypeEntity> modeTypeMap;
    static Map<Integer, CookParamEntity> cookParamMap;

    /**
     * 获取烹饪或者辅助模式的列表
     *
     * @param modeIdsKey
     * @return
     */
    public static List<ModeTypeEntity> getModeTypeList(int modeIdsKey) {
        modeTypeMap = parseAllModeType();
        int[] modeIdKeys = ApplicationUtils.getContext().getResources().getIntArray(modeIdsKey);
        List<ModeTypeEntity> modeTypeEntities = new ArrayList<>(modeIdKeys.length);
        for (int modeId : modeIdKeys) {
            ModeTypeEntity menuEntity = modeTypeMap.get(modeId);
            if (menuEntity == null) {
                throw new NullPointerException("menu set error");
            }
            modeTypeEntities.add(menuEntity);
        }
        Log.i(TAG, "getWaterMenuEntity: " + modeTypeEntities.size());
        return modeTypeEntities;
    }

    public static ModeTypeEntity getModeEntityById(int modeId) {
        ModeTypeEntity modeTypeEntity = null;
        if (modeId < 100) {
            modeTypeMap = parseAllModeType();
            modeTypeEntity = modeTypeMap.get(modeId);
            Log.i(TAG, "getModeEntityById: " + modeTypeEntity);
        } else {
            CustomeModeUtils.getCustomeRecipe(CustomeModeUtils.CUSTOM_NAME_MODE, modeId);
        }

        return modeTypeEntity;
    }

    // 解析所有应用
    private static Map<Integer, ModeTypeEntity> parseAllModeType() {
        if (modeTypeMap != null) {
            return modeTypeMap;
        }
        modeTypeMap = new HashMap<>();
        cookParamMap = new HashMap<>();
        XmlResourceParser menuParser = ApplicationUtils.getContext().getResources().getXml(R.xml.modes_info);
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
            // attrs 是从 xmlParse 获取的。
            final TypedArray typedArray = ApplicationUtils.getContext().obtainStyledAttributes(menuAttr, R.styleable.modeType);
            final String iconBig = typedArray.getString(R.styleable.modeType_iconBig);
            final String iconBigCombine = typedArray.getString(R.styleable.modeType_iconBigCombine);
            Log.i(TAG, "parseAllMenuInfo: name : " + name + "  iconBigCombine: " + iconBigCombine);
            final String iconSmall = typedArray.getString(R.styleable.modeType_iconSmall);
            final int modeId = typedArray.getInteger(R.styleable.modeType_modeId, 0);
            final String modeType = typedArray.getString(R.styleable.modeType_cookType);
            final String modeName = typedArray.getString(R.styleable.modeType_modeName);
            ModeTypeEntity modeTypeEntity = new ModeTypeEntity();
            modeTypeEntity.setModeType(modeType);
            modeTypeEntity.setResIdBg(iconBig);
            modeTypeEntity.setResIdBgCombine(iconBigCombine);
            modeTypeEntity.setResIdTip(iconSmall);
            modeTypeEntity.setName(modeName);
            modeTypeEntity.setModeId(modeId);
            CharSequence[] modeIdList = typedArray.getTextArray(R.styleable.modeType_childModeIdList);
            Log.i(TAG, "parseAllMenuInfo: modeIdlist: " + modeIdList);
            ArrayList<CookParamEntity> cookParamEntityList = new ArrayList<>();
            if (modeIdList == null) {
                final int firepowerDefine = typedArray.getInteger(R.styleable.modeType_firepowerDefine, 0);
                final int firepowerRangeMax = typedArray.getInteger(R.styleable.modeType_firepowerRangeMax, 0);
                final int firepowerRangeMin = typedArray.getInteger(R.styleable.modeType_firepowerRangeMin, 0);
                final float timeDefine = typedArray.getFloat(R.styleable.modeType_timeDefine, 0);
                final float timeRangeMax = typedArray.getFloat(R.styleable.modeType_timeRangeMax, 0);
                final float timeRangeMin = typedArray.getFloat(R.styleable.modeType_timeRangeMin, 0);
                final int firepowerDefineTwo = typedArray.getInteger(R.styleable.modeType_firepowerDefineTwo, 0);
                CookParamEntity.Builder builder = new CookParamEntity().new Builder(modeId, modeType, modeName, timeDefine, timeRangeMin, timeRangeMax, firepowerDefine, firepowerRangeMin, firepowerRangeMax);
                builder.setdefineFirepowerTwo(firepowerDefineTwo);
                CookParamEntity cookParamEntity = builder.build();
                cookParamEntityList.add(cookParamEntity);
                cookParamMap.put(modeId, cookParamEntity);
            } else {
                Log.i(TAG, "parseAllMenuInfo:modeIds  " + modeIdList.length);
                Log.i(TAG, "parseAllMenuInfo:modeName  " + modeName);
                for (CharSequence modeIdStr : modeIdList) {
                    Log.i(TAG, "parseAllMenuInfo:childModeId:  " + modeIdStr);
                    if (TextUtils.isEmpty(modeIdStr)) {
                        continue;
                    }
                    int childModeId = Integer.parseInt(modeIdStr.toString());
                    CookParamEntity cookParamEntity = cookParamMap.get(childModeId);
                    Log.i(TAG, "parseAllModeType: childModeId: " + childModeId + " cookParamEntity: " + cookParamEntity);
                    if (cookParamEntity != null) {
                        cookParamEntityList.add(cookParamEntity);
                    }
                }
            }
            Log.i(TAG, "parseAllMenuInfo: cookparamListSize: " + cookParamEntityList.size());
            modeTypeEntity.setCookParamEntityList(cookParamEntityList);
            modeTypeMap.put(modeId, modeTypeEntity);
        }
        Log.i(TAG, "parseAllModeType: " + modeTypeMap.size());
        return modeTypeMap;
    }

    public static boolean isCookMode(int modeId) {
        int modeIdArray = R.array.modeid_cook;
        int[] cookModeIds = ApplicationUtils.getContext().getResources().getIntArray(modeIdArray);
        for (int cookModeId : cookModeIds) {
            if (modeId == cookModeId) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAssistMode(int modeId) {
        int assistModeIdArray = R.array.modeid_assistant;
        int[] assistModeIds = ApplicationUtils.getContext().getResources().getIntArray(assistModeIdArray);
        for (int assistId : assistModeIds) {
            if (modeId == assistId) {
                return true;
            }
        }
        return false;
    }
}
