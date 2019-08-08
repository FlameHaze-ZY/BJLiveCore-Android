CHANGELOG 2.0.5
==============
- 修复了同时进入教室，相同user_number会出现互踢不起作用的问题
- 修复了多白板的一些问题
- 兼容iOS广播boolean值0/1的问题

CHANGELOG 2.0.2
==============
- 设置AspectRatio改到了LPVideoView和LPCameraView中了。需要在play/setPreview之前设置。
- 修复了测验的一些回调缺失的问题

CHANGELOG 2.0.1
==============
- [答题器](http://dev.baijiayun.com/wiki/detail/77#h7-20)支持出题功能
- [测验](http://dev.baijiayun.com/wiki/detail/77#h7-18)接口支持新版完整功能
- AVSDK支持拉流设置AspectRatio，LPPlayer接口中
```java
/**
 * 设置拉流的填充模式，注意需要在play之前调用
 * 目前仅支持AVSDK
 *
 * @param aspectRatio Fit  居中展示，可能会有黑边
 *                    Fill 铺满View，可能会裁减掉部分内容
 */
void setAspectRatio(LPConstants.LPAspectRatio aspectRatio);
```

CHANGELOG 2.0.0
==============
1.xSDK[升级方案](http://dev.baijiayun.com/default/wiki/detail/76)
- 引入webrtc底层，对集成方透明
- 支持问答、答题器、点赞等功能

CHANGELOG 1.4.20
==============
- 增加音频输出设置接口
注意：需要在进教室之前调用
```java
LiveSDK.setAudioOutput(LPConstants.VoiceType.VOICE_CALL);    //通话通道输出
LiveSDK.setAudioOutput(LPConstants.VoiceType.VOICE_MEDIA);   //媒体通道输出
```

CHANGELOG 1.4.19
==============
- 修复音视频播放成功状态回调

CHANGELOG 1.4.10
==============
- 增加AVSDK Log
```java
liveRoom.getLivePlayer().getLogFilePath() // 获取日志文件路径
```
- 增加小测结束回调
```java
liveRoom.getSurveyVM().getObservableOfSurveyClose().subscribe(new LPErrorPrintSubscriber<Void>() {
                    @Override
                    public void call(Void aVoid) {                      
                    }
                });
```

CHANGELOG PPT 1.4.5
==============
- 修复动态PPT不能禁用滑动的问题

CHANGELOG PPT 1.4.3
==============
- 修复PPT IndexOutOfBoundsException

CHANGELOG PPT 1.3.6
==============
- 修复新上传动态PPT后不能往后翻页的问题

CHANGELOG core 1.3.5 & PPT 1.3.5
==============
- 升级底层SDK
- 修复PPT画笔清除的问题

CHANGELOG core 1.3.4 & PPT 1.3.4
==============
- 升级底层SDK

CHANGELOG core 1.3.3 & PPT 1.3.3
==============
- IMessageModel新增getTime方法获取消息的服务器时间
- 修复动态PPT切换画笔个别情况没有刷新的问题

CHANGELOG core 1.3.2 & PPT 1.3.2
==============
- 修复动态PPT缩放问题
- 修复断网重连时偶现进度卡在50%的问题

CHANGELOG core 1.3.1
==============
- avsdk升级
- 修复网线接入网络时的问题

CHANGELOG core 1.3.0 & PPT 1.3.0
==============
- 支持私聊
- 支持拉流时远端音视频状态变化不断流（推流端需具备推黑帧、静音流的能力）
- 上麦路数优化
- 修复了一些已知问题

CHANGELOG core 1.2.6
==============
- 支持前端任意多边形绘制

CHANGELOG core ppt-1.2.7 & 1.2.8
==============
- 修复快速翻页问题

CHANGELOG core ppt-1.2.6
==============
- 修复了某些情况下点击事件的问题

CHANGELOG core 1.2.5 & ppt-1.2.5
==============
- 兼容前端颜色问题
- 修复了一些已知问题

CHANGELOG core 1.2.4 & ppt-1.2.4
==============
- 添加ppt预加载逻辑
- ChatVM Observable<Void> getObservableOfNotifyDataChange()改为Observable<List<IMessageModel>> getObservableOfNotifyDataChange()返回消息池中所有数据

CHANGELOG core 1.2.2
==============
- 兼容各端广播信令中的不同类型

CHANGELOG core 1.2.1
==============
- 修复在接听电话后声音变小的问题

CHANGELOG core 1.2.0
==============
- 支持h5画笔任意多边形
- 大小班课切换
- 升级hubble

CHANGELOG core 1.1.0 & ppt-1.1.0
==============
- PPT缩放

CHANGELOG core 1.0.1
==============
- 参加码进房间支持传头像

CHANGELOG core 0.4.0+
==============
- 多视频支持

CHANGELOG core-0.2.1 & ppt-0.1.3
==============
- 修复了在离线回看下PPT不显示的问题

CHANGELOG 0.1.6
==============
## bugfix
- 修复了因美颜开启导致的切换时采集视频异常
## new features
- SpeakQueueVM增加了学生主动取消举手功能
```java
liveRoom.getSpeakQueueVM().cancelSpeakApply();
```

CHANGELOG 0.1.5
==============
## bugfix
- 修复了一个因云端录制状态引起的广播接受异常

CHANGELOG 0.1.4
==============
## bugfix
- 修复断线重连后在线用户列表里出现两个自己
- 修复小测统计某些字段对不上的问题

CHANGELOG 0.1.3
==============
## new features
- Recorder增加开关闪光灯功能
```java
recorder.openFlashLight();
recorder.closeFlashLight();
```

CHANGELOG 0.1.2
==============
## new features
### 增加了在线课堂随堂测验功能
- 获取历史测验
```java
liveRoom.getSurveyVM().requestPreviousSurvey(liveRoom.getCurrentUser().getNumber())
        .subscribe(new LPErrorPrintSubscriber<IPreviousSurveyModel>() {
    @Override
    public void call(IPreviousSurveyModel iPreviousSurveyModel) {
        iPreviousSurveyModel.getPreviousSurvey() //历史测验List
        iPreviousSurveyModel.getRightCount()     //当前用户答对几题
        iPreviousSurveyModel.getWrongCount()     //当前用户打错几题
    }
});
```
- 收到老师发送新的测验
```java
liveRoom.getSurveyVM().getObservableOfSurveyReceive().observeOn(AndroidSchedulers.mainThread()).subscribe(new LPErrorPrintSubscriber<ISurveyReceiveModel>() {
    @Override
    public void call(final ISurveyReceiveModel iSurveyReceiveModel) {
        iSurveyReceiveModel.getSurvey()  //新的测验
    }
});
```
- 学生发送答案。
```java
/**
 * 学生发送答案
 *
 * @param order      题目序号
 * @param userName   学生姓名
 * @param userNumber
 * @param answer     [A, B ,C] 数组元素是 option 下 key
 * @param result     0 正确 1 错误 -1 没有答案（老师没有设置正确答案，所有的option的isAnswer都是false）
 */
liveRoom.getSurveyVM().sendAnswer(int order, String userName, String userNumber, List<String> answer, int result);
```
- 服务器答题统计。服务器会10秒汇总一次，如果有答题状态更新的话就广播下发
```java
/**
 * 收到测验统计结果回调
 *
 * @return
 */
Observable<ISurveyStatisticModel> getObservableOfAnswerStatistic();
```
- 模型接口说明
```java
ISurveyModel {
    int getOrder();                             //题目序号
    String getQuestion();                       //获取题干
    List<ISurveyOptionModel> getOptionList();   //各个选项
}
ISurveyOptionModel{
    String getKey();                            //获得选项标识 A,B,C \ 1,2,3 ...
    String getValue();                          //获得选项值
    boolean isAnswer();                         //是否是正确答案
    int getUserCount();                         //该选项选择人数
}
ISurveyStatisticModel{
    int getOrder();                              //题目序号
    Map<String, Integer> getResult();            //获得统计结果  key 是 option key， value 是选择的人数
}

```

CHANGELOG 0.1.1
==============
## bugfix
修复直播视频采集时因开启美颜引起切后台的黑屏问题

CHANGELOG 0.1.0
==============
## bugfix
修复集成回放sdk时聊天模块可能出现空指针的情况

CHANGELOG 0.0.8 & 0.0.9
==============
## API changes
- 无需在Application初始化了~~LiveSDK.init("partnerId",LPConstants.LPDeployType.Test);~~，默认为Product环境
- Recorder和Player录制或者播放视频时，使用新的TextureView代替了SurfaceView，让您的视频也可以拥有完美的动画效果
视频采集的view
```java
CameraGLTextureView view = new CameraGLTextureView(this);
recorder.setPreview(view);
```
视频播放的view
```java
TextureView textureView = ViERenderer.CreateRenderer(context, true);
player.setVideoView(surfaceView);
```
- 移除一个打印Log的第三方依赖`com.orhanobut:logger`

## new features
- 聊天模块新增绑定Adapter的方法
```java
int getMessageCount();
IMessageModel getMessage(int position);
Observable<Void> getObservableOfNotifyDataChange();
```
- 聊天模块新增channel支持。在IMessageModel中增加channel字段，发送消息时，使用
```java
liveRoom.getChatVM().sendMessage(msg, channel);
```
来指定channel
- PPT模块增加禁止学生主动滑动PPT翻页（仍然会随老师翻页）
```java
lppptFragment.setFlingEnable(false);
```
- 增加了一个接收用户自定义广播事件的接口
```java
liveRoom.getObservableOfBroadcast().observeOn(AndroidSchedulers.mainThread())
.subscribe(new LPErrorPrintSubscriber<LPKVModel>() {
    @Override
    public void call(LPKVModel lpkvModel) {
        String key = lpkvModel.key;
        String value = lpkvModel.value;
    }
});
```

CHANGELOG 0.0.7
==============
## bugfix
- 修复后进教室的学生PPT页码不对的问题


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
