package com.baijia.liveplayerdemo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baijia.liveplayerdemo.CoreManger;
import com.baijia.liveplayerdemo.R;
import com.baijiahulian.common.cache.sp.SharePreferenceUtil;
import com.baijiahulian.liveplayer.context.LPConstants;
import com.baijiahulian.liveplayer.utils.LPRxUtils;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class MainActivity extends Activity {

    private EditText etCode, etName;
    private Button btnLogin;
    private SharePreferenceUtil mSharePreferenceUtil;

    private final static String USER_NAME = "user_name";
    private final static String JOIN_CODE = "join_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoreManger.getInstance().setDeployType(LPConstants.LPDeployType.Test);
        mSharePreferenceUtil = new SharePreferenceUtil(this, "liveplayer_demo_sharepreference_file");

        initViews();
        initData();
    }

    private void initViews() {
        etCode = (EditText) findViewById(R.id.activity_main_invite_code);
        etName = (EditText) findViewById(R.id.activity_main_nick_name);
        btnLogin = (Button) findViewById(R.id.activity_main_login_btn);

        LPRxUtils.clicks(btnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String code = etCode.getText().toString();
                        String name = etName.getText().toString();
                        if (TextUtils.isEmpty(code)) {
                            Toast.makeText(MainActivity.this, getString(R.string.lp_demo_code), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(name)) {
                            Toast.makeText(MainActivity.this, getString(R.string.lp_demo_nickname), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mSharePreferenceUtil.putString(JOIN_CODE, code);
                        mSharePreferenceUtil.putString(USER_NAME, name);

                        CoreManger.getInstance().enterRoom(MainActivity.this, code, name);
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
}
