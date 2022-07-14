package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class ComService extends Service {
    String text;
    boolean changed;
    private int NOTIF_ID = 10234;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            while (true) {
                System.out.println("service is running....");
                try {
                    if (!changed) {
                        for (Element element : Jsoup.connect("https://ceice.gva.es/va/web/rrhh-educacion/cuerpo-de-secundaria-y-otros-cuerpos3").get().select("#main-content")) {
//                                    System.out.println(element.text());
                            if (text == null) {
                                text = element.text();
                            } else {
                                if (!text.equals(element.text())) {
                                    changed = true;
                                    startForeground(NOTIF_ID, getNotification("Inga va!!!!!", R.drawable.celebration));

                                } else {
//                                    System.out.println("no change");
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();

        startForeground(NOTIF_ID, getNotification("Checking CEICE...", R.drawable.coffee));

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification getNotification(String text, int icon){

        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(CHANNELID, CHANNELID, NotificationManager.IMPORTANCE_LOW);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText(text)
//                .setContentTitle("CEICE WATCHER")
                .setSmallIcon(icon);

        return notification.build();
    }
}