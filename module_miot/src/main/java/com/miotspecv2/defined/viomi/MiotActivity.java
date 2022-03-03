package com.miotspecv2.defined.viomi;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.viomi.common.VLogUtil;
import com.viomi.spec_profile.R;
import com.viomi.spec_profile.databinding.ActivityMiotBinding;

/**
 * miot 的测试类
 */
public class MiotActivity extends AppCompatActivity {
    private static final String TAG = "MiotActivity";
    private ActivityMiotBinding miotBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VLogUtil.init(this);
        miotBinding = DataBindingUtil.setContentView(this, R.layout.activity_miot);
        setContentView(miotBinding.getRoot());
//        MiotDeviceManager.getInstance().startMiotService(this);
        initListener();
    }

    private void initListener() {
        miotBinding.miotGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
               /* Observable.create(obserableEmitter -> MiotDeviceManager.getInstance().getMiotBindKey(obserableEmitter))
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(loadQrResult -> {
                            Log.i(TAG, "onClick: " + loadQrResult);
                        });
*/
            }
        });
    }
}