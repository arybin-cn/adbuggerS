package cn.arybin.adbuggers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;

import cn.arybin.core.Root;

public class MainActivity extends Activity {

    protected Root root = null;
    protected TextView textViewMain = null;
    protected WifiManager cachedWifiManager = null;
    protected Animation cachedSimpleAnimation = null;
    protected Animation cachedScaleAnimation = null;
    protected Animation cachedRotateAnimation = null;
    protected AnimationSet cachedAnimationSet = null;
    protected Toast cachedSimpleToast = null;
    private ViewEventListener eventListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            init();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            root.write("stop adbd");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(1);
        }
    }

    private void init() {
        initAnimation();
        initToast();
        findViews();
        initListeners();
        configureViews();
        refreshViews();
        if (initRoot()) {
            showToast("ADBD started :)");
            startADB();
        } else {
            showToast("Fail to root :(");
        }
    }

    private void initAnimation() {

        cachedSimpleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        cachedSimpleAnimation.setDuration(100);
        cachedSimpleAnimation.setRepeatMode(Animation.REVERSE);
        cachedSimpleAnimation.setRepeatCount(1);

        cachedScaleAnimation = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        cachedScaleAnimation.setDuration(30);
        cachedScaleAnimation.setRepeatMode(Animation.REVERSE);
        cachedScaleAnimation.setRepeatCount(Animation.INFINITE);

        cachedRotateAnimation = new RotateAnimation(0f, 360 * 20f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        cachedRotateAnimation.setDuration(3000);
        cachedRotateAnimation
                .setInterpolator(new AccelerateDecelerateInterpolator());

        cachedAnimationSet = new AnimationSet(false);
        cachedAnimationSet.addAnimation(cachedScaleAnimation);
        cachedAnimationSet.addAnimation(cachedRotateAnimation);
    }

    private void initToast() {
        cachedSimpleToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
    }

    private void findViews() {
        textViewMain = (TextView) findViewById(R.id.textViewMain);
    }

    private void initListeners() {
        eventListener = ViewEventListener.newInstance(this);
    }

    private void configureViews() {
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/cousine.ttf");
        textViewMain.setTypeface(typeface);
        textViewMain.setOnClickListener(eventListener);
        textViewMain.setOnLongClickListener(eventListener);
    }

    private void refreshViews() {
        textViewMain.setText(retrieveIP());
    }

    private void startADB() {
        try {
            root.write("stop adbd", "start adbd",
                    "setprop service.adb.tcp.port 5555");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean showToast(CharSequence content) {
        synchronized (cachedSimpleToast) {
            if (cachedSimpleToast != null) {
                cachedSimpleToast.setText(content);
                cachedSimpleToast.show();
                return true;
            }
        }
        return false;
    }

    private boolean initRoot() {
        root = Root.getInstance();
        return (root != null);
    }

    private String int2IP(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    private String retrieveIP() {
        if (cachedWifiManager == null) {
            cachedWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        }
        return int2IP(cachedWifiManager.getConnectionInfo().getIpAddress());
    }
}
