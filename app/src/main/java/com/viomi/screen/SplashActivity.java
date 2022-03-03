package com.viomi.screen;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.viomi.ovensocommon.ViomiRouterConstant;
import com.viomi.ovensocommon.utils.ApplicationManager;
import com.viomi.router.core.ViomiRouter;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goToMoudleMain();
    }

    private void goToMoudleMain() {
        String packageName = getPackageName();
        Log.i(TAG, "goToMoudleMain: packageName: " + packageName);
        if (TextUtils.equals(packageName, ApplicationManager.OVENSO_PACKAGE)) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.OVENSO_MAIN).navigation();
//            startActivity(new Intent(this, OvenMainActivity.class));
//            OvensoServiceFactory.getInstance().setOvenService(new OvenSoService());
//            ViomiRouter.getInstance().build(ViomiRouterConstant.CAMERA_TEST_MAIN).navigation();
        }
        if (TextUtils.equals(packageName, ApplicationManager.WATER_PACKAGE)) {
            ViomiRouter.getInstance().build(ViomiRouterConstant.WATER_MAIN).navigation();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "onWindowFocusChanged: hasFocus: " + hasFocus);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}