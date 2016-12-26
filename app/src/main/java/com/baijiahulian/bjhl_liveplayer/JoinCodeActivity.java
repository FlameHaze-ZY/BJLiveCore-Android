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
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.avsdk.liveplayer.CameraGLSurfaceView;
import com.baijiahulian.avsdk.liveplayer.ViERenderer;
import com.baijiahulian.livecore.LiveSDK;
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

    private Menu menu = null;

    private final static String USER_NAME = "user_name";
    private final static String JOIN_CODE = "join_code";

    private LiveRoom liveRoom;

    private LPPPTFragment lppptFragment;
    private FrameLayout recorderLayout, playerLayout;

    private final String[] menuItem = new String[]{"画笔模式", "添加图片"};
    private final String[] videoItem = new String[]{"打开视频", "关闭视频", "打开音频", "关闭音频"};
    private String[] playerItem = null;

    private LPRecorder recorder; // recorder用于发布本地音视频
    private LPPlayer player; // player用于播放远程音视频流

    private int videoItemNum = 0;
    private int menuItemNum = 0;


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
        etMessage = (EditText) findViewById(R.id.activity_login_text_et);
        btnSend = (Button) findViewById(R.id.activity_login_text_send);
        tvMessages = (TextView) findViewById(R.id.activity_login_text_area);
        openMenu = (Button) findViewById(R.id.activity_join_code_menu);
        tvMessages.setMovementMethod(new ScrollingMovementMethod());
        LPRxUtils.clicks(openMenu)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        new AlertDialog.Builder(JoinCodeActivity.this).setTitle("菜单选项").setSingleChoiceItems(menuItem, menuItemNum, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                menuItemNum = which;
                                // TODO Auto-generated method stub
                                switch (which) {
                                    case 0:
                                        lppptFragment.changePPTCanvasMode();
                                        break;
                                    case 1:
                                        lppptFragment.choosePhoto();
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
        int offset = tvMessages.getLineCount() * tvMessages.getLineHeight();
        if (offset > tvMessages.getHeight()) {
            tvMessages.scrollTo(0, offset - tvMessages.getHeight());
        }
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

        playerLayout = (FrameLayout) findViewById(R.id.activity_join_code_remote_video);
        player = liveRoom.getPlayer();

        //初始化ppt模块
        lppptFragment = new LPPPTFragment();
        lppptFragment.setLiveRoom(liveRoom);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
                if (iMediaModels != null) {
                    int num = -1;
                    playerItem = new String[iMediaModels.size()];
                    for (int i = 0; i < iMediaModels.size(); i++) {
                        if (iMediaModels.get(i).isVideoOn()) {
                            playerItem[num++] = iMediaModels.get(i).getUser().getUserId();
                        }
                    }
                }
                if (iMediaModels.size() == 0) {
                    Toast.makeText(JoinCodeActivity.this, "没有远端在线远端视频", Toast.LENGTH_LONG).show();
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
                        tvMessages.append("media change:" + iMediaModel.getUser().getUserId() + "\n");
                    }
                });
        liveRoom.getSpeakQueueVM().getObservableOfMediaNew().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        tvMessages.append("media new:" + iMediaModel.getUser().getUserId() + "\n");
                    }
                });
        liveRoom.getSpeakQueueVM().getObservableOfMediaClose().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<IMediaModel>() {
                    @Override
                    public void call(IMediaModel iMediaModel) {
                        tvMessages.append("media close:" + iMediaModel.getUser().getUserId() + "\n");
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
                new AlertDialog.Builder(JoinCodeActivity.this).setTitle("菜单").setSingleChoiceItems(videoItem, videoItemNum, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        videoItemNum = which;
                        switch (which) {
                            case 0:
                                if (!recorder.isPublishing())
                                    recorder.publish();
                                if (!recorder.isVideoAttached())
                                    recorder.attachVideo();
                                break;
                            case 1:
                                if (recorder.isVideoAttached())
                                    recorder.detachVideo();
                                break;
                            case 2:
                                if (!recorder.isPublishing())
                                    recorder.publish();
                                if (!recorder.isAudioAttached())
                                    recorder.attachAudio();
                                break;
                            case 3:
                                if (recorder.isAudioAttached())
                                    recorder.detachAudio();
                                break;
                        }
                    }
                }).show();
            }
        });
        playerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(JoinCodeActivity.this).setTitle("播放列表").setItems(playerItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        // 播放远端视频 (在聊天框中输入远端用户的userId)
                        SurfaceView surfaceView = ViERenderer.CreateRenderer(JoinCodeActivity.this, true);
                        playerLayout.addView(surfaceView);
                        player.setVideoView(surfaceView);
                        player.playVideo(playerItem[which]);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liveRoom.quitRoom();
    }
}
