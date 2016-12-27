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
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baijiahulian.avsdk.liveplayer.CameraGLSurfaceView;
import com.baijiahulian.avsdk.liveplayer.ViERenderer;
import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPConstants;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.context.OnLiveRoomListener;
import com.baijiahulian.livecore.launch.LPLaunchListener;
import com.baijiahulian.livecore.models.imodels.ILoginConflictModel;
import com.baijiahulian.livecore.models.imodels.IMediaModel;
import com.baijiahulian.livecore.models.imodels.IMessageModel;
import com.baijiahulian.livecore.models.imodels.IUserModel;
import com.baijiahulian.livecore.ppt.LPPPTFragment;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;
import com.baijiahulian.livecore.utils.LPRxUtils;
import com.baijiahulian.livecore.wrapper.LPPlayer;
import com.baijiahulian.livecore.wrapper.LPRecorder;
import com.baijiahulian.livecore.wrapper.listener.LPPlayerListener;

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

    private final String[] menuItemDrawOpen = new String[]{"打开画笔模式", "添加图片", "清除画笔"};
    private final String[] menuItemDrawClose = new String[]{"关闭画笔模式", "添加图片", "清除画笔"};
    private final String[] videoItem = new String[]{"打开视频", "打开音频", "打开美颜", "切换至高清"};

    private List<String> playerVideoItem = null;
    private String currentPlayingVideoUserId;

    private LPRecorder recorder; // recorder用于发布本地音视频
    private LPPlayer player; // player用于播放远程音视频流

    private boolean menuItemState = false;
    private boolean videoItemState = false;
    private boolean audioItemState = false;
    private boolean beautyFilterState = false;
    private boolean captureVideoDefinition = false;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        playerVideoItem = new ArrayList<>();
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
                                        lppptFragment.choosePhoto();
                                        break;
                                    case 2:
                                        lppptFragment.eraseAllShape();
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
        liveRoom.getObservableOfUserOut().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String userId) {

            }
        });
        liveRoom.getObservableOfLoginConflict().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ILoginConflictModel>() {
                    @Override
                    public void call(ILoginConflictModel iLoginConflictModel) {
                    }
                });
        Subscriber<List<IMediaModel>> subs = new LPErrorPrintSubscriber<List<IMediaModel>>() {
            @Override
            public void call(List<IMediaModel> iMediaModels) {
                playerVideoItem.clear();
                if (iMediaModels != null) {
                    for (IMediaModel model : iMediaModels) {
                        if (model.isVideoOn()) {
                            playerVideoItem.add(model.getUser().getUserId());
                        }
                    }
                }
            }
        };
        ConnectableObservable<List<IMediaModel>> obs = liveRoom.getSpeakQueueVM().getObservableOfActiveUsers();
        obs.subscribe(subs);
        obs.connect();
        liveRoom.getSpeakQueueVM().getObservableOfMediaChange().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        String userId = iMediaModel.getUser().getUserId();
                        tvMessages.append("media change:" + userId + "\n");
                        if (iMediaModel.isVideoOn() && !playerVideoItem.contains(userId)) {
                            playerVideoItem.add(userId);
                        } else if (playerVideoItem.contains(userId)) {
                            playerVideoItem.remove(userId);
                            if (userId.equals(currentPlayingVideoUserId)) {
                                currentPlayingVideoUserId = null;
                            }
                        }
                    }
                });
        liveRoom.getSpeakQueueVM().getObservableOfMediaNew().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        String userId = iMediaModel.getUser().getUserId();
                        tvMessages.append("media new:" + userId + "\n");
                        if (iMediaModel.isVideoOn()) {
                            playerVideoItem.add(userId);
                        }
                    }
                });
        liveRoom.getSpeakQueueVM().getObservableOfMediaClose().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        tvMessages.append("media close:" + iMediaModel.getUser().getUserId() + "\n");
                        String userId = iMediaModel.getUser().getUserId();
                        if (playerVideoItem.contains(userId)) {
                            playerVideoItem.remove(userId);
                            if (userId.equals(currentPlayingVideoUserId)) {
                                currentPlayingVideoUserId = null;
                            }
                        }
                    }
                });
        // 播放音视频回调
        liveRoom.getPlayer().addPlayerListener(new LPPlayerListener() {
            @Override
            public void onPlayAudioSuccess(int userId) {
                tvMessages.append("onPlayAudioSuccess:" + userId + "\n");
            }

            @Override
            public void onPlayVideoSuccess(int userId) {
                tvMessages.append("onPlayVideoSuccess:" + userId + "\n");
            }

            @Override
            public void onPlayClose(int userId) {
                tvMessages.append("onPlayClose:" + userId + "\n");
            }
        });
        // 用户列表回调
        liveRoom.getOnlineUserVM().getObservableOfOnlineUser().subscribe(new LPErrorPrintSubscriber<List<IUserModel>>() {
            @Override
            public void call(List<IUserModel> iUserModels) {
                tvMessages.append("users:");
                for (IUserModel model : iUserModels)
                    tvMessages.append(model.getName());
                tvMessages.append("\n");
            }
        });
        liveRoom.requestAnnouncement(new LPErrorPrintSubscriber<String>() {
            @Override
            public void call(String s) {

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
        // error 回调
        liveRoom.setOnLiveRoomListener(new OnLiveRoomListener() {
            @Override
            public void onError(LPError lpError) {
                Log.e("error", lpError.getMessage());
            }
        });
        liveRoom.getSpeakQueueVM().requestActiveUsers();

        recorderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                judgeVideoState();
                showVideoDialog();
            }
        });

        SurfaceView surfaceView = ViERenderer.CreateRenderer(JoinCodeActivity.this, true);
        playerLayout.addView(surfaceView);
        player.setVideoView(surfaceView);

        playerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(JoinCodeActivity.this).setTitle("播放列表").setItems(playerVideoItem.toArray(new String[playerVideoItem.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        currentPlayingVideoUserId = playerVideoItem.get(which);
                        player.playVideo(playerVideoItem.get(which));
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
            }

            @Override
            public void onLaunchError(LPError error) {
                Log.e("error", error.getCode() + " " + error.getMessage());
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
                        if (!videoItemState) {
                            videoItemState = true;
                        } else {
                            videoItemState = false;
                        }
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
                        if (!audioItemState) {
                            audioItemState = true;
                        } else {
                            audioItemState = false;
                        }
                        break;
                    case 2:
                        if (!beautyFilterState) {
                            recorder.openBeautyFilter();
                        } else {
                            recorder.closeBeautyFilter();
                        }
                        if (!beautyFilterState) {
                            beautyFilterState = true;
                        } else {
                            beautyFilterState = false;
                        }
                        break;
                    case 3:
                        if (!captureVideoDefinition) {
                            recorder.setCaptureVideoDefinition(LPConstants.LPResolutionType.HIGH);
                        } else {
                            recorder.setCaptureVideoDefinition(LPConstants.LPResolutionType.LOW);
                        }
                        if (!captureVideoDefinition) {
                            captureVideoDefinition = true;
                        } else {
                            captureVideoDefinition = false;
                        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (liveRoom != null)
            liveRoom.quitRoom();
    }
}
