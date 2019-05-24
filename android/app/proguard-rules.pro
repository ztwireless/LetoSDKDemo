################################################################################
# 通用部分
################################################################################
 -ignorewarnings


#代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

#混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

#指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

#这句话能够使我们的项目混淆后产生映射文件
#包含有类名->混淆后类名的映射关系
-verbose

#指定不去忽略非公共库的类
-dontskipnonpubliclibraryclassmembers

#不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

#保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

#避免混淆泛型
-keepattributes Signature

#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#保护js调用方法不被混淆
-keepattributes *JavascriptInterface*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# webview
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

#指定混淆是采用的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

#保留我们使用的四大组件，自定义的Application等等这些类不被混淆
#因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**


#保留support下的所有类及其内部类
-keep class android.support.** {*;}

#保留R下面的资源
-keep class **.R$* {*;}

#保留本地native方法不被混淆
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# 对于用Keep标记过的类/方法/成员等做保持
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

#保留在Activity中的方法参数是view的方法，
#这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

#保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#对于带有回调函数的onXXEvent的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
}

################################################################################
# 第三方库
################################################################################

#Glide的混淆
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#glide如果你的API级别<=Android API 27 则需要添加
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder


# 友盟
-dontwarn com.umeng.**
-keep class com.umeng.** {*;}

# xmlpull
-keep class org.xmlpull.v1.** { *;}
-dontwarn org.xmlpull.v1.**

# avi loader indicator
-keep class com.wang.avi.** { *; }

# rxandroid
-dontwarn io.reactivex.**
-keep class io.reactivex.** {*;}

# okhttp
-dontwarn okio.**
-dontwarn com.kymjs.rxvolley.**
-keep class okio.** {*;}
-keep class com.kymjs.rxvolley.** {*;}

# eventbus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# eventbus: Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# mojo
-dontwarn org.codehaus.mojo.**
-keep class org.codehaus.mojo.** { *; }

# 现在支付
-dontwarn com.ipaynow.plugin.**
-keep class com.ipaynow.plugin.** {*;}

# 百度定位
-dontwarn com.baidu.location.**
-keep class com.baidu.location.** {*;}

# apache
-dontwarn org.apache.**
-keep class org.apache.** {*;}
-dontwarn android.net.http.**
-keep class android.net.http.** { *;}
-keep class com.android.volley.** {*;}

################################################################################
# leto.core
################################################################################

# beans
-keep class com.ledong.lib.leto.api.bean.** { *; }
-keep class com.ledong.lib.leto.api.ad.vast.** { *; }
-keep class com.leto.game.minigame.bean.** { *; }
-keep class com.leto.game.base.bean.** { *; }
-keep class com.leto.game.base.ad.bean.** { *; }

# api实现模块中的接口方法需要保持
-keepclassmembers class * extends com.ledong.lib.leto.api.AbsModule {
    public void *(java.lang.String, java.lang.String, com.ledong.lib.leto.interfaces.IApiCallback);
}



#######################################
#头条SDK
-keep class com.bytedance.sdk.openadsdk.** {*;}
-keep class com.androidquery.callback.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}


#广点通SDK
-keep class com.qq.e.** {
  public protected *;
}
-keep class android.support.v4.app.NotificationCompat**{
  public *;
}

#百度SDK
-keep class com.baidu.mobads.*.** { *; }

#vivoSDK
-keep class com.vivo.*.** { *; }

#applovin SDK
-keep public class com.applovin.sdk.AppLovinSdk { *; }
-keep public class com.applovin.sdk.AppLovin* { public protected *; }
-keep public class com.applovin.nativeAds.AppLovin* { public protected *; }
-keep public class com.applovin.adview.* { public protected *; }
-keep public class com.applovin.mediation.* { public protected *; }
-keep public class com.applovin.mediation.ads.* { public protected *; }
-keep public class com.applovin.impl.**.AppLovin* { public protected *; }
-keep public class com.applovin.impl.**.*Impl { public protected *; }
-keepclassmembers class com.applovin.sdk.AppLovinSdkSettings { private java.util.Map localSettings; }
-keep class com.applovin.mediation.adapters.** { *; }
-keep class com.applovin.mediation.adapter.** { *; }

#小米 SDK
-keep class com.xiaomi.ad.**{*;}
-keep class com.miui.zeus.**{*;}


#oppo sdk
-keep class com.oppo.** {
public protected *;
}
-keep class okio.**{ *; }
-keep class com.squareup.wire.**{ *; }
-keep public class * extends com.squareup.wire.**{ *; }
# Keep methods with Wire annotations (e.g. @ProtoField)
-keepclassmembers class ** {
 @com.squareup.wire.ProtoField public *;
 @com.squareup.wire.ProtoEnum public *;
}
-keep public class com.cdo.oaps.base.**{ *; }
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*
#support-v4
-keep class android.support.v4.** { *; }

-dontwarn com.androidquery.auth.**
#######################################

#android-gif
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int, java.lang.String);}



#inmobe
-keepattributes SourceFile,LineNumberTable
-keep class com.inmobi.** { *; }
-dontwarn com.inmobi.**
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{public *;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{public *;}

#skip the Picasso library classes
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**

#skip Moat classes
-keep class com.moat.** {*;}
-dontwarn com.moat.**

#skip AVID classes
-keep class com.integralads.avid.library.** {*;}