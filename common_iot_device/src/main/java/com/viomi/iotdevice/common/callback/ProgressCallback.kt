package com.viomi.iotdevice.common.callback

/**
 * <pre>
 *     author : William
 *     e-mail : liangzhiwei@viomi.com.cn
 *     time   : 2019/2/21
 *     desc   : 进度回调
 *     version: 1.0
 * </pre>
 */
interface ProgressCallback {

    /**
     * 回调结果
     *
     * @param isProcessing  是否处理中, 为 false 时看进度判断未开始和完成状态
     * @param progress      进度百分比, 负数代表失败
     * @param desc          进度描述
     */
    fun onResult(isProcessing: Boolean, progress: Int, desc: String?)
}
