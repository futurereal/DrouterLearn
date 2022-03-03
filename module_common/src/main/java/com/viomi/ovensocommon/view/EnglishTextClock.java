package com.viomi.ovensocommon.view;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextClock;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EnglishTextClock extends TextClock {
    private static final String TAG = "MyTextClock";
    private String ampmName;

    public EnglishTextClock(Context context) {
        this(context, null);
    }

    public EnglishTextClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnglishTextClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLocaleDateFormat();
        addTextChangedListener(new EnglishTextCahgneListener());
    }

    private String getEnAmPmName() {
        Locale currentLocale = new Locale("en");
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getDefault(), currentLocale);
        String currentAmPmName = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, currentLocale);
        return currentAmPmName;
    }

    private void setLocaleDateFormat() {
        ampmName = getEnAmPmName();
        this.setFormat12Hour("hh:mm '" + ampmName + "'");
        this.setFormat24Hour("HH:mm");
    }

    class EnglishTextCahgneListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i(TAG, "onTextChanged: ");
            String currentAmpmName = getEnAmPmName();
            if (!TextUtils.equals(currentAmpmName, ampmName)) {
                setLocaleDateFormat();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
