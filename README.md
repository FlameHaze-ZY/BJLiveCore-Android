<style>
	table th {
		border:0.5px solid #a8b6c3;
	}
	table td {
		border:0.5px solid #a8b6c3;
		padding: 0px 5px 0px 5px !important;
		height: 30px !important;
	}
	h1 {
		font-size: 30px;
	}
	h2 {
		font-size: 27px;
	}
	h3 {
		font-size: 22px !important;
		font-weight: 500 !important;
	}
	h4 {
		font-size: 19px;
		font-weight: 500;
	}
	h5 {
		font-size: 16px;
		font-weight: 500;
	}
	h6 {
		font-size: 13px;
		font-weight: 500;
	}
</style>

Android 直播 Core SDK
===============
- **[github链接](https://github.com/baijia/BJLiveCore-Android)**
- platform: API 14~25  [BeautyVideoFilter(美颜功能) 18+]
- cpu: ARM, ARMv7a, ARM64v8a
- IDE: **[Android Studio](https://developer.android.com/studio/index.html)** Recommend
- **[Change Log](https://github.com/baijia/BJLiveCore-Android/blob/master/CHANGELOG.md)**

## 功能介绍
百家云直播Android SDK提供了[Core (liveplayer-sdk-core)](https://github.com/baijia/BJLiveCore-Android)、[PPT (liveplayer-sdk-core-ppt)](link_ppt)和[UI (BJLiveUI-Android)](https://github.com/baijia/BJLiveUI-Android)三个库。

- [UI](https://github.com/baijia/BJLiveUI-Android)库基于[Core](https://github.com/baijia/BJLiveCore-Android)和[PPT](link_ppt)实现，提供了一个针对教育场景下师生互动模板，主要包括师生一对一音视频互动，多人音视频互动，课件展示、文字聊天等功能，可以快速接入，集成工作量小，适合需要快速上线的同学，该库计划开源。
- [Core](https://github.com/baijia/BJLiveCore-Android)为核心库，涵盖了直播间几乎所有的功能，包括音视频推拉流、信令服务器通信、聊天服务器通信等功能，该库不含UI资源，如果使用的是eclipse的同学可以将aar其中的jar包拿出来单独使用。
- [PPT](link_ppt)单独把课件模块拆出来了，主要功能包含课件展示、画笔交互、动态PPT效果等，依赖于[Core](https://github.com/baijia/BJLiveCore-Android)。

### 1. 概念 
<table>
    <tbody>
    	<tr>
    		<td width = "40px">老师</td>
    		<td>主讲人，拥有直播间最高权限，可以 设置上下课、发公告、处理他人举手、远程开关他人音视频、开关录课、开关聊天禁言</td>
    	</tr>
    	<tr>
    		<td>助教</td>
    		<td>
    			管理员，拥有部分老师的权限，不包含上、下课等改变教室状态的功能			</td>
    	</tr>
    	<tr>
    		<td>学生</td>
    		<td>听讲人，权限受限，无法对他人的直播间内容进行管理</td>
    	</tr>
    	<tr>
    		<td width = "36px";>教室</td>
    		<td>直播间，提供创建、管理等一系列功能。提供上课、下课等接口，大多数功能模块只有在上课状态下有效
    		</td>
    	</tr>
    	<tr>
    		<td>举手</td>
    		<td>学生申请发言，老师和管理员可以允许或拒绝</td>
    	</tr>
    	<tr>
    		<td>发言</td>
    		<td>发布音频、视频，SDK 层面发言不要求举手状态</td>
    	</tr>
    	<tr>
    		<td>播放</td>
    		<td>播放他人发布的视频，支持同时播放多个人的视频</td>
    	</tr>
    	<tr>
    		<td>录课</td>
    		<td>云端录制课程</td>
    	</tr>
    	<tr>
    		<td>聊天</td>
    		<td>直播间内的群聊功能，支持发送图片、表情</td>
    	</tr>
    	<tr>
    		<td>课件</td>
    		<td>课件第一页是白板，主要用于添加画笔；老师可上传图片格式的课件，上传成功之后可在直播间内显示；支持 PPT 动画（需要在 PC 端上传）
    		</td>
    	</tr>
    	<tr>
    		<td>画笔</td>
    		<td>
    			老师、助教或发言状态的学生可以在 白板和 PPT 上添加、清除画笔；添加画笔的用户当前的 PPT 页必须与老师保持一致
    		</td>
    	</tr>
    	<tr>
    		<td>公告</td>
    		<td>
    			由老师编辑、发布，可包含跳转链接，即时更新
    		</td>
    	</tr>
    </tbody>
</table>

### 2.主要功能

<table>
	<thead>
		<tr>
			<th align = "center">模块</th>
			<th align = "center">功能</th>
			<th align = "center">接口</th>
	</tr>
	</thead>
	<tbody>
    	<tr>
    		<td rowspan = "5">教室管理</td>
    		<td>进入 / 退出教室及相应的事件监听</td>
    		<td rowspan = "5">LiveRoom</td>
    	</tr>
    	<tr>
    		<td>异常监听</td>
    	</tr>
    	<tr>
    		<td>进入教室的加载状态监听</td>
    	</tr>
    	<tr>
    		<td>老师：上课 / 下课</td>
    	</tr>
    	<tr>
    		<td>禁言</td>
    	</tr>
    	<tr>
    		<td rowspan = "2">在线用户信息管理</td>
    		<td>加载在线用户信息</td>
    		<td rowspan = "2">OnlineUserVM</td>
    	</tr>
    	<tr>
    		<td>监听用户进入、退出教室</td>
    	</tr>
    	<tr>
    		<td rowspan = "4">音视频采集</td>
    		<td>开启／关闭音视频采集</td>
    		<td rowspan = "4">LPRecorder</td>
    	</tr>
    	<tr>
    		<td>音视频采集状态监听</td>
    	</tr>
    	<tr>
    		<td>采集设置：视频方向，清晰度，美颜</td>
    	</tr>
    	<tr>
    		<td>设置用于音视频的上行链路的类型：UDP／TCP</td>
    	</tr>
    	<tr>
    		<td rowspan = "4">视频播放</td>
    		<td>播放、关闭指定用户的视频</td>
    		<td rowspan = "4">LPPlayer</td>
    	</tr>
    	<tr>
    		<td>老师：远程开关用户麦克风、摄像头</td>
    	</tr>
    	<tr>
    		<td>监听对象音视频开关状态、音视频用户列表变化</td>
    	</tr>
    	<tr>
    		<td>设置用于音视频的下行链路的类型：UDP／TCP</td>
    	</tr>
    		<td rowspan = "2">举手、发言邀请</td>
    		<td>学生举手、取消举手，老师处理举手申请</td>
    		<td rowspan = "2">SpeakQueueVM</td>
    	</tr>
    	<tr>
    		<td>学生接收、处理发言邀请</td>
    	</tr>
    	<tr>
    		<td rowspan = "3">课件管理</td>
    		<td>上传、添加课件，删除课件，课件翻页</td>
    		<td>DocListVM</td>
    	</tr>
    	<tr>
    		<td>加载所有课件</td>
    		<td rowspan = "2">LPPPTFragment</td>
    	</tr>
    	<tr>
    		<td>监听课件添加、删除</td>
    	</tr>
    	<tr>
    		<td>画笔</td>
    		<td>开启/关闭画笔，清空画板</td>
    		<td> LPPPTFragment </td>    		</tr>
    	<tr>
    		<td rowspan = "2">聊天</td>
    		<td>发送消息（文字、图片、表情）</td>
    		<td rowspan = "2">ChatVM</td>
    	</tr>
    	<tr>
    		<td>监听收到消息</td>
    	</tr>
    	<tr>
    		<td>录课</td>
    		<td>老师：监听云端录课不可用的通知，获取云端录课状态，开启/停止云端录课</td>
    		<td>LiveRoom</td>
    	</tr>
    	<tr>
    		<td rowspan = "2">公告</td>
    		<td>发布公告</td>
    		<td rowspan = "2">LiveRoom</td>
    	</tr>
    	<tr>
    		<td>获取教室公告，监听公告变化</td>
    	</tr>
    	<tr>
    		<td rowspan = "2">测验</td>
    		<td>获取历史题目、新题目、答题统计</td>
    		<td rowspan = "2">SurveyVM</td>
    	</tr>
    	<tr>
    		<td>学生：答题</td>
    	</tr>
    </tbody>
</table>

## 引入SDK
###1.添加maven仓库
```groovy
maven { url 'https://raw.github.com/baijia/maven/master/' }
```
对于部分国内用户，如果github被墙或者访问比较慢，可以使用我们国内的镜像仓库
```groovy
maven { url 'http://live-cdn.baijiayun.com/android-sdk/' }
```
###2.添加依赖
```groovy
dependencies {
	compile 'com.baijia.live:liveplayer-sdk-core:1.0.1'
}
```
如果使用到了PPT、白板、涂鸦等功能可以可以使用我们为您提供的PPTFragment，需添加如下依赖

```groovy
	compile 'com.baijia.live:liveplayer-sdk-core-ppt:1.0.0'
```

## API说明
### 1.要点说明

- SDK所有的直播功能都是基于教室这个场景的，进入教室成功之后才能正常使用各个功能模块。要进入教室，需要调用`LiveSDK.enterRoom`，如果成功会回调到`LPLaunchListener.onLaunchSuccess(LiveRoom liveRoom)`，返回的LiveRoom实例可以获取到各个功能对应的ViewModel，后文会对进入教室和各个ViewModel具体说明。

- RxJava订阅之后在不使用时需要反订阅，例如

```java
// 监听上课
Subscription subscription = liveRoom.getObservableOfClassStart().subscribe(subscriber);
// 在onDestroy时，需要反订阅
subscription.unsubscribe();
```
本文为了简单起见，忽略了反订阅操作。

### 2.创建、进入教室
#### 进入直播间

LiveSDK.enterRoom目前提供房间号和参加码两种方式进入房间，接口如下

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
Sign原则上由后端计算返给前端，[计算规则](http://dev.baijiayun.com/default/wiki/detail/4#h3)
#### 进入房间回调说明

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
#### 离开直播间
  一般在**Activity**的`onDestroy()`中调用
 
```java
liveRoom.quitRoom();
```
### 3.音视频管理
#### 发布音视频(推流)
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
**注意：**发布视频时需要先设置本地视频采集的preview，然后再调用`recorder.attachVideo();`这里我们提供了SuraceView和TextureView两个版本

```java
CameraGLSurfaceView view= new CameraGLSurfaceView(this);
recorder.setPreview(view);
```
或者

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

#### 播放音视频(拉流)
如果有用户（老师/学生）正在发布音视频，这个用户会出现在发言列表SpeakQueueVM的ActiveUser中。当获得这个用户对象的userId就能播放其音视频流

```java
LPPlayer player = liveRoom.getPlayer();
player.playAudio(userId);   //播放音频
player.playVideo(userId, view);   //播放音视频
player.playAVClose(userId); //关闭音视频流
```
**注意：**`player.playVideo(userId, view)`中的view为显示视频的view，可以根据需要创建SurfaceView或者TextureView

```java
SurfaceView view = ViESurfaceViewRenderer.CreateRenderer(getContext(), true);
TextureView view = ViETextureViewRenderer.CreateRenderer(getContext(), true);
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
### 4.举手发言
请求发言队列

```java
liveRoom.getSpeakQueueVM().requestActiveUsers();
```
请求队列回调

```java
Observable<List<IMediaModel>> obs = liveRoom.getSpeakQueueVM().getObservableOfActiveUsers();
Subscriber<List<IMediaModel>> subs = new LPErrorPrintSubscriber<List<IMediaModel>>() {
    @Override
    public void call(List<IMediaModel> iMediaModels) {
    }
};
obs.subscribe(subs);
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

### <span id = "function">5.在线用户</span>
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
#### 用户进入房间（房间人数小于100人时）

```java
liveRoom.getObservableOfUserIn().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<IUserInModel>() {
    @Override
    public void call(IUserInModel iUserInModel) {        
    }
});
```
#### 用户退出房间（房间人数小于100人时）

```java
liveRoom.getObservableOfUserOut().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
    @Override
    public void call(String userId) {        
    }
});
```
#### 房间人数变化（实时）

```java
liveRoom.getObservableOfUserNumberChange().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
    @Override
    public void call(Integer integer) {
    }
});
```

### 6.课件、画笔
  如果您使用了我们提供的PPTFragment，如下初始化即可
  
```java
LPPPTFragment lppptFragment = new LPPPTFragment();
lppptFragment.setLiveRoom(liveRoom);
```
学生端主动滑动PPT翻页的逻辑是不大于老师PPT的当前页面。如果您想禁止学生主动滑动PPT翻页（仍然会随老师翻页），可以在学生端调用

```java
lppptFragment.setFlingEnable(false);
boolean lppptFragment.isFlingEnable(); //是否支持滑动
```
切换PPT在容器中显示全屏\铺满

```java
lppptFragment.setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_FULL_SCREEN);
lppptFragment.setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_COVERED);
```
画笔模式\浏览模式切换

```java
lppptFragment.changePPTCanvasMode();
boolean lppptFragment.isEditable(); //是否在画笔模式
```
清除画笔

```java
lppptFragment.clearCanvas();
```
显示/隐藏页码

```java
lppptFragment.showPPTPageView();
lppptFragment.hidePPTPageView();
```

此外，在0.4.0+的版本中支持了动态PPT（PPT动画），具体设置可以在百家云后台配置。目前仅支持Android 5.0及以上的版本。

### 7.聊天
#### 发送消息
文字消息

```java
liveRoom.getChatVM().sendMessage(msg);
```
```java
liveRoom.getChatVM().sendMessage(msg, channel);
```
发送表情

```java
liveRoom.getChatVM().sendEmojiMessage("[" + emoji.key + "]");
```
获取配置的表情库

```java
List<IExpressionModel> expressions = liveRoom.getChatVM().getExpressions();
```
发送图片

```java
String imageContent = LPChatMessageParser.toImageMessage(imageUrl);
liveRoom.getChatVM().sendImageMessage(imageContent, imageWidth, imageHeight);
```
#### 接收消息
收到新消息

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

### 8.录课
  云端录制功能只有**老师**角色可以调用
```java
liveRoom.requestCloudRecord(ture);                          // 开始录制
liveRoom.requestCloudRecord(false);                         // 停止录制
Observable<Boolean> getObservableOfCloudRecordStatus();     // 云端录制状态KVO
```

### 9.公告
房间公告支持跳转，如果不需要可以直接传null。
主动获取直播间公告
```java
liveRoom.requestAnnouncement();
```
设置直播间公告，仅**老师**角色可用
```java
liveRoom.changeRoomAnnouncement(content, link);
```
直播间公告变更通知
```java
liveRoom.getObservableOfAnnouncementChange().observeOn(AndroidSchedulers.mainThread())
    .subscribe(new LPErrorPrintSubscriber<IAnnouncementModel>() {
        @Override
        public void call(IAnnouncementModel iAnnouncementModel) {
            String content = iAnnouncementModel.getContent();
            String url = iAnnouncementModel.getLink();
    }
});
```

### 10.测验
#### 获取历史测验
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
#### 收到老师发送新的测验
```java
liveRoom.getSurveyVM().getObservableOfSurveyReceive().observeOn(AndroidSchedulers.mainThread()).subscribe(new LPErrorPrintSubscriber<ISurveyReceiveModel>() {
    @Override
    public void call(final ISurveyReceiveModel iSurveyReceiveModel) {
        iSurveyReceiveModel.getSurvey()  //新的测验
    }
});
```
#### 学生发送答案
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
#### 服务器答题统计
服务器会10秒汇总一次，如果有答题状态更新的话就广播下发
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
### 11.LiveRoom其他API
#### 上课/下课
```java
liveRoom.requestClassStart();
liveRoom.requestClassEnd();
liveRoom.getObservableOfClassStart().subscribe(subscriber);
liveRoom.getObservableOfClassEnd().subscribe(subscriber);
```
一般地，requestClassStart和requestClassEnd均由**老师**角色调用
#### 获取当前用户
```java
IUserModel currentUser = liveRoom.getCurrentUser();
```
#### 获取老师用户
```java
IUserModel currentUser = liveRoom.getTeacherUser();
```
#### 被踢下线（单点登录）
  可以监听此回调，ILoginConflictModel会返回冲突的用户在什么终端登录，被踢时也会报LPError
```java
liveRoom.getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
.subscribe(new Action1<ILoginConflictModel>() {
    @Override
    public void call(ILoginConflictModel iLoginConflictModel) {
    }
});
```

#### 全体禁言
```java
liveRoom.requestForbidAllChat(true);                        // 开启全体禁言
liveRoom.requestForbidAllChat(false);                       // 关闭全体禁言
Observable<Boolean> getObservableOfForbidAllChatStatus();   // 全体禁言状态KVO
```
#### 单个禁言
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
#### 自定义事件广播接收
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

### 12.出错回调
```java
liveRoom.setOnLiveRoomListener(new OnLiveRoomListener() {
    @Override
    public void onError(LPError lpError) {
    }
});
```
#### 错误码
```java
public static final int CODE_ERROR_NETWORK_FAILURE = -0x01;//无网
public static final int CODE_ERROR_NETWORK_MOBILE = -0x02;//当前网络为mobile
public static final int CODE_ERROR_NETWORK_WIFI = -0x03;//wifi
public static final int CODE_ERROR_UNKNOWN = -0x04;// 未知错误
public static final int CODE_ERROR_JSON_PARSE_FAIL = -0x05;// 数据解析失败
public static final int CODE_ERROR_INVALID_PARAMS = -0x06; // 无效参数
public static final int CODE_ERROR_ROOMSERVER_FAILED = -0x07; //roomserver登录失败
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
public static final int CODE_ERROR_CHATSERVER_LOSE_CONNECTION = -0x12; // chatserver 连接断开
public static final int CODE_ERROR_MESSAGE_SEND_FORBID = -0x13; //发言被禁止
public static final int CODE_ERROR_VIDEO_PLAY_EXCEED = -0x14; // 超出最大播放视频数量
public static final int CODE_ERROR_LOGIN_KICK_OUT = -0x15; //被踢
public static final int CODE_ERROR_FORBID_RAISE_HAND = -0x16; //举手被禁止
```

## 集成常见问题
- 是否支持模拟器？  
  答：直播暂时不支持x86架构，模拟器的话不能使用音视频模块。
- 视频窗口和PPT叠加时，显示不出来？  
  答：PPT画笔本质上是一个SurfaceView，当视频窗口采用SurfaceView时，会存在SurfaceView叠加的问题，可以采用SurfaceView的setZOrderMediaOverLayer或者setZOrderOnTop来解决，原理请参见[官方文档](https://developer.android.com/reference/android/view/SurfaceView.html#setZOrderMediaOverlay(boolean))。
