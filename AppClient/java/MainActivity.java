package com.jikexueyuan.anotherapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import com.jikexueyuan.startservicfromanother.IAppServiceRemoteBinder;
import com.jikexueyuan.startservicfromanother.TimerServiceCallback;

import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection {

    private Intent serviceIntent;
    private EditText etInput;
    private TextView tvCallbackText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = (EditText) findViewById(R.id.etInput);
        tvCallbackText = (TextView) findViewById(R.id.tvCallbackText);

        serviceIntent = new Intent();
        //添加要给夸应用启动服务组件
        serviceIntent.setComponent(new ComponentName(
                //夸应用包名
                "com.jikexueyuan.startservicfromanother",
                //夸应用类名
                "com.jikexueyuan.startservicfromanother.AppService"));

        //监听器
        findViewById(R.id.btnStartAppService).setOnClickListener(this);
        findViewById(R.id.btnStopAppService).setOnClickListener(this);
        findViewById(R.id.btnBindAppService).setOnClickListener(this);
        findViewById(R.id.btnUnbindAppService).setOnClickListener(this);
        findViewById(R.id.btnSync).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartAppService:
                Intent intent = new Intent();
                //启动另个一个应用的服务
                startService(serviceIntent);
                break;
            case R.id.btnStopAppService:
                //停止另个一个应用的服务
                stopService(serviceIntent);
                break;
            case R.id.btnBindAppService:
                //this:需要实现onServiceConnected，onServiceDisconnected
                bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btnUnbindAppService:
                unbindService(this);
                binder = null;
                break;
            case R.id.btnSync:
                if (binder != null){
                    try {
                        binder.setData(etInput.getText().toString());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = IAppServiceRemoteBinder.Stub.asInterface(service);

        //注册绑定服务
        //注册回调函数
        try {
            binder.registCallback(onServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //解绑服务
    @Override
    public void onServiceDisconnected(ComponentName name) {
        System.out.println("unbind service");
        callUnRegistBinder();
    }
    //杀死服务
    @Override
    protected void onDestroy() {
        super.onDestroy();
        callUnRegistBinder();
    }

    //解绑方法
    private void callUnRegistBinder(){
        try {
            binder.unRegistCallback(onServiceCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义个TimerServiceCallback对象
     * 使用Stub(存根)方式实现内部方法
     * 服务绑定成功之后回调回来的参数是numIndex
     */

    private TimerServiceCallback.Stub onServiceCallback = new TimerServiceCallback.Stub() {
        @Override
        public void onTimer(int numIndex) throws RemoteException {

            //回调函数调用成功后需要用sendMessage
            Message msg = new Message();
            msg.obj = MainActivity.this;//打包
            msg.arg1 = numIndex;
            handler.sendMessage(msg); //信息发出去

        }
    };

    //定义并实例化一个自定义的handler;
    private final MyHandler handler = new MyHandler();

    //自定义更新UI线程Handler
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int index = msg.arg1;//信息接收
            MainActivity _this = (MainActivity) msg.obj;//拆包
            _this.tvCallbackText.setText("这是回调回服务器端的数据："+index);
        }
    }

    //定义binder对象
    private IAppServiceRemoteBinder binder = null;
}
