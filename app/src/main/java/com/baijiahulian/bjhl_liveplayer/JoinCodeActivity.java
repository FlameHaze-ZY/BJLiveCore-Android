package com.baijiahulian.bjhl_liveplayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.avsdk.liveplayer.CameraGLSurfaceView;
import com.baijiahulian.avsdk.liveplayer.ViERenderer;
import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.context.OnLiveRoomListener;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.models.imodels.ILoginConflictModel;
import com.baijiahulian.livecore.models.imodels.IMediaControlModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.baijiahulian.livecore.models.imodels.IUserInModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.livecore.utils.LPBackPressureBufferedSubscriber;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.livecore.utils.LPRxUtils;
import com.baijiahulian.livecore.wrapper.LPPlayer;
import com.baijiahulian.livecore.wrapper.LPRecorder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

public class JoinCodeActivity extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private TextView tvMessages;
    private Button openMenu;
    private ScrollView scrollView;

    private final static String USER_NAME = "user_name";
    private final static String JOIN_CODE = "join_code";

    private LiveRoom liveRoom;

    private LPPPTFragment lppptFragment;
    private FrameLayout recorderLayout, playerLayout;

    private final String[] menuItemDrawOpen = new String[]{"打开画笔模式", "添加图片", "清除画笔", "课件铺满", "课件全屏"};
    private final String[] menuItemDrawClose = new String[]{"关闭画笔模式", "添加图片", "清除画笔", "课件铺满", "课件全屏"};
    private final String[] videoItem = new String[]{"打开视频", "打开音频", "打开美颜", "切换至高清", "切换摄像头"};

    //    private List<String> playerVideoItem = null;
    private List<IMediaModel> playerVideoModel;
    private String currentPlayingVideoUserId;

    private LPRecorder recorder; // recorder用于发布本地音视频
    private LPPlayer player; // player用于播放远程音视频流

    private boolean menuItemState = false;
    private boolean videoItemState = false;
    private boolean audioItemState = false;
    private boolean beautyFilterState = false;
    private boolean captureVideoDefinition = false;

    private boolean isSpeakingAllowed = false;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //如果系统回收的Activity， 但是系统却保留了Fragment， 当Activity被重新初始化， 此时， 系统保存的Fragment 的getActivity为空，
            //所以要移除旧的Fragment ， 重新初始化新的Fragment
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joincode);
        initViews();
        enter(getIntent().getStringExtra(JOIN_CODE), getIntent().getStringExtra(USER_NAME));
    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
    }

    private void initViews() {
        playerVideoModel = new ArrayList<>();
        etMessage = (EditText) findViewById(R.id.activity_login_text_et);
        btnSend = (Button) findViewById(R.id.activity_login_text_send);
        tvMessages = (TextView) findViewById(R.id.activity_login_text_area);
        openMenu = (Button) findViewById(R.id.activity_join_code_menu);
        scrollView = (ScrollView) findViewById(R.id.sl_tv);
        tvMessages.setMovementMethod(new ScrollingMovementMethod());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("菜单选项");
        LPRxUtils.clicks(openMenu)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        String[] items = null;
                        if (menuItemState) {
                            items = menuItemDrawClose;
                        } else {
                            items = menuItemDrawOpen;
                        }
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        lppptFragment.changePPTCanvasMode();
                                        if (!menuItemState) {
                                            menuItemState = true;
                                        } else {
                                            menuItemState = false;
                                        }
                                        break;
                                    case 1:
                                        // UI请自行实现
//                                        liveRoom.getDocListVM().uploadImageToPPT(String filePath)
                                        break;
                                    case 2:
                                        lppptFragment.eraseAllShape();
                                        break;
                                    case 3:
                                        lppptFragment.setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_COVERED);
                                        break;
                                    case 4:
                                        lppptFragment.setPPTShowWay(LPConstants.LPPPTShowWay.SHOW_FULL_SCREEN);
                                        break;
                                }
                            }
                        }).show();

                    }
                });

        LPRxUtils.clicks(btnSend)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String message = etMessage.getEditableText().toString();
                        if (TextUtils.isEmpty(message)) {
                            return;
                        }
                        // 发送聊天消息
                        liveRoom.getChatVM().sendMessage(message);
                        etMessage.setText("");
                    }
                });
    }

    void refreshLogView(String msg) {
        tvMessages.append(msg);
        int height = tvMessages.getLineCount() * tvMessages.getLineHeight();
        int offset = height - scrollView.getMeasuredHeight();
        if (offset < 0) {
            offset = 0;
        }
        scrollView.scrollTo(0, offset);

    }

    private void enter(String code, String name) {
        enterRoom(this, code, name);
    }

    private IMediaModel getVideoMediaById(String userId) {
        for (IMediaModel model : playerVideoModel) {
            if (model.getUser().getUserId().equals(userId))
                return model;
        }
        return null;
    }

    public void onInitSuccess(LiveRoom mLiveRoom) {
        this.liveRoom = mLiveRoom;
        //用于显示上行视频的surfaceview
        recorderLayout = (FrameLayout) findViewById(R.id.activity_join_code_video);
        CameraGLSurfaceView view = new CameraGLSurfaceView(this);
        recorderLayout.addView(view);
        recorder = liveRoom.getRecorder();
        recorder.setPreview(view);
        recorder.setCaptureVideoDefinition(LPConstants.LPResolutionType.LOW);

        playerLayout = (FrameLayout) findViewById(R.id.activity_join_code_remote_video);
        player = liveRoom.getPlayer();

        //初始化ppt模块
        lppptFragment = new LPPPTFragment();
        lppptFragment.setLiveRoom(liveRoom);
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.activity_join_code_ppt, lppptFragment);
        transaction.commitAllowingStateLoss();
        // 收到聊天消息
        liveRoom.getChatVM().getObservableOfReceiveMessage().subscribe(new Action1<IMessageModel>() {
            @Override
            public void call(IMessageModel iMessageModel) {
                refreshLogView(iMessageModel.getFrom().getName() + ":" + iMessageModel.getContent() + "\n");
            }
        });
        // 房间人数改变
        liveRoom.getObservableOfUserNumberChange().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                refreshLogView("房间人数:" + integer + "\n");
            }
        });
        // 用户进入
        liveRoom.getObservableOfUserIn().observeOn(AndroidSchedulers.mainThread()).subscribe(new LPBackPressureBufferedSubscriber<IUserInModel>() {
            @Override
            public void call(IUserInModel iUserInModel) {
                tvMessages.append("用户进入:" + iUserInModel.getUser().getName() + "\n");
            }
        });
        // 用户退出
        liveRoom.getObservableOfUserOut().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String userId) {
                tvMessages.append("用户退出:" + userId + "\n");
            }
        });
        // 全体禁言
        liveRoom.getObservableOfForbidAllChatStatus().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        btnSend.setEnabled(!aBoolean);
                    }
                });
        // 登录冲突
        liveRoom.getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ILoginConflictModel>() {
                    @Override
                    public void call(ILoginConflictModel iLoginConflictModel) {
                        Toast.makeText(JoinCodeActivity.this, "您的账号在" + iLoginConflictModel.getConflictEndType().name() + "端登录",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        Subscriber<List<IMediaModel>> subs = new LPErrorPrintSubscriber<List<IMediaModel>>() {
            @Override
            public void call(List<IMediaModel> iMediaModels) {
                playerVideoModel.clear();
                if (iMediaModels != null) {
                    for (IMediaModel model : iMediaModels) {
                        if (model.isVideoOn()) {
                            playerVideoModel.add(model);
                        }
                    }
                }
            }
        };
        // 进入房间首次获取发言队列
        ConnectableObservable<List<IMediaModel>> obs = liveRoom.getSpeakQueueVM().getObservableOfActiveUsers();
        obs.subscribe(subs);
        obs.connect();
        liveRoom.getSpeakQueueVM().requestActiveUsers();
        // 发言人音视频状态改变
        liveRoom.getSpeakQueueVM().getObservableOfMediaChange().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        String userId = iMediaModel.getUser().getUserId();
                        IMediaModel model = getVideoMediaById(userId);
                        tvMessages.append("media change:" + iMediaModel.getUser().getName() + "\n");
                        if (iMediaModel.isVideoOn() && !playerVideoModel.contains(model)) {
                            playerVideoModel.add(iMediaModel);
                        } else if (!iMediaModel.isVideoOn() && playerVideoModel.contains(model)) {
                            playerVideoModel.remove(model);
                            if (userId.equals(currentPlayingVideoUserId)) {
                                currentPlayingVideoUserId = null;
                            }
                        }
                    }
                });
        // 新的发言
        liveRoom.getSpeakQueueVM().getObservableOfMediaNew().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        tvMessages.append("media new:" + iMediaModel.getUser().getName() + "\n");
                        if (iMediaModel.isVideoOn()) {
                            playerVideoModel.add(iMediaModel);
                        }
                    }
                });
        // 关闭发言
        liveRoom.getSpeakQueueVM().getObservableOfMediaClose().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        tvMessages.append("media close:" + iMediaModel.getUser().getName() + "\n");
                        String userId = iMediaModel.getUser().getUserId();
                        IMediaModel model = getVideoMediaById(userId);
                        if (playerVideoModel.contains(model)) {
                            playerVideoModel.remove(model);
                            if (userId.equals(currentPlayingVideoUserId)) {
                                currentPlayingVideoUserId = null;
                            }
                        }
                    }
                });
        // 用户列表回调
        liveRoom.getOnlineUserVM().getObservableOfOnlineUser().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<List<IUserModel>>() {
                    @Override
                    public void call(List<IUserModel> iUserModels) {
                        tvMessages.append("users:");
                        for (IUserModel model : iUserModels)
                            tvMessages.append(model.getName() + " ");
                        tvMessages.append("\n");
                    }
                });
        // 主动请求公告
        liveRoom.requestAnnouncement(new LPErrorPrintSubscriber<String>() {
            @Override
            public void call(String s) {
                tvMessages.append("公告:" + s + "\n");
            }
        });
        // 公告修改通知
        liveRoom.getObservableOfAnnouncementChange().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<String>() {
                    @Override
                    public void call(String s) {
                        tvMessages.append("公告:" + s + "\n");
                    }
                });
        liveRoom.getObservableOfClassStart().subscribe(new LPErrorPrintSubscriber<Void>() {
            @Override
            public void call(Void aVoid) {
                tvMessages.append("上课了\n");
            }
        });
        liveRoom.getObservableOfClassEnd().subscribe(new LPErrorPrintSubscriber<Void>() {
            @Override
            public void call(Void aVoid) {
                tvMessages.append("下课了\n");
            }
        });
        // 上行链路切换
        recorder.getObservableOfLinkType().subscribe(new LPErrorPrintSubscriber<LPConstants.LPLinkType>() {
            @Override
            public void call(LPConstants.LPLinkType lpLinkType) {
                tvMessages.append("上行:" + lpLinkType.name() + "\n");
            }
        });
        // 下行链路切换
        player.getObservableOfLinkType().subscribe(new LPErrorPrintSubscriber<LPConstants.LPLinkType>() {
            @Override
            public void call(LPConstants.LPLinkType lpLinkType) {
                tvMessages.append("下行:" + lpLinkType.name() + "\n");
            }
        });
        // error 回调
        liveRoom.setOnLiveRoomListener(new OnLiveRoomListener() {
            @Override
            public void onError(LPError lpError) {
                Log.e("error", lpError.getMessage());
                tvMessages.append("error code:" + lpError.getCode() + " error msg:" + lpError.getMessage() + "\n");
            }
        });

        recorderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 学生先请求发言
                if (liveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Student &&
                        !isSpeakingAllowed) {
                    new AlertDialog.Builder(JoinCodeActivity.this).setTitle("菜单").setItems(new String[]{"举手"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            liveRoom.getSpeakQueueVM().requestSpeakApply();
                        }
                    }).show();
                    return;
                }
                judgeVideoState();
                showVideoDialog();
            }
        });
        // 老师远程控制
        liveRoom.getSpeakQueueVM().getObservableOfMediaControl().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
                    @Override
                    public void call(IMediaControlModel iMediaControlModel) {
                        if (!iMediaControlModel.isApplyAgreed()) {
                            // 老师关闭发言
                            isSpeakingAllowed = false;
                            videoItemState = false;
                            audioItemState = false;
                            beautyFilterState = false;
                            captureVideoDefinition = false;
                        }
                    }
                });
        // 有学生申请发言 自动同意
        liveRoom.getSpeakQueueVM().getObservableOfSpeakApply().subscribe(new Action1<IMediaModel>() {
            @Override
            public void call(IMediaModel iMediaModel) {
                liveRoom.getSpeakQueueVM().agreeSpeakApply(iMediaModel.getUser().getUserId());
            }
        });
        // 老师处理发言结果
        liveRoom.getSpeakQueueVM().getObservableOfSpeakResponse()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new LPErrorPrintSubscriber<IMediaControlModel>() {
                    @Override
                    public void call(IMediaControlModel iMediaControlModel) {
                        if (!iMediaControlModel.getUser().getUserId().equals(liveRoom.getCurrentUser().getUserId()))
                            return;
                        if (iMediaControlModel.isApplyAgreed()) {
                            tvMessages.append("老师同意了你的发言\n");
                            isSpeakingAllowed = true;
                        } else {
                            tvMessages.append("老师拒绝了你的发言\n");
                            isSpeakingAllowed = false;
                        }
                    }
                });
        //上课
        if (liveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Teacher)
            liveRoom.requestClassStart();

        SurfaceView surfaceView = ViERenderer.CreateRenderer(JoinCodeActivity.this, true);
        playerLayout.addView(surfaceView);
        player.setVideoView(surfaceView);

        playerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> playerVideoName = new ArrayList<String>();
                for (IMediaModel model : playerVideoModel) {
                    playerVideoName.add(model.getUser().getName());
                }
                String[] temp = playerVideoName.toArray(new String[playerVideoName.size() + 1]);
                temp[playerVideoName.size()] = "关闭视频";
                new AlertDialog.Builder(JoinCodeActivity.this).setTitle("播放列表")
                        .setItems(temp, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                if (which > playerVideoName.size()) {
                                    return;
                                }
                                if (which == playerVideoName.size()) {
                                    if (!TextUtils.isEmpty(currentPlayingVideoUserId)) {
                                        player.playAVClose(currentPlayingVideoUserId);
                                        if (getVideoMediaById(currentPlayingVideoUserId).isAudioOn())
                                            player.playAudio(currentPlayingVideoUserId);
                                        currentPlayingVideoUserId = null;
                                    }
                                    return;
                                }
                                if (which < playerVideoModel.size()) {
                                    currentPlayingVideoUserId = playerVideoModel.get(which).getUser().getUserId();
                                    player.playVideo(playerVideoModel.get(which).getUser().getUserId());
                                }
                            }
                        }).show();
            }
        });
    }

    public void enterRoom(final Context context, final String code, final String name) {
        LiveSDK.enterRoom(context, code, name, new LPLaunchListener() {
            @Override
            public void onLaunchSteps(int step, int totalStep) {
                Log.i("init steps", "step:" + step + "/" + totalStep);
                tvMessages.append("init steps: " + step + "/" + totalStep + "\n");
            }

            @Override
            public void onLaunchError(LPError error) {
                Log.e("error", error.getCode() + " " + error.getMessage());
                Toast.makeText(JoinCodeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onLaunchSuccess(LiveRoom liveRoom) {
                onInitSuccess(liveRoom);
            }
        });
    }

    private void showVideoDialog() {
        new AlertDialog.Builder(JoinCodeActivity.this).setTitle("菜单").setItems(videoItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (!videoItemState) {
                            if (!recorder.isPublishing())
                                recorder.publish();
                            if (!recorder.isVideoAttached())
                                recorder.attachVideo();
                        } else {
                            videoItemState = true;
                            if (recorder.isVideoAttached())
                                recorder.detachVideo();
                        }
                        videoItemState = !videoItemState;
                        break;
                    case 1:
                        if (!audioItemState) {
                            if (!recorder.isPublishing())
                                recorder.publish();
                            if (!recorder.isAudioAttached())
                                recorder.attachAudio();
                        } else {
                            if (recorder.isAudioAttached())
                                recorder.detachAudio();
                        }
                        audioItemState = !audioItemState;
                        break;
                    case 2:
                        if (!beautyFilterState) {
                            beautyFilterState = true;
                            recorder.openBeautyFilter();
                        } else {
                            beautyFilterState = false;
                            recorder.closeBeautyFilter();
                        }
                        break;
                    case 3:
                        if (!captureVideoDefinition) {
                            captureVideoDefinition = true;
                            recorder.setCaptureVideoDefinition(LPConstants.LPResolutionType.HIGH);
                        } else {
                            captureVideoDefinition = false;
                            recorder.setCaptureVideoDefinition(LPConstants.LPResolutionType.LOW);
                        }
                        break;
                    case 4:
                        recorder.switchCamera();
                        break;
                }
            }
        }).show();
    }

    private void judgeVideoState() {
        if (!videoItemState) {
            videoItem[0] = "打开视频";
        } else {
            videoItem[0] = "关闭视频";
        }
        if (!audioItemState) {
            videoItem[1] = "打开音频";
        } else {
            videoItem[1] = "关闭音频";
        }
        if (!beautyFilterState) {
            videoItem[2] = "打开美颜";
        } else {
            videoItem[2] = "关闭美颜";
        }
        if (!captureVideoDefinition) {
            videoItem[3] = "切换至高清";
        } else {
            videoItem[3] = "切换至普清";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recorder != null && recorder.isPublishing()) {
            if (recorder.isVideoAttached()) {
                recorder.detachVideo();
                recorder.attachVideo();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (liveRoom != null && liveRoom.getCurrentUser() != null && liveRoom.getCurrentUser().getType() == LPConstants.LPUserType.Teacher)
            liveRoom.requestClassEnd();
        if (liveRoom != null)
            liveRoom.quitRoom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("test");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (recorder == null || player == null) return true;
        menu.clear();
        if (recorder.getLinkType() == LPConstants.LPLinkType.TCP) {
            menu.add(Menu.NONE, 0, 0, "上行链路:TCP->UDP");
        } else {
            menu.add(Menu.NONE, 0, 0, "上行链路:UDP->TCP");
        }
        if (player.getLinkType() == LPConstants.LPLinkType.TCP) {
            menu.add(Menu.NONE, 1, 1, "下行链路:TCP->UDP");
        } else {
            menu.add(Menu.NONE, 1, 1, "下行链路:UDP->TCP");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 0) {
            if (recorder.getLinkType() == LPConstants.LPLinkType.TCP) {
                recorder.setLinkType(LPConstants.LPLinkType.UDP);
            } else {
                recorder.setLinkType(LPConstants.LPLinkType.TCP);
            }
        } else if (item.getItemId() == 1) {
            if (player.getLinkType() == LPConstants.LPLinkType.TCP) {
                player.setLinkType(LPConstants.LPLinkType.UDP);
            } else {
                player.setLinkType(LPConstants.LPLinkType.TCP);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
