package com.structit.snake.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.structit.snake.Score;
import com.structit.snake.activity.ScoreboardActivity;

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
        //instantiation d'une task (ici login) et de sa callback
        RequestTask requestTask = new RequestTask(this, new RequestHandler() {
            @Override
            public void onResponse(Document document) {
                notifyLogin(document);
            }
        });
        try {
            //url de login pat defaut
            URL url = new URL("http://snake.struct-it.fr/login.php?user=snake&pwd=test");
            requestTask.execute(url);

        } catch (Exception ex) {

        }



        return START_STICKY;
    }

    public void notifyLogin(Document mDocument) {

            mDocument.normalizeDocument();
            Element root = mDocument.getDocumentElement();

            this.apiData.id = root.getAttribute("id");
            //stockage de l'url de base pour la réutiliser et etre tranquille si elle change
            this.apiData.path = root.getAttribute("url");

            Log.d("webservice", root.getAttribute("id"));
            Log.d("webservice", root.getAttribute("url"));

            //instantiation d'une task (ici score list) et de sa callback
            RequestTask requestTask = new RequestTask(this, new RequestHandler() {
                @Override
                public void onResponse(Document document) {
                    notifyScore(document);
                }
            });
            try {
                URL url = new URL(this.apiData.path+"score?list");
                requestTask.execute(url);

            } catch (Exception ex) {

            }

    }

    public void notifyScore(Document mDocument) {

        mDocument.normalizeDocument();

        Log.d("webservice", "notifyScore");

        this.apiData.scoresList = new ArrayList<Score>();
        //parcours du xml reçu pour créer une liste de scores
        NodeList nodes = mDocument.getElementsByTagName("score");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            Score score = new Score(element.getAttribute("value"), element.getAttribute("player"));
            Log.d("webservice", element.getAttribute("player")+" "+element.getAttribute("value"));
            this.apiData.scoresList.add(score);
        }

    }

    public void addScore(String name, int currentScore) {

        Log.d("webservice", "addScore");

        //instantiation d'une task (ici add score) et de sa callback
        RequestTask requestTask = new RequestTask(this, new RequestHandler() {
            @Override
            public void onResponse(Document document) {
                Log.d("webservice", "score added");

                //actualistion de la liste des scores
                notifyScore(document);
            }
        });
        try {
            //création d'un nom de joueur ou un nom aléatoire + ajout score en fin d'url
            URL url;
            Log.d("webservice", name);
            if (name == "" || name == " ") {
                url = new URL(this.apiData.path+"score?player=guest"+getRandomNumberInRange(0,9999999)+"&value="+currentScore);
            } else {
                url = new URL(this.apiData.path+"score?player="+name+"&value="+currentScore);
            }

            requestTask.execute(url);

        } catch (Exception ex) {
            Log.d("webservice", "catch add score");
        }

    }

    @Override
    public void onResponse(Document document) {

    }

    //return un int aléatoire dans la range [min,max]
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
