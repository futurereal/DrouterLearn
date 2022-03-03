package com.viomi.viot.listener

import com.viomi.viot.push.PushMessage

/**
 * 消息推送回调接口
 *
 * @author William
 * @date 2020/12/22
 */
interface OnMessageArrivedListener {
    fun onMessageArrived(pushMessage: PushMessage?)
}
