package com.example.jamessmith.marvelcomics;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jamessmith.marvelcomics.comics.ComicsFragment;
import com.example.jamessmith.marvelcomics.description.ComicDescription;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private BroadcastReceiver broadcastReceiver;
    private int fragmentIndex = 0;

    @BindView(R.id.et_search) EditText _price;
    @BindView(R.id.btn_refine) Button _refineBtn;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.bottomSheetLayout) RelativeLayout bottomSheet;
    @BindView(R.id.bottomSheetHeading)TextView bottomSheetHeading;

    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        _price.clearFocus();

        if (!_price.hasFocus()) {
            _price.setText(getString(R.string.cost));
        }

        initBottomsheet();
        initFloatingButtonAction();
        initBroadcastHandler();

        if(!isStoragePermissionGranted()){
            Toast toast = Toast.makeText(this,"To view images please grant permissions.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            fragmentIndex = 0;
            swapFragment("0");
        }

        _price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    _price.setText("");
                }else{
                    _price.setText(getString(R.string.cost));
                }
            }
        });

        fragmentIndex = 0;
        swapFragment("0");
    }

    private void initBottomsheet(){
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetHeading.setText(getString(R.string.expanded));
                }

                // Check Logs to see how bottom sheets behaves
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i(TAG, "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i(TAG, "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i(TAG, "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i(TAG, "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i(TAG, "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                bottomSheet.setAlpha(slideOffset);
            }
        });
    }

    private void initFloatingButtonAction(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){

                    }
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
    }

    private void initBroadcastHandler(){
        IntentFilter intentFilter = new IntentFilter("updateMain");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null){
                    fragmentIndex = 1;
                    swapFragment(intent.getStringExtra("indexValue"));
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        _refineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentIndex = 0;
                swapFragment(_price.getText().toString());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private void swapFragment(String value){
        Fragment fragment;
        Bundle bundle = new Bundle();

        switch(fragmentIndex){
            case 0:
                fragment = new ComicsFragment();
                bundle.putString("price", value);
                fragment.setRetainInstance(true);
                break;
            case 1:
                fragment = new ComicDescription();
                bundle.putString("index", value);

                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                floatingActionButton.setVisibility(View.INVISIBLE);
                break;
            default:
                fragment = new ComicsFragment();
                break;
        }

        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frag_container, fragment, null).addToBackStack(null).commit();
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission has been granted");
                return true;
            } else {

                Log.v(TAG,"Permission has been revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //Automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission has been granted");
            return true;
        }
    }

    @Override
    public void onBackPressed() {

        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
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
