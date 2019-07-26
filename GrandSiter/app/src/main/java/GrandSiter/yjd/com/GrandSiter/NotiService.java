package GrandSiter.yjd.com.GrandSiter;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class NotiService extends Service {
    SensorCheckThread sensorCheckThread;
    MediCheckThread mediCheckThread;
    LocateCheckThread locateCheckThread;
    NotificationHelper n;
    private String userID;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        myServiceHandler handler = new myServiceHandler();
        userID = (String) intent.getExtras().get("userID");
        n = new NotificationHelper(NotiService.this);
        mediCheckThread = new MediCheckThread(handler);
        sensorCheckThread = new SensorCheckThread(handler);
        locateCheckThread = new LocateCheckThread(handler, userID);
        mediCheckThread.start();
        sensorCheckThread.start();
        locateCheckThread.start();

        return START_STICKY;

    }
    public void onDestroy() {
        sensorCheckThread = null;
        mediCheckThread = null;

    }

    class myServiceHandler extends Handler {
        StringBuilder name;
        StringBuilder mediItem;
        @Override
        public void handleMessage(android.os.Message msg) {
            //Log.d("msg : ", Integer.toString(msg.what));


            if(msg.what == 0) {
               n.createNotification("기저귀 교체 요청", msg.obj.toString());
            }
            else if(msg.what == 1){
                mediItem = new StringBuilder(msg.obj.toString());
                Log.d("mediItem : ", mediItem.toString());
                n.createNotification("약 복용", mediItem.toString());
            }
            else if(msg.what == 2){
                n.createNotification("위치 확인 요청", msg.obj.toString());
            }
            //토스트 띄우기
            //Toast.makeText(MyService.this, "뜸?", Toast.LENGTH_LONG).show();
        }
    };

}
