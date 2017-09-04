package com.example.jamessmith.marvelcomics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.jamessmith.marvelcomics.backend.services.BackgroundService;
import com.example.jamessmith.marvelcomics.comics.ComicModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BackgroundServiceInstrumentedTest {

    private Intent intent;
    private Context context;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    @Before
    public void setUp() {
        context =  InstrumentationRegistry.getTargetContext();
        intent = new Intent(context, BackgroundService.class);
    }

    @Test
    public void useAppContext() throws Exception {
        assertEquals("com.example.jamessmith.marvelcomics", context.getPackageName());
    }

    @Test
    public void testComicDataAccessibility() throws TimeoutException {
        intent.putExtra("requestedOperation", "comicsFragment");
        intent.putExtra("selectedPrice", "999.999");//Set to value about cost of any comic; to get all comics.
        context.startService(intent);

        intentFilter = new IntentFilter("updateComicFragment");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ArrayList<ComicModel> allParcelables = intent.getParcelableArrayListExtra("comicData");

                if(allParcelables != null){
                    assertTrue(allParcelables.size() > 0);
                    assertEquals(allParcelables.size(), 100);
                }
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    @After
    public void tearDown() throws Exception {

        try {
            if(context != null) {
                context.unregisterReceiver(broadcastReceiver);
            }
        }catch(Exception e){}

    }
}
