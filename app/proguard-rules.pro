# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/yanglei/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-keep public class com.squareup.**{*;}
-dontwarn com.squareup.**

#-keep class okio.*{ *; }
-dontwarn okio.**

#百家云混淆规则
-keep public class com.baijiahulian.**{*;}
-keep public class com.bjhl.**{*;}
-dontwarn com.baijiahulian.**
-dontwarn com.bjhl.**

#ReactiveNetwork
-keep public class com.github.pwittchen.reactivenetwork.**{*;}
-dontwarn com.github.pwittchen.reactivenetwork.library.ReactiveNetwork
-dontwarn io.reactivex.functions.Function

#RxJava混淆规则
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}