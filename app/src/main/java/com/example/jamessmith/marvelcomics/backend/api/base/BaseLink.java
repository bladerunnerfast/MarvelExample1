package com.example.jamessmith.marvelcomics.backend.api.base;

/**
 * Created by James on 03/08/2017.
 */

public class BaseLink {

    private static final String base = "http://gateway.marvel.com";

    public static String getBase(){
        return base;
    }
}
