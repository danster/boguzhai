package com.boguzhai.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.boguzhai.activity.mainpage.MainActivity;

public class AvtivityEntry extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		startActivity(new Intent(this, MainActivity.class));
    }
}
