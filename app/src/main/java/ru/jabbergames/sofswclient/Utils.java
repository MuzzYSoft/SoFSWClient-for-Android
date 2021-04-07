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
    public static boolean seeHist=false;
    public static boolean inFight=false;
    public final static int THEME_LIGHT = 0;
    public final static int THEME_DARK = 1;
    public static boolean flag=true;
    public static boolean isLight=true;
    public static boolean toastHpIsAcc=true;
    public static boolean toastPrMesIsAcc=true;
    public static boolean toastChMesIsAcc=true;
    public static String devId;
    public static int AuthMode = 0;
    public static int lastMessId = 0;
    public static boolean ProtectTraffic = true;
    public static String emoji = "\uD83D\uDC68";
    public static ArrayList<String> mapC = new ArrayList<String>();
    public static ArrayList<String> mapP = new ArrayList<String>();
    public static String curX;
    public static String curY;
    public static String curCode;
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