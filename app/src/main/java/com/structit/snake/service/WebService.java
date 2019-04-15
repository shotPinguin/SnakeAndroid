package com.structit.snake.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.structit.snake.Score;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WebService extends Service implements RequestHandler{

    private static final int NOTIFICATION_CHANNEL_ID = 101;
    private static final String NOTIFICATION_CHANNEL_NAME = "WEB_CHANNEL";
    private static final String NOTIFICATION_CHANNEL_DESCR = "Snake web service";

    private ApiData apiData = ApiData.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = NOTIFICATION_CHANNEL_NAME;
            String description = NOTIFICATION_CHANNEL_DESCR;

            String channelID = Integer.toString(NOTIFICATION_CHANNEL_ID);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelID, name,  importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification.Builder notificationBuilder = new Notification.Builder(this, channelID);
            Notification notification = notificationBuilder.build();
            startForeground(Integer.valueOf(channelID), notification);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("webservice", "onStartCommand");
        //LoginTask loginTask = new LoginTask(this);
        RequestTask requestTask = new RequestTask(this, new RequestHandler() {
            @Override
            public void onResponse(Document document) {
                notifyLogin(document);
            }
        });
        try {
            URL url = new URL("http://snake.struct-it.fr/login.php?user=snake&pwd=test");
            //loginTask.execute(url);
            requestTask.execute(url);

        } catch (Exception ex) {

        }



        return START_STICKY;
    }

    public void notifyLogin(Document mDocument) {

            mDocument.normalizeDocument();
            Element root = mDocument.getDocumentElement();
            this.apiData.id = root.getAttribute("id");
            this.apiData.path = root.getAttribute("url");

            Log.d("webservice", root.getAttribute("id"));
            Log.d("webservice", root.getAttribute("url"));

            RequestTask requestTask = new RequestTask(this, new RequestHandler() {
                @Override
                public void onResponse(Document document) {
                    notifyScore(document);
                }
            });
            try {
                URL url = new URL(this.apiData.path+"score?list");
                //scoreTask.execute(url);
                requestTask.execute(url);

            } catch (Exception ex) {

            }

    }

    public void notifyScore(Document mDocument) {

        mDocument.normalizeDocument();
        Element root = mDocument.getDocumentElement();

        Log.d("webservice", "notifyScore");

        NodeList nodes = mDocument.getElementsByTagName("score");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            Score score = new Score(element.getAttribute("value"), element.getAttribute("player"));
            Log.d("webservice", element.getAttribute("player")+" "+element.getAttribute("value"));
            this.apiData.scoresList.add(score);
        }

    }

    public void addScore(int currentScore) {

        Log.d("webservice", "addScore");

        RequestTask requestTask = new RequestTask(this, new RequestHandler() {
            @Override
            public void onResponse(Document document) {
                //notifyScore(document);
                Log.d("webservice", "score added");
            }
        });
        try {
            URL url = new URL(this.apiData.path+"score?player=guest"+getRandomNumberInRange(0,9999999)+"&value="+currentScore);
            //scoreTask.execute(url);
            requestTask.execute(url);

        } catch (Exception ex) {

        }

    }

    @Override
    public void onResponse(Document document) {

    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
