## 使用前提  设置页依赖外部 rootProject.ext.
# 示例如下
ext {  
    android = [  
            compileSdkVersion: 28,
            buildToolsVersion: "28.0.0",
            applicationId    : "com.viomi.device",
            minSdkVersion    : 22,  
            targetSdkVersion : 28,
            versionCode      : 100,
            versionName      : "1.3.1"
            ]

    versions = [
            "support-version": "28.0.0",
            "junit-version"  : "4.12"
    ]

    support = [
            'multidex'                : "androidx.multidex:multidex:2.0.0",
            'androidx-recyclerview'   : "androidx.recyclerview:recyclerview:1.0.0",
            'androidx-appcompat'      : "androidx.appcompat:appcompat:1.3.1",
            'constraint-layout'       : "androidx.constraintlayout:constraintlayout:1.1.3",
            'support-v4'              : "com.android.support:support-v4:${versions["support-version"]}",
            'appcompat-v7'            : "com.android.support:appcompat-v7:${versions["support-version"]}",
            'recyclerview-v7'         : "com.android.support:recyclerview-v7:${versions["support-version"]}",
            'support-v13'             : "com.android.support:support-v13:${versions["support-version"]}",
            'support-fragment'        : "com.android.support:support-fragment:${versions["support-version"]}",
            'design'                  : "com.android.support:design:${versions["support-version"]}",
            'animated-vector-drawable': "com.android.support:animated-vector-drawable:${versions["support-version"]}",
            'junit'                   : "junit:junit:${versions["junit-version"]}",
            'androidx-junit'          : "androidx.test.ext:junit:1.3.1",
            'androidx-espresso'       : "androidx.test.espresso:espresso-core:3.2.0"
    ]

    dependencies = [
            //rxjava
            "rxjava"                      : "io.reactivex.rxjava2:rxjava:2.2.3",
            "rxandroid"                   : "io.reactivex.rxjava2:rxandroid:2.1.0",
            //rx系列与View生命周期同步
            "rxlifecycle"                 : "com.trello.rxlifecycle2:rxlifecycle:2.2.2",
            "rxlifecycle-components"      : "com.trello.rxlifecycle2:rxlifecycle-components:2.2.2",
            //rxbinding
            "rxbinding"                   : "com.jakewharton.rxbinding2:rxbinding:2.1.1",
            //rx 6.0权限请求
//            "rxpermissions"                        : "com.github.tbruyelle:rxpermissions:0.10.2",
            //rxrelay 订阅过程中发生异常不会取消订阅关系
            "rxrelay"                     : "com.jakewharton.rxrelay2:rxrelay:2.0.0",

            //network
            "okhttp"                      : "com.squareup.okhttp3:okhttp:3.10.0",
            "retrofit"                    : "com.squareup.retrofit2:retrofit:2.6.0",
            "converter-gson"              : "com.squareup.retrofit2:converter-gson:2.4.0",
            "retrofit2-fastjson-converter": "com.github.BaronZ88:Retrofit2-FastJson-Converter:1.2",
            "adapter-rxjava"              : "com.squareup.retrofit2:adapter-rxjava2:2.4.0",
            "logging-interceptor"         : "com.squareup.okhttp3:logging-interceptor:3.9.1",

            //glide图片加载
            "glide"                       : "com.github.bumptech.glide:glide:4.8.0",
            "glide-compiler"              : "com.github.bumptech.glide:compiler:4.8.0",

            //json解析
            "gson"                        : "com.google.code.gson:gson:2.8.5",
            "fastjson"                    : "com.alibaba:fastjson:1.2.31",

            //Google AAC
            "lifecycle-extensions"        : "android.arch.lifecycle:extensions:1.1.1",
            "lifecycle-compiler"          : "android.arch.lifecycle:compiler:1.1.1",

            //dagger
            "dagger"                      : "com.google.dagger:dagger:2.13",
            "dagger-compiler"             : "com.google.dagger:dagger-compiler:2.13",

            //facebook
            "stetho"                      : "com.facebook.stetho:stetho:1.3.1",
            "fresco"                      : "com.facebook.fresco:fresco:2.0.0",

            // room
            "room"                        : "androidx.room:room-runtime:2.0.0-rc01",
            "room-annotation"             : "androidx.room:room-compiler:2.0.0-rc01",

            // permissions
            "easy-permissions"            : "pub.devrel:easypermissions:3.0.0",

            // db 调试工具
            "debug-db"                    : "com.amitshekhar.android:debug-db:1.0.4",

            //viomi 组件
            "viomi-apm"                   : "com.viomi.apm:viomi-apm-lib:1.0.2.1",
            "viomi-router-core"           : "com.viomi.router:viomi-router-lib:1.0.0.14",
            "viomi-router-compiler"       : "com.viomi.router.compiler:viomi-router-compiler-lib:1.0.0.12",
            // viomi module framework 功能组件
            "viomi-module-common"         : "com.viomi.module:viomi_common:1.0.1.1",
            "viomi-module-api"            : "com.viomi.module:viomi_api:1.0.0.0",
            "viomi-module-db"             : "com.viomi.module:viomi_db:1.0.0.0",
            //"tools_compiler_annotation"            :"com.viomi.tools_module_annotation:viomi-tools-module-annotation-lib:1.0.0.1",
            //"tools_compiler"                       :"com.viomi.module_compiler:viomi-module-compiler-lib:1.0.0.1",

            // blurdialog
            "blurdialogfragment"          : "fr.tvbarthel.blurdialogfragment:lib:2.2.0",

            // 小米IOT
            "conscrypt-android"           : "org.conscrypt:conscrypt-android:1.1.4",
            "viomi-devicelib"             : "com.viomi.devicelib:viomi-device-lib:2.3.1",

            // Tencent Tinker
            "crashreport-upgrade"         : "com.tencent.bugly:crashreport_upgrade:1.3.6",
            "tinker-android-lib"          : "com.tencent.tinker:tinker-android-lib:1.9.9"
    ]
}



