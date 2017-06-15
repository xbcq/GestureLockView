package tex.xb.com.gesturelockview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import tex.xb.com.wiget.GestureLockView;

public class MainActivity extends Activity {


    private Animation mShake;
    private int state = 0;//1,第一次设置2，确认3，验证
    private GestureLockView mLockView;
    private TextView mTvTips;
    private String cachePwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLockView = (GestureLockView) findViewById(R.id.myview);
        mTvTips = (TextView) findViewById(R.id.tv_tips);
        mShake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLockView.clear();
                mTvTips.setText("请设置手势密码");
                state = 1;
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLockView.clear();
                state = 3;
            }
        });
        mLockView.setOnChooseListener(new GestureLockView.OnChooseListener() {
            @Override
            public void onMaxNum() {

                mTvTips.setText("设置的手势密码至少要包含4个点，请重新绘制");
                mTvTips.setTextColor(getResources().getColor(R.color.color_ff3939));
                mTvTips.startAnimation(mShake);
                mLockView.setError();
                if(state == 2){
                    state = 1;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLockView.clear();
                            mTvTips.setText("请设置手势密码");
                            mTvTips.setTextColor(getResources().getColor(R.color.color_00));
                        }
                    },1000);
                }else{
                    mLockView.clear();
                }
            }

            @Override
            public void onComplete(String pwd) {


                switch (state){
                    case 1:
                        state = 2;
                        mLockView.clear();
                        mTvTips.setText("再次输入以确认");
                        mTvTips.setTextColor(getResources().getColor(R.color.color_00));
                        cachePwd = pwd;
                        break;
                    case 2:
                        if(!TextUtils.equals(cachePwd,pwd)) {
                            mTvTips.setText("密码不一致，请重新设置");
                            mTvTips.setTextColor(getResources().getColor(R.color.color_ff3939));
                            mTvTips.startAnimation(mShake);
                            state = 1;
                            cachePwd ="";
                            mLockView.setError();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLockView.clear();
                                    mTvTips.setText("请设置手势密码");
                                    mTvTips.setTextColor(getResources().getColor(R.color.color_00));
                                }
                            },1000);
                        }else {
                            mLockView.clear();
                            Toast.makeText(MainActivity.this,"设置成功",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 3:
                        if(!TextUtils.equals(cachePwd,pwd)) {
                            mTvTips.setText("密码错误");
                            mTvTips.setTextColor(getResources().getColor(R.color.color_ff3939));
                            mTvTips.startAnimation(mShake);
                            mLockView.setError();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mLockView.clear();
                                    mTvTips.setText("请输入手势密码");
                                    mTvTips.setTextColor(getResources().getColor(R.color.color_00));
                                }
                            },1000);
                        }else {
                            mLockView.clear();

                            Toast.makeText(MainActivity.this,"验证成功",Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }
}
