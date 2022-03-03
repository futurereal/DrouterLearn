package com.viomi.robolectric.test;

import android.widget.Button;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.viomi.screen.R;
import com.viomi.waterpurifier.edison.ui.WaterMainActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

/**
 * @description:
 * @data:2021/12/20
 */
@RunWith(AndroidJUnit4.class)
public class TestActivity {
    @Test
    public void testAdd() {
        WaterMainActivity waterMainActivity = Robolectric.setupActivity(WaterMainActivity.class);
        TextView textView = waterMainActivity.findViewById(R.id.watermain_ampm);
        Button button = waterMainActivity.findViewById(R.id.watermain_setting);
        button.performClick();

        Assert.assertEquals("0", "0");
    }

    @Config(qualifiers = "fr-xlarge")
    public void getSandwichName() {
        Assert.assertEquals("0", "0");
    }
}
