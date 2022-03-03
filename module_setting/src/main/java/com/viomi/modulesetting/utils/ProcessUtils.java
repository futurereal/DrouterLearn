package com.viomi.modulesetting.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Copyright (C), 2014-2019, 佛山云米科技有限公司
 *
 * @ProjectName: viomi_face
 * @Package: com.viomi.apm.library.utils
 * @ClassName: ProcessUtils
 * @Description: 进程工具类
 * @Author: randysu
 * @CreateDate: 2019-05-17 11:44
 * @UpdateUser:
 * @UpdateDate: 2019-05-17 11:44
 * @UpdateRemark:
 * @Version: 1.0
 */
public class ProcessUtils {

    private static final String SUB_TAG = ProcessUtils.class.getName();

    private static String sProcessName = null;

    public static String getCurrentProcessName() {
        if (TextUtils.isEmpty(sProcessName)) {
            sProcessName = getCurrentProcessNameInternal();
        }
        return sProcessName;
    }

    private static String getCurrentProcessNameInternal() {
        String fn = "/proc/self/cmdline";

        try (FileInputStream in = new FileInputStream(fn)) {
            byte[] buffer = new byte[256];
            int len = 0;
            int b;
            while ((b = in.read()) > 0 && len < buffer.length) {
                buffer[len++] = (byte) b;
            }

            if (len > 0) {
                String s = new String(buffer, 0, len, StandardCharsets.UTF_8);
                return s;
            }
        } catch (IOException e) {
            Log.d(SUB_TAG, "getCurrentProcessName: got exception: " + Log.getStackTraceString(e));
        }

        return null;
    }


}
