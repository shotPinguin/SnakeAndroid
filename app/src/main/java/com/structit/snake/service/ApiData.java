package com.structit.snake.service;

import com.structit.snake.Score;

import java.util.ArrayList;
import java.util.List;

public class ApiData {

    public static String path;
    public static String id;
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
