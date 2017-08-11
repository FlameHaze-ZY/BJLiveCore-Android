package com.baijiahulian.bjhl_liveplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baijiahulian.common.cache.sp.SharePreferenceUtil;
import com.baijiahulian.livecore.utils.LPRxUtils;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

// import com.genshuixue.liveplayer.ui.BJLiveCourseActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private EditText joinCode;
    private EditText password;
    private Button btnLogin;
    private SharePreferenceUtil mSharePreferenceUtil;

    private String JOIN_CODE = "join_code";
    private String USER_NAME = "user_name";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharePreferenceUtil = new SharePreferenceUtil(getActivity(), "liveplayer_demo_sharepreference_file");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        joinCode = (EditText) view.findViewById(R.id.join_Code);
        password = (EditText) view.findViewById(R.id.pw_join_code);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        initData();

//        WebRtcAudioManager
        LPRxUtils.clicks(btnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String code = joinCode.getText().toString();
                        String name = password.getText().toString();
                        if (TextUtils.isEmpty(code)) {
                            Toast.makeText(getActivity(), "请输入参加码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(getActivity(), "请输入昵称", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mSharePreferenceUtil.putString(JOIN_CODE, code);
                        mSharePreferenceUtil.putString(USER_NAME, name);
                        Intent i = new Intent(getActivity(), JoinCodeActivity.class);
                        i.putExtra(JOIN_CODE, code);
                        i.putExtra(USER_NAME, name);
                        startActivity(i);
                    }
                });
        return view;
    }

    private void initData() {
        String code = mSharePreferenceUtil.getStringValue(JOIN_CODE, "");
        if (!TextUtils.isEmpty(code)) {
            joinCode.setText(code);
        }
        String name = mSharePreferenceUtil.getStringValue(USER_NAME, "");
        if (!TextUtils.isEmpty(name)) {
            password.setText(name);
        }
    }

}