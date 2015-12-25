package com.jikexueyuan.startservicfromanother;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvCallbackText;
    private Intent serviceIntent;
    AppService.Callback cb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnBindService).setOnClickListener(this);
        findViewById(R.id.btnUnbindService).setOnClickListener(this);
        tvCallbackText = (TextView) findViewById(R.id.tvCallbackText);
        serviceIntent = new Intent(this,AppService.class);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBindService:

                break;
            case R.id.btnUnbindService:

                break;
        }
    }


}
