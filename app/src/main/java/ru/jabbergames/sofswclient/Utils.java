package ru.jabbergames.sofswclient;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by hoqu on 29.09.15.
 */
public class Utils
{
    private static int sTheme;
    public final static int THEME_LIGHT = 0;
    public final static int THEME_DARK = 1;
    public static boolean flag=true;
    public static boolean isLight=true;
    public static boolean toastHpIsAcc=true;
    public static boolean toastPrMesIsAcc=true;
    public static ArrayList<String> mapC = new ArrayList<String>();
    public static ArrayList<String> mapP = new ArrayList<String>();
    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();

        activity.startActivity(new Intent(activity, activity.getClass()));

    }

    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case THEME_LIGHT:
                activity.setTheme(R.style.AppTheme);
                isLight=true;
                break;
            case THEME_DARK:
                activity.setTheme(R.style.AppThemeDark);
                isLight=false;
                break;
        }
    }
}