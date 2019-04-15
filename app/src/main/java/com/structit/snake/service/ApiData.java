package com.structit.snake.service;

import com.structit.snake.Score;

import java.util.ArrayList;
import java.util.List;

//informations renvoyées par l'api stocké dans une instance de singleton
public class ApiData {

    //url de base de l'api
    public static String path;
    //id de l'api
    public static String id;
    //liste des scores
    public static List<Score> scoresList = new ArrayList<Score>();

    /** Instance unique pré-initialisée */
    private static ApiData INSTANCE = null;

    /** Point d'accès pour l'instance unique du singleton */
    public static ApiData getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new ApiData();
        }
        return INSTANCE;
    }

    /** Constructeur privé */
    private ApiData() {

    }

}
