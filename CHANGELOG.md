CHANGELOG 0.0.6
==============
## bugfix
- 修复老师角色全体禁言、云端录制不可用的问题

## API changes
- PPT模块不再提供上传图片的UI，~~`lppptFragment.choosePhoto();`~~，可以通过调用如下API实现在手机端上传图片至PPT。
```java
liveRoom.getDocListVM().uploadImageToPPT(String PicFilePath).subscribe(new Action1<Boolean>() {
    @Override
    public void call(Boolean isSuccess) {
    }
});
```

## new features
- 单个用户禁言，仅限**老师**角色
```java
/**
* 禁言(teacher only)
*
* @param forbidUser 禁言用户
* @param duration   禁言时长
*/
liveRoom.forbidChat(IUserModel forbidUser, long duration);
```
- 禁言回调(包含其他人被禁言)
```java
liveRoom.getObservableOfForbidChat().subscribe(new Action1<IForbidChatModel>() {
    @Override
    public void call(IForbidChatModel iForbidChatModel) {
    }
});
```
- 当前用户是否被禁言
```java
liveRoom.getObservableOfIsSelfChatForbid().subscribe(new Action1<Boolean>() {
    @Override
    public void call(Boolean isChatForbid) {
    }
})
```

CHANGELOG 0.0.5
==============
## bugfix
- 修复了在线用户人数不对的问题
- 修复了在PPT在滑动过程中退出教室导致的崩溃问题

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
