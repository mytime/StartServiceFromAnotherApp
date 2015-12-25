package com.jikexueyuan.startservicfromanother;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class AppService extends Service {

    //2函数列表,回调函数管道
    private RemoteCallbackList<TimerServiceCallback> callbackList = new RemoteCallbackList<>();

    public AppService() {
    }
    //绑定AIDL
    @Override
    public IBinder onBind(Intent intent) {

        //抽象类
        return new IAppServiceRemoteBinder.Stub() {
            @Override
            public void setData(String data) throws RemoteException {
                AppService.this.data = data;
            }
            @Override
            public void registCallback(TimerServiceCallback callback) throws RemoteException {
                //注册服务
                callbackList.register(callback);
            }
            @Override
            public void unRegistCallback(TimerServiceCallback callback) throws RemoteException {
                //绑定服务
                callbackList.unregister(callback);
            }
        };
    }



    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("start service");

        //开辟新线程
        new Thread(){
            @Override
            public void run() {
                super.run();
                //改变线程对象状态
                running = true;
                for (numIndex = 0; running; numIndex++) {
                    System.out.println(numIndex);

                    //休眠前回传数据
                    //准备
                    int count = callbackList.beginBroadcast();

                    //遍历完成后开始广播
                    //
                    while (count-- > 0){
                        //先得到广播项目count,调用onTimer方法，回传numIndex
                        try {
                            //另一个应用
                            callbackList.getBroadcastItem(count).onTimer(numIndex);
                            //当前应用
                            callback.onDataChange(String.valueOf(numIndex));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    //结束
                    callbackList.finishBroadcast();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("stop service");
        //改变线程对象状态
        running = false;
    }

    private String data = "默认数据";
    //线程对象
    private boolean running = false;
    //
    private int numIndex = 0;

    //实现当前应用回调接口
    private Callback callback = null;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }
    //定义当前应用回调接口
    public static interface Callback{
        void onDataChange(String data);
    }
}
