package com.noahedu.upen;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @description:
 * @data:2021/12/20
 */
@RunWith(AndroidJUnit4.class)
public class TestRobolectric {
    @Test
    public void equal() {
        Assert.assertEquals("0","0");
    }
}
