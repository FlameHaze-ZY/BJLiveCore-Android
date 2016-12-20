package com.baijiahulian.bjhl_liveplayer;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.avsdk.liveplayer.CameraGLSurfaceView;
import com.baijiahulian.avsdk.liveplayer.ViERenderer;
import com.baijiahulian.common.cache.sp.SharePreferenceUtil;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.context.OnLiveRoomListener;
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

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class JoinCodeActivity extends AppCompatActivity implements CoreManger.LiveRoomInitListener {

    private EditText etCode, etName, etMessage;
    private Button btnLogin, btnAddPPT, btnDrawMode, btnSend;
    private SharePreferenceUtil mSharePreferenceUtil;
    private TextView tvMessages;

    private final static String USER_NAME = "user_name";
    private final static String JOIN_CODE = "join_code";

    private LiveRoom liveRoom;

    private LPPPTFragment lppptFragment;
    private FrameLayout recorderLayout, playerLayout;

    private LPRecorder recorder; // recorder用于发布本地音视频
    private LPPlayer player; // player用于播放远程音视频流

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joincode);

        mSharePreferenceUtil = new SharePreferenceUtil(this, "liveplayer_demo_sharepreference_file");

        initViews();
        initData();
    }

    private void initViews() {
        etCode = (EditText) findViewById(R.id.activity_invite_code);
        etName = (EditText) findViewById(R.id.activity_nick_name);
        etMessage = (EditText) findViewById(R.id.activity_login_text_et);
        btnLogin = (Button) findViewById(R.id.activity_login_btn);
        btnAddPPT = (Button) findViewById(R.id.activity_login_add_ppt);
        btnDrawMode = (Button) findViewById(R.id.activity_login_draw);
        btnSend = (Button) findViewById(R.id.activity_login_text_send);
        tvMessages = (TextView) findViewById(R.id.activity_login_text_area);

        tvMessages.setMovementMethod(new ScrollingMovementMethod());

        LPRxUtils.clicks(btnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String code = etCode.getText().toString();
                        String name = etName.getText().toString();
                        if (TextUtils.isEmpty(code)) {
                            Toast.makeText(JoinCodeActivity.this, "请输入参加码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(JoinCodeActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mSharePreferenceUtil.putString(JOIN_CODE, code);
                        mSharePreferenceUtil.putString(USER_NAME, name);
                        enter(code, name);
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

        LPRxUtils.clicks(btnDrawMode)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        lppptFragment.changePPTCanvasMode();
                    }
                });

        LPRxUtils.clicks(btnAddPPT)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        lppptFragment.choosePhoto();
                    }
                });

        LPRxUtils.clicks(findViewById(R.id.activity_join_code_ov))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // 发布视频
                        if (!recorder.isPublishing())
                            recorder.publish();
                        if (!recorder.isVideoAttached())
                            recorder.attachVideo();
                    }
                });
        LPRxUtils.clicks(findViewById(R.id.activity_join_code_cv))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // 关闭视频
                        if (recorder.isVideoAttached())
                            recorder.detachVideo();
                    }
                });
        LPRxUtils.clicks(findViewById(R.id.activity_join_code_oa))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // 发布音频
                        if (!recorder.isPublishing())
                            recorder.publish();
                        if (!recorder.isAudioAttached())
                            recorder.attachAudio();
                    }
                });
        LPRxUtils.clicks(findViewById(R.id.activity_join_code_ca))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // 关闭音频
                        if (recorder.isAudioAttached())
                            recorder.detachAudio();
                    }
                });
        LPRxUtils.clicks(findViewById(R.id.activity_login_text_video))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // 播放远端视频 (在聊天框中输入远端用户的userId)
                        SurfaceView surfaceView = ViERenderer.CreateRenderer(JoinCodeActivity.this, true);
                        playerLayout.addView(surfaceView);
                        player.setVideoView(surfaceView);
                        player.playVideo(etMessage.getEditableText().toString());
                    }
                });
    }

    private void initData() {
        String code = mSharePreferenceUtil.getStringValue(JOIN_CODE, "");
        if (!TextUtils.isEmpty(code)) {
            etCode.setText(code);
        }
        String name = mSharePreferenceUtil.getStringValue(USER_NAME, "");
        if (!TextUtils.isEmpty(name)) {
            etName.setText(name);
        }
    }

    private void enter(String code, String name) {
        CoreManger.getInstance().setLiveRoomInitListener(this);
        CoreManger.getInstance().enterRoom(this, code, name);
    }

    @Override
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
                tvMessages.append(iMessageModel.getFrom().getName() + ":" + iMessageModel.getContent() + "\n");
            }
        });
        // 房间人数改变
        liveRoom.getObservableOfUserNumberChange().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                tvMessages.append("房间人数:" + integer + "\n");
            }
        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        liveRoom.quitRoom();
    }
}
