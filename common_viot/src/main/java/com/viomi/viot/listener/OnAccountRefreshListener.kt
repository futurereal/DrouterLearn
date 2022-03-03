package com.viomi.viot.listener

import com.viomi.viot.account.AccountMessage

/**
 * 账号信息刷新监听
 *
 * @author William
 * @date 2021/3/16
 */
interface OnAccountRefreshListener {
    fun onAccountRefresh(accountMessage: AccountMessage?)
}
