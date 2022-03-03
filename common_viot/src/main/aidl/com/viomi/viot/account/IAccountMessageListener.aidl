// IAccountMessageListener.aidl
package com.viomi.viot.account;

import com.viomi.viot.account.AccountMessage;
// Declare any non-default types here with import statements

interface IAccountMessageListener {
    void onAccountRefresh(inout AccountMessage accountMessage);
}
