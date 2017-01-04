
CHANGELOG 0.0.4
==============

## bugfix
- 修复了某些情况下断线重连不成功的情况
- 修复了网络切换时的重连问题
- 修复了发言中切换链路buffer问题
- 修复了TCP/UDP切换导致的ANR
- 修复了一个因学生上传课件导致的崩溃问题
- 修复了被踢下线导致的崩溃问题

## API changes
- 老师处理发言结果回调改变了
```java
liveRoom.getSpeakQueueVM().getObservableOfSpeakResponse().subscribeOn(AndroidSchedulers.mainThread())
.subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
    @Override
    public void call(IMediaControlModel iMediaControlModel) {
        if (iMediaControlModel.isApplyAgreed()) {
            //老师同意了你的发言
        } else {
            //老师拒绝了你的发言
        }
    }
});
```

## new features
- 增加了下行链路切换回调
```java
player.getObservableOfLinkType().subscribe(new LPErrorPrintSubscriber<LPConstants.LPLinkType>() {
    @Override
    public void call(LPConstants.LPLinkType lpLinkType) {
    }
});
```
- 增加了学生请求发言接口
```java
liveRoom.getSpeakQueueVM().requestSpeakApply();
```
- 增加了发言被远程关闭的接口
```java
liveRoom.getSpeakQueueVM().getObservableOfMediaControl().observeOn(AndroidSchedulers.mainThread())
        .subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
            @Override
            public void call(IMediaControlModel iMediaControlModel) {
                if (!iMediaControlModel.isApplyAgreed()) {
                    // 老师关闭发言
                }
            }
        });
```
- demo中增加了链路切换功能
- demo中增加了PPT全屏/铺满切换功能
- demo中增加了上行美颜开关、高清\普清切换开关、摄像头切换开关
- demo中增加了举手功能,如果是老师则自动同意学生举手
