# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5         # 指定代码的压缩级别,默认为5
-dontusemixedcaseclassnames   # 是否使用大小写混合，混淆后都是小写字母的组合
-dontpreverify           # 混淆时是否做预校验，不作预校验，加快混淆速度
-verbose               # 混淆时是否记录日志

# 去除编译时警告
-ignorewarnings

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/* #混淆时所采用的算法,参数是google官方推荐的过滤器算法


# 如果项目用到注解，需要保留注解属性
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

# 不混淆泛型，内部类，闭包，异常抛出
-keepattributes Signature, InnerClasses, EnclosingMethod, Exceptions

# 保留代码行号，这在混淆后代码运行中抛出异常信息时，有利于定位出问题的代码
-keepattributes SourceFile,LineNumberTable

#不混淆需要根据manifest来识别的类
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# 保留继承的
-keep public class * extends android.support.v4.**

-keep public class * extends android.support.v7.**

-keep public class * extends android.support.annotation.**

# 保留R下面的资源
-keep class **.R$* {
    *;
}
# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
*;
}

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持Activity中参数是View类型的函数，保证在Layout XML中配置的onClick属性值能够正常调用到
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
void *(**On*Event);
void *(**On*Listener);
}

-keepclassmembers class * extends android.webkit.webViewClient {
public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
public boolean *(android.webkit.WebView, java.lang.String);
}

-keepclassmembers class * extends android.webkit.webViewClient {
public void *(android.webkit.webView, jav.lang.String);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
!static !transient <fields>;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
*** get*();
void set*(***);
}
# 有引用android-support-v4.jar包
-keep public class com.xxxx.app.ui.fragment.** {*;}

# 保留JS方法不被混淆
#-keepclassmembers class com.example.xxx.MainActivity$JSInterface1 {
#    <methods>;
#}
#################################################################################################################
# 不混淆某个类
-keep class com.viomi.iotdevice.ViomiIotManager { *; }
-keep class com.viomi.iotdevice.IotSerialConfig { *; }
-keep class com.viomi.iotdevice.common.protocol.EventPack { *; }

# 保留实体类和成员不被混淆
-keep public class com.xxxx.entity.** {
    public void set*(***);
    public *** get*();
    public *** is*();
}

# 保留内嵌类不被混淆
-keep class com.viomi.iotdevice.IotSerialConfig$* { *; }

# 不混淆某个包所有的类
-keep class com.viomi.iotdevice.common.callback.** { *; }
-keep class com.viomi.iotdevice.common.util.** { *; }
-keep class com.viomi.iotdevice.common.exception.** { *; }
-keep class com.viomi.iotdevice.iottomcu.bean.** { *; }
