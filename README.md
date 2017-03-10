
liveplayer-sdk-core
===============

## 功能简介
- 教室：直播间；
- 角色：老师、学生、助教；
- 老师：主讲人，拥有最高权限；
- 助教：管理员，拥有部分老师的权限，**移动端不支持助教登录**；
- 学生：听讲人，权限受限，不支持 设置上下课、发公告、处理他人举手、远程开关他人音视频、开关录课、开关聊天禁言；
- 上课、下课：上课中才能举手、发言、录课；
- 举手：学生申请发言，老师和管理员可以允许或拒绝；
- 发言：发布音频、视频，SDK 层面发言不要求举手状态；
- 录课：云端录制课程；
- 聊天/弹幕：目前只支持群发；
- 白板、课件、画笔：课件第一页是白板，后面是老师上传的课件，白板和每一页课件都支持画笔；
- 由于[SDK Core](https://github.com/baijia/LivePlayerDemo_Android)接口中使用了[RxJava](https://github.com/ReactiveX/RxJava)，建议在集成此SDK前，对响应式编程有一定了解。

## 集成SDK

* 添加maven仓库
```groovy
maven { url 'https://raw.github.com/baijia/maven/master/' }
```
* 在build.gradle中添加依赖
```groovy
dependencies {
	compile 'com.baijia.live:liveplayer-sdk-core:0.1.5'
}
```
如果使用到了PPT、白板、涂鸦等功能可以自行实现PPTVM、ShapeVM、DocListVM中相关接口（TODO），也可以使用我们为您提供的PPTFragment，需添加如下依赖
```groovy
	compile 'com.baijia.live:liveplayer-sdk-core-ppt:0.0.9'
```

## API说明
* 进入直播间

LiveSDK.enterRoom目前提供两种方式进入房间
```java
/**
 * @param context
 * @param roomId     房间号
 * @param userNumber 用户 ID
 * @param userName   用户名
 * @param userType   用户类型 {@link com.baijiahulian.livecore.context.LPConstants.LPUserType}
 * @param userAvatar 用户头像
 * @param sign       请求接口参数签名, 签名由 (roomId, userNumber, userName, userType, userAvatar) 5 个参数生成
 * @param listener   进房间回调
 */
public static void enterRoom(Context context, final long roomId, String userNumber, String userName, LPConstants.LPUserType userType, String userAvatar, String sign, final LPLaunchListener listener)

/**
 * @param context
 * @param joinCode 参加吗
 * @param userName 昵称
 * @param listener 进房间回调
 */
public static void enterRoom(Context context, String joinCode, String userName, final LPLaunchListener listener)

```
进入房间回调说明
```java
LPLaunchListener {
    @Override
    public void onLaunchSteps(int step, int totalStep) {
        //进直播间初始化任务队列回调。因为涉及信令与聊天服务器的连接，进直播间时间可能会比较长，可以根据step/totoalStep实现友好的loading效果
    }

    @Override
    public void onLaunchError(LPError error) {
        //进直播间错误回调
    }

    @Override
    public void onLaunchSuccess(LiveRoom liveRoom) {
        //进入直播间成功，返回LiveRoom对象
    }
};
```
* 离开直播间
  一般在**Activity**的`onDestroy()`中调用
```java
liveRoom.quitRoom();
```
* 上课/下课
```java
liveRoom.requestClassStart();
liveRoom.requestClassEnd();
liveRoom.getObservableOfClassStart().subscribe(subscriber);
liveRoom.getObservableOfClassEnd().subscribe(subscriber);
```
一般地，requestClassStart和requestClassEnd均由**老师**角色调用
* 获取当前用户
```java
IUserModel currentUser = liveRoom.getCurrentUser();
```
* 获取老师用户
```java
IUserModel currentUser = liveRoom.getTeacherUser();
```
* 发布音视频（LPRecorder）
  发布音视频主要使用到了LPRecorder。基本方法如下
```java
LPRecorder recorder = liveRoom.getRecorder();
recorder.publish();           // 发布流
recorder.attachAudio();			// 打开音频
recorder.attachVideo();			// 打开视频
recorder.detachAudio();			// 关闭音频
recorder.detachVideo();			// 关闭视频
recorder.stopPublishing();    // 关闭流
```
例如:发布本地音频时，先调用`recorder.publish();`然后再调用`recorder.attachAudio();`即可。
**注意：**发布视频时需要先设置本地视频采集的preview，然后再调用`recorder.attachVideo();`
```java
CameraGLTextureView view = new CameraGLTextureView(this);
recorder.setPreview(view);
```
除此之外，LPRecorder还提供如下一些方法满足某些高级使用场景
```java
boolean isVideoAttached();    
boolean isAudioAttached();    
Observable<Boolean> getObservableOfCameraOn();  //摄像头是否开启回调
void switchCamera();           //切换摄像头(如果有)
int getCameraCount();          //获得系统摄像头数量
LPConstants.LPLinkType getLinkType();  //获得上行链路类型（TCP/UDP)
boolean setLinkType(LPConstants.LPLinkType linkType);  //设置上行链路类型
Observable<LPConstants.LPLinkType> getObservableOfLinkType(); //链路类型改变回调
boolean isPublishing();        //流是否正在上传
void setCaptureVideoDefinition(LPConstants.LPResolutionType definition);//设置分辨率
void openBeautyFilter();       //开启美颜模式
void closeBeautyFilter();      //关闭美颜模式
int getPublishIndex();         //上行服务器index
LPIpAddress getUpLinkServer(); //获得上行服务器地址
```
* 播放音视频（LPPlayer）
  如果有用户（老师/学生）正在发布音视频，这个用户会出现在发言列表SpeakQueueVM的ActiveUser中。当获得这个用户对象的userId就能播放其音视频流
```java
LPPlayer player = liveRoom.getPlayer();
player.playAudio(userId);   //播放音频
player.playVideo(userId);   //播放音视频
player.playAVClose(userId); //关闭音视频流
```
**注意：**在调用`player.playVideo(userId);`前，需要设置显示视频的SurfaceView
```java
TextureView textureView = ViERenderer.CreateRenderer(context, true);
player.setVideoView(surfaceView);
```
此外，LPPlayer还提供如下一些方法满足某些高级使用场景
```java
LPConstants.LPLinkType getLinkType();                  //获得下行链路类型
void setLinkType(LPConstants.LPLinkType linkType);     //设置下行链路类型
Observable<LPConstants.LPLinkType> getObservableOfLinkType(); //链路类型改变回调
int getCurrentUdpDownLinkIndex();                      //UDP下行服务器Index
void setCurrentUdpDownLinkIndex(int index);
void addPlayerListener(LPPlayerListener listener);     //增加播放音视频回调
void removePlayerListener(LPPlayerListener listener);  //移除播放音视频回调
```
* 发言队列（SpeakQueueVM）

请求发言队列
```java
liveRoom.getSpeakQueueVM().requestActiveUsers();
```
请求队列回调
```java
ConnectableObservable<List<IMediaModel>> obs = liveRoom.getSpeakQueueVM().getObservableOfActiveUsers();
Subscriber<List<IMediaModel>> subs = new LPErrorPrintSubscriber<List<IMediaModel>>() {
    @Override
    public void call(List<IMediaModel> iMediaModels) {
    }
};
obs.subscribe(subs);
obs.connect();
```
学生请求发言接口
```java
liveRoom.getSpeakQueueVM().requestSpeakApply();
```
新的用户发言，所有用户都能收到
```java
liveRoom.getSpeakQueueVM().getObservableOfMediaNew().observeOn(AndroidSchedulers.mainThread())
.subscribe(new Action1<IMediaModel>() {
    @Override
    public void call(IMediaModel iMediaModel) {
    }
});
```
发言状态改变（开关音视频、链路切换等），所有用户都能收到
```java
liveRoom.getSpeakQueueVM().getObservableOfMediaChange().observeOn(AndroidSchedulers.mainThread())
.subscribe(new Action1<IMediaModel>() {
    @Override
    public void call(IMediaModel iMediaModel) {
    }
});
```
关闭他人发言，仅**老师**角色可用
```java
liveRoom.getSpeakQueueVM().closeOtherSpeak(userId);
```
关闭发言,被关闭者收到
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
发言关闭，所有用户都能收到
```java
liveRoom.getSpeakQueueVM().getObservableOfMediaClose().observeOn(AndroidSchedulers.mainThread())
.subscribe(new Action1<IMediaModel>() {
    @Override
    public void call(IMediaModel iMediaModel) {
    }
});
```
* 课件模块
  如果您使用了我们提供的PPTFragment，如下初始化即可
```java
LPPPTFragment lppptFragment = new LPPPTFragment();
lppptFragment.setLiveRoom(liveRoom);
```
如果您想禁止学生主动滑动PPT翻页（仍然会随老师翻页），可以在学生端调用
```java
lppptFragment.setFlingEnable(false);
```
切换PPT在容器中显示全屏\铺满
```java
lppptFragment.setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_FULL_SCREEN);
lppptFragment.setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_COVERED);
```

* 聊天（ChatVM）

发送消息
```java
liveRoom.getChatVM().sendMessage(msg);
```
```java
liveRoom.getChatVM().sendMessage(msg, channel);
```
接收消息
```java
liveRoom.getChatVM().getObservableOfReceiveMessage().subscribe(new Action1<IMessageModel>() {
    @Override
    public void call(IMessageModel iMessageModel) {
        String channel = iMessageModel.getChannel();
    	String msg = iMessageModel.getFrom().getName() + ":" + iMessageModel.getContent();
    }
});
```
或者也可以使用
```java
int getMessageCount();
IMessageModel getMessage(int position);
```
来绑定您列表的adapter，并在`liveRoom.getChatVM().getObservableOfNotifyDataChange().subscribe(subscriber);`更新列表即可

* 在线用户（OnlineUserVM）

在线用户模块可以通过liveRoom.getOnlineUserVM获得，其提供了
```java
int getUserCount();
IUserModel getUser(int position);
```
两个方法，可以方便高效的绑定UI的Adapter。由于服务器压力，房间人数大于100人时，不再广播用户进入和退出，所以提供了一个加载更多用户的接口。（每次加载30个）
```java
liveRoom.getOnlineUserVM().loadMoreUser();
```
此外，如果不直接绑定adapter，还可以直接监听整个列表变化，用户进入、退出和loadMoreUser()都会触发此回调
```java
liveRoom.getOnlineUserVM().getObservableOfOnlineUser().subscribe(new LPErrorPrintSubscriber<List<IUserModel>>() {
    @Override
    public void call(List<IUserModel> iUserModels) {
    }
});
```
* 用户进入房间（房间人数小于100人时）
```java
liveRoom.getObservableOfUserIn().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<IUserInModel>() {
    @Override
    public void call(IUserInModel iUserInModel) {        
    }
});
```
* 用户退出房间（房间人数小于100人时）
```java
liveRoom.getObservableOfUserOut().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
    @Override
    public void call(String userId) {        
    }
});
```
* 房间人数变化（实时）
```java
liveRoom.getObservableOfUserNumberChange().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
    @Override
    public void call(Integer integer) {
    }
});
```
* 被踢下线（单点登录）
  可以监听此回调，ILoginConflictModel会返回冲突的用户在什么终端登录，被踢时也会报LPError
```java
liveRoom.getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
.subscribe(new Action1<ILoginConflictModel>() {
    @Override
    public void call(ILoginConflictModel iLoginConflictModel) {
    }
});
```
* 云端录制
  云端录制功能只有**老师**角色可以调用
```java
liveRoom.requestCloudRecord(ture);                          // 开始录制
liveRoom.requestCloudRecord(false);                         // 停止录制
Observable<Boolean> getObservableOfCloudRecordStatus();     // 云端录制状态KVO
```
* 全体禁言
```java
liveRoom.requestForbidAllChat(true);                        // 开启全体禁言
liveRoom.requestForbidAllChat(false);                       // 关闭全体禁言
Observable<Boolean> getObservableOfForbidAllChatStatus();   // 全体禁言状态KVO
```
* 单个禁言
  单个用户禁言，仅限**老师**角色
```java
/**
* 禁言(teacher only)
*
* @param forbidUser 禁言用户
* @param duration   禁言时长
*/
liveRoom.forbidChat(IUserModel forbidUser, long duration);
```
禁言回调(包含其他人被禁言)
```java
liveRoom.getObservableOfForbidChat().subscribe(new Action1<IForbidChatModel>() {
    @Override
    public void call(IForbidChatModel iForbidChatModel) {
    }
});
```
当前用户是否被禁言
```java
liveRoom.getObservableOfIsSelfChatForbid().subscribe(new Action1<Boolean>() {
    @Override
    public void call(Boolean isChatForbid) {
    }
})
```
* 直播间公告

主动获取直播间公告
```java
liveRoom.requestAnnouncement(new LPErrorPrintSubscriber<String>() {
            @Override
            public void call(String s) {
            }
        });
```
设置直播间公告，仅**老师**角色可用
```java
liveRoom.changeRoomAnnouncement(string);
```
直播间公告变更通知
```java
liveRoom.getObservableOfAnnouncementChange().observeOn(AndroidSchedulers.mainThread())
.subscribe(new Action1<String>() {
    @Override
    public void call(String s) {
    }
})
```

* 随堂测验功能 （SurveyVM） <br>
  获取历史测验
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
收到老师发送新的测验
```java
liveRoom.getSurveyVM().getObservableOfSurveyReceive().observeOn(AndroidSchedulers.mainThread()).subscribe(new LPErrorPrintSubscriber<ISurveyReceiveModel>() {
    @Override
    public void call(final ISurveyReceiveModel iSurveyReceiveModel) {
        iSurveyReceiveModel.getSurvey()  //新的测验
    }
});
```
学生发送答案。
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
服务器答题统计。服务器会10秒汇总一次，如果有答题状态更新的话就广播下发
```java
/**
 * 收到测验统计结果回调
 *
 * @return
 */
Observable<ISurveyStatisticModel> getObservableOfAnswerStatistic();
```
模型接口说明
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
* 自定义事件广播接收
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

* 出错回调
```java
liveRoom.setOnLiveRoomListener(new OnLiveRoomListener() {
    @Override
    public void onError(LPError lpError) {
    }
});
```
* 错误码
```java
public static final int CODE_SUCCESS = 0;//成功
public static final int CODE_ERROR_NETWORK_FAILURE = -0x01;//失败、无网
public static final int CODE_ERROR_NETWORK_MOBILE = -0x02;//当前网络为mobile
public static final int CODE_ERROR_NETWORK_WIFI = -0x03;//wifi
public static final int CODE_ERROR_UNKNOWN = -0x04;// 未知错误
public static final int CODE_ERROR_JSON_PARSE_FAIL = -0x05;// 数据解析失败
public static final int CODE_ERROR_INVALID_PARAMS = -0x06; // 无效参数
public static final int CODE_ERROR_ROOMSERVER_FAILED = -0x07; // roomserver登录失败
public static final int CODE_ERROR_OPEN_AUDIO_RECORD_FAILED = -0x08;//打开麦克风失败，采集声音失败
public static final int CODE_ERROR_OPEN_AUDIO_CAMERA_FAILED = -0x09;//打开摄像头失败，采集图像失败
public static final int CODE_ERROR_MAX_STUDENT = -0x0A;//人数上限
public static final int CODE_ERROR_ROOMSERVER_LOSE_CONNECTION = -0x0B; // roomserver 连接断开
public static final int CODE_ERROR_LOGIN_CONFLICT = -0x0C; // 被踢下线
public static final int CODE_ERROR_PERMISSION_DENY = -0x0D; // 权限错误
public static final int CODE_RECONNECT_SUCCESS = -0x0E; // 重连成功
public static final int CODE_ERROR_STATUS_ERROR = -0x0F; // 状态错误
public static final int CODE_ERROR_MEDIA_SERVER_CONNECT_FAILED = -0x10; //音视频服务器连接错误
public static final int CODE_ERROR_MEDIA_PLAY_FAILED = -0x11; //音视频播放失败
```