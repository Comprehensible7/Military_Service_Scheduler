package com.teamproject.aaaaan_2.util;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teamproject.aaaaan_2.MainActivity;
import com.teamproject.aaaaan_2.R;

/**
 * Created by hanman-yong on 2019-12-30.
 */
public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebase";
    private Context mContext = null;

    @Override
    public void onNewToken(String token) {
        Log.d("FCM Log", "Refreshed token: " + token);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {  //data payload로 보내면 실행
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //여기서 메세지의 두가지 타입(1. data payload 2. notification payload)에 따라 다른 처리를 한다.
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //화면을 깨운다. 그러나 이방법은 Deprecated 되었다. 더이상 사용되지 않는다는 것이다. 현재로 작동은 하지만 나중에 어떻게 될지 모른다.
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            wakeLock.acquire(3000);

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("content");
            String click_action = remoteMessage.getData().get("clickAction");
            sendNotification(title, body, click_action);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
//            String click_action = remoteMessage.getNotification().getClickAction();
//            sendNotification(title, body, click_action);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    private void sendNotification(String title, String messageBody, String click_action) {
        if (title == null){
            //제목이 없는 payload이면
            title = "FCM Noti"; //기본제목을 적어 주자.
        }

        //전달된 액티비티에 따라 분기하여 해당 액티비티를 오픈하도록 한다.
        Intent intent;
        if (click_action.equals("MainActivity")) {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setLights(Color.BLUE, 1,1)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (remoteMessage.getNotification() != null) {
//            Log.d("FCM Log", "알림 메시지: " + remoteMessage.getNotification().getBody());
//            String messageBody = remoteMessage.getNotification().getBody();
//            String messageTitle = remoteMessage.getNotification().getTitle();
//            Intent intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//            String channelId = "Channel ID";
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, channelId)
//                            .setSmallIcon(R.mipmap.ic_launcher)
//                            .setContentTitle(messageTitle)
//                            .setContentText(messageBody)
//                            .setAutoCancel(true)
//                            .setSound(defaultSoundUri)
//                            .setContentIntent(pendingIntent);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                String channelName = "Channel Name";
//                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//                notificationManager.createNotificationChannel(channel);
//            }
//            notificationManager.notify(0, notificationBuilder.build());
//        }
//    }
//}