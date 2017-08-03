package com.example.jamessmith.marvelcomics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.jamessmith.marvelcomics.comics.ComicsFragment;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter("updateMain");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null){
                    Toast.makeText(context, "Got selected index: " + intent.getIntExtra("indexValue", 0),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        swapFragment(0);
    }

    private void swapFragment(int index){
        Fragment fragment;
        Bundle bundle = new Bundle();

        switch(index){
            case 0:
                fragment = new ComicsFragment();
                break;
            case 1:
                fragment = new ComicDescription();
                fragment.setArguments(bundle);
                break;
            default:
                fragment = new ComicsFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment, null).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        try{
            unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }
}
