package com.structit.snake.service;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.CookieManager;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RequestTask extends AsyncTask<URL, Void, Boolean> {

    private static final int MAX_ATTEMPT = 3;

    private WebService mService;
    private Document mDocument;

    private RequestHandler mCallBack;

    public RequestTask(WebService service, RequestHandler callback) {
        this.mService = service;
        this.mDocument = null;
        this.mCallBack = callback;
    }

    //parametres des requetes
    private final String METHOD = "GET";
    private final Boolean OUTPUT = true;
    private final int CONNECT_TIMEOUT = 10000;
    private final int READ_TIMEOUT = 5000;


    @Override
    protected Boolean doInBackground(URL... urls) {
        Boolean isConnected = false;
        int nbAttempt = 0;

        try {
            do {

                Log.d("webservice", "do");
                HttpURLConnection connection = (HttpURLConnection) urls[0].openConnection();
                Log.d("webservice", urls[0]+"");

                connection.setRequestMethod(METHOD);
                connection.setDoOutput(OUTPUT);
                connection.setConnectTimeout(CONNECT_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                String c = CookieManager.getInstance().getCookie("snake");
                Log.d("webservice", c);
                connection.setRequestProperty("Cookie", c);

                connection.connect();
                Log.d("webservice", connection.getResponseCode()+"");


                //code retour est egale à 200
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Map<String, List<String>> headerFields = connection.getHeaderFields();
                    List<String> cookiesHeader = headerFields.get("Set-Cookie");

                    if (cookiesHeader != null) {
                        for (String cookie : cookiesHeader) {
                            CookieManager cookieManager = CookieManager.getInstance();
                            cookieManager.setCookie("snake", cookie);
                        }
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        buffer.append(inputLine);
                    }
                    reader.close();

                    Log.d("webservice", "Buffer: " + buffer.toString());

                    DocumentBuilderFactory builderFactory = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(buffer.toString()));

                    this.mDocument = documentBuilder.parse(inputSource);

                    isConnected = true;
                }
                //code retour est egale à 403
                else if (connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                    Log.d("webservice", "forbidden");
                }
                //code retour est egale à 404
                else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    Log.d("webservice", "not found");
                }

                nbAttempt++;
            } while(!isConnected && nbAttempt < MAX_ATTEMPT);
        } catch(Exception ex) {
            Log.d("webservice", "error");
        }

        return isConnected;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(success) {
            //execution de la callback passée en parametre lors de l'instantiation de la task
            mCallBack.onResponse(mDocument);
        }

    }
}