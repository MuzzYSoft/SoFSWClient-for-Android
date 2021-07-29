package ru.jabbergames.sofswclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



public class AppFirebaseMessagingService extends FirebaseMessagingService {

    //GameFragment gmFr;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Utils.fmsToken = token;

        SharedPreferences settings = getSharedPreferences("sofclient", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("fmsToken", Utils.fmsToken);
        prefEditor.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //Log.e("NEW_Message", "Message data payload: " + remoteMessage.getNotification().getBody());

        //gmFr.ShowToast(remoteMessage.getNotification().getBody());

        sendNotification(remoteMessage.getNotification().getBody());

    }

    private void sendNotification(String messageBody) {
        //Intent intent = new Intent(this, GameFragment.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Intent intent = new Intent(this.getApplicationContext(), GameFragment.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.getApplicationContext(), "notif")
                .setContentIntent(pIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setChannelId("chat_notifications");

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notificationBuilder.build());
    }
}