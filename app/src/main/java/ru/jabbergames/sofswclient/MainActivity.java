package ru.jabbergames.sofswclient;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity  extends TabActivity {

    private String deviceId;
    private String ClVer = "a.1.0.0.1";
    private int STextEditID;
    int chatminid = 2147483647;

    private TextView mCmdTextView;

    Button btnCmdSend;

    TabHost tabHost;

    final String TABS_TAG_1 = "game";
    final String TABS_TAG_2 = "coms";
    final String TABS_TAG_3 = "chat";
    final String TABS_TAG_4 = "cmds";

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;

    private List<String> ReqGm = new ArrayList<String>();
    private int tick = 0;

    ArrayList<String> mapC = new ArrayList<String>();
    ArrayList<String> mapP = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        setDeviceId(deviceUuid.toString());

        tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec(TABS_TAG_1);
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator(getString(R.string.title_activity_game));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TABS_TAG_2);
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator(getString(R.string.title_activity_com_buts));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TABS_TAG_3);
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator(getString(R.string.title_activity_chat));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TABS_TAG_4);
        tabSpec.setContent(TabFactory);
        tabSpec.setIndicator(getString(R.string.title_activity_cmd));
        tabHost.addTab(tabSpec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                if (tabId == TABS_TAG_2) {
                    SendCom("getcomms");
                } else if (tabId == TABS_TAG_3) {
                    SendCom("chatmess !chroom? descr");
                    //addLog("chatmess !chroom? descr");
                    LinearLayout ll = (LinearLayout) findViewById(R.id.chatContent);
                    if (ll.getChildCount() == 0) SendCom("chatmess !history");
                    Button btn = (Button) findViewById(R.id.chatRoomSelButt);
                    // создаем обработчик нажатия
                    View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LinearLayout ll = (LinearLayout) findViewById(R.id.chatContent);
                            ll.removeAllViewsInLayout();
                            SendCom("chatmess !chroom? list");
                            addLog("chatmess !chroom? list");
                        }
                    };
                    // присвоим обработчик кнопке
                    btn.setOnClickListener(oclBtnCmd);

                    Button btncs = (Button) findViewById(R.id.chatSendButton);
                    // создаем обработчик нажатия
                    View.OnClickListener oclCBtnCmd = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText et = (EditText) findViewById(R.id.chatText);
                            if (et.getText().length() > 0) {
                                SendCom("chatmess " + nk + et.getText().toString());
                                addLog("chatmess " + nk + et.getText().toString());
                                et.setText("");
                            }
                        }
                    };
                    // присвоим обработчик кнопке
                    btncs.setOnClickListener(oclCBtnCmd);
                } else if (tabId == TABS_TAG_4) {
                    btnCmdSend = (Button) findViewById(R.id.cmdSendButton);

                    // создаем обработчик нажатия
                    View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText mCmdText = (EditText) findViewById(R.id.cmdText);
                            String scom = mCmdText.getText().toString();
                            if (scom != "") {
                                SendCom(mCmdText.getText().toString());
                                addLog(mCmdText.getText().toString());
                            }
                            mCmdText.setText("");
                        }
                    };

                    // присвоим обработчик кнопке
                    btnCmdSend.setOnClickListener(oclBtnCmd);

                    //авторпрокрутка
                    TextView textView1 = (TextView) findViewById(R.id.logTextView);
                    textView1.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void afterTextChanged(Editable arg0) {
                            ScrollView scrollView1 = (ScrollView) findViewById(R.id.scrollViewCons);
                            scrollView1.fullScroll(ScrollView.FOCUS_DOWN);
                            // you can add a toast or whatever you want here
                        }

                        @Override
                        public void beforeTextChanged(CharSequence arg0, int arg1,
                                                      int arg2, int arg3) {
                            //override stub
                        }

                        @Override
                        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                            //override stub
                        }

                    });
                }
            }
        });

        tabHost.setCurrentTabByTag(TABS_TAG_4);
        SendCom("getmappoints");
        SendCom("0");
        tabHost.setCurrentTabByTag(TABS_TAG_1);

        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();

        mTimer.schedule(mMyTimerTask, 1000, 1000);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ReqGm.size() > 0) { //(ReqCur == ReqCnt) {
                        String com = ReqGm.get(0);
                        SendComN(com);
                        ReqGm.remove(0);
                    } else {
                        if (tick > 4)
                        {
                            SendComN("000");
                            tick = 0;
                        } else {
                            tick += 1;
                        }
                    }
                }
            });
        }
    }

    private void SendCom(String comstr)
    {
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarMine);
        pb.setVisibility(View.VISIBLE);
        ReqGm.add(ReqGm.size(), comstr);
    }

    TabHost.TabContentFactory TabFactory = new TabHost.TabContentFactory() {

        @Override
        public View createTabContent(String tag) {
            if (tag == TABS_TAG_1) {
                return getLayoutInflater().inflate(R.layout.activity_game, null);
            } else if (tag == TABS_TAG_2) {
                /*TextView tv = new TextView(MainActivity.this);
                tv.setText("Команды");
                return tv;*/
                return getLayoutInflater().inflate(R.layout.activity_com_buts, null);
            } else if (tag == TABS_TAG_3) {
                return getLayoutInflater().inflate(R.layout.activity_chat, null);
            } else if (tag == TABS_TAG_4) {
                return getLayoutInflater().inflate(R.layout.activity_cmd, null);
            }
            return null;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Since this activity is part of a TabView we want to send
        // the back button to the TabView activity.
        SendCom("0");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //лог в консоле
    public void addLog(String addstr) {
        TextView mCmdText = (TextView)findViewById(R.id.logTextView);
        if (mCmdText != null) {
            mCmdText.append("\n\r" + addstr);
        }
    }

    private void ClearButtc() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ComButtLay);
        ll.removeAllViewsInLayout();
    }

    private void AddButC(String kay, String txt) {
        if (txt != "") {
            LinearLayout ll = (LinearLayout) findViewById(R.id.ComButtLay);
            Button btn = new Button(this);
            btn.setText(txt);
            btn.setTag(kay);

            // создаем обработчик нажатия
            View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String com = (String)v.getTag();
                    SendCom(com);
                    addLog(com);
                    tabHost.setCurrentTabByTag(TABS_TAG_1);
                 }
            };

            // присвоим обработчик кнопке
            btn.setOnClickListener(oclBtnCmd);

            ll.addView(btn);
        }
    }

    private void ClearGLL() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.GameLinearLayout);
        ll.removeAllViewsInLayout();
    }

    private void AddGText(String txt) {
        if (txt != "") {
            LinearLayout ll = (LinearLayout) findViewById(R.id.GameLinearLayout);
            TextView tv = new TextView(MainActivity.this);
            tv.setText(txt);
            ll.addView(tv);
        }
    }

    private void  AddLnkButt(String kay, String txt) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.GameLinearLayout);
        Button btn = new Button(this);
        btn.setText(kay);
        btn.setTag(txt);
        // создаем обработчик нажатия
        View.OnClickListener oclBtnCmd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String com = (String)v.getTag();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(com));
                startActivity(browserIntent);
            }
        };

        // присвоим обработчик кнопке
        btn.setOnClickListener(oclBtnCmd);

        ll.addView(btn);
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    private void AddButG(String kay, String txt) {
        if (txt != "") {
            LinearLayout ll = (LinearLayout) findViewById(R.id.GameLinearLayout);
            switch (kay)
            {
                case "name":
                case "X":
                case "N":
                case "Х":
                    //добавить поле ввода
                    EditText et = new EditText(this);
                    et.setHint(txt);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        STextEditID = generateViewId();
                    } else {
                        STextEditID = View.generateViewId();
                    }
                    et.setId(STextEditID);
                    ll.addView(et);

                    Button btnS = new Button(this);
                    btnS.setText("OK");

                    // создаем обработчик нажатия
                    View.OnClickListener oclBtnCmdS = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText mCmdText = (EditText) findViewById(STextEditID);
                            String scom = mCmdText.getText().toString();
                            if (scom == "") { scom = "0"; }
                            SendCom(scom);
                            addLog(scom);
                            mCmdText.setText("");
                        }
                    };

                    // присвоим обработчик кнопке
                    btnS.setOnClickListener(oclBtnCmdS);
                    ll.addView(btnS);
                    break;
                default:
                    //добавить кнопку
                    Button btn = new Button(this);
                    btn.setText(txt);
                    btn.setTag(kay);
                    btn.setTransformationMethod(null);

                    // создаем обработчик нажатия
                    View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String com = (String)v.getTag();
                            SendCom(com);
                            addLog(com);
                        }
                    };

                    // присвоим обработчик кнопке
                    btn.setOnClickListener(oclBtnCmd);
                    ll.addView(btn);
                    break;
            }
        }
    }

    private void SetPname(String nm) {
        TextView tv = (TextView) findViewById(R.id.player_name_text);
        tv.setText(nm);
    }

    private void SetPlev(String de, String lv) {
        TextView tv = (TextView) findViewById(R.id.player_lev_text);
        tv.setText("  " + de + lv);
    }

    private void SetPHP(String hpdes, String hp, String hpmax) {
        TextView tv = (TextView) findViewById(R.id.progress_hp_text);
        tv.setText(hpdes + hp + "/" + hpmax);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarHP);
        pb.setMax(Integer.parseInt(hpmax));
        pb.setProgress(Integer.parseInt(hp));
    }

    private void SetPSP(String spdes, String sp, String spmax) {
        TextView tv = (TextView) findViewById(R.id.progress_sp_text);
        tv.setText(spdes + sp + "/" + spmax);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarSP);
        pb.setMax(Integer.parseInt(spmax));
        pb.setProgress(Integer.parseInt(sp));
    }

    private void SetPPT(String ptdes, String pt, String ptmax) {
        String shpt = pt;
        if (pt.length() > 5)
        {
            shpt = pt.substring(0, pt.length() - 3) + "k";
        }
        TextView tv = (TextView) findViewById(R.id.progress_pt_text);
        Integer i = Integer.parseInt(ptmax) - Integer.parseInt(pt);
        tv.setText(ptdes + shpt + "/" + i.toString());  // Convert.ToString(Convert.ToInt32(ptmax) - Convert.ToInt32(pt));
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarPT);
        pb.setMax(Integer.parseInt(ptmax));
        pb.setProgress(Integer.parseInt(pt));
    }

    private void SetAtten(String p) {
        TextView tv = (TextView) findViewById(R.id.player_atten_text);
        if (p.contains("1")) {
            tv.setText(getString(R.string.level_up_atten));
        }
        else {
            tv.setText("");
        }
    }

    private void AddChatRoomD(String chnm, String chdes, String chincount) {
        TextView tv = (TextView) findViewById(R.id.chatRoomName);
        tv.setText(chnm);
        tv = (TextView) findViewById(R.id.chatRoomCount);
        tv.setText(getString(R.string.room_cnt)+chincount+getString(R.string.room_cnt_p));
        tv = (TextView) findViewById(R.id.chatRoomDescr);
        tv.setText(chdes);
    }

    private void ChatClearAll() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.chatContent);
        ll.removeAllViewsInLayout();
    }

    private void AddChatRoomB(String chnum, String chname, String des, String incount) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.chatContent);
        Button btn = new Button(this);
        btn.setText("Комната: " + chname + "  {" + incount + "чел.}\n" + des);
        btn.setTag("chatmess !chroom! " + chnum);
        btn.setTransformationMethod(null);
        // создаем обработчик нажатия
        View.OnClickListener oclBtnCmd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String com = (String)v.getTag();
                SendCom(com);
                addLog(com);
                LinearLayout ll = (LinearLayout) findViewById(R.id.chatContent);
                ll.removeAllViewsInLayout();
            }
        };

        // присвоим обработчик кнопке
        btn.setOnClickListener(oclBtnCmd);

        ll.addView(btn);
    }

    private void AddToChat(String from, String to, String message, String dtime, boolean priv, boolean totop) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.chatContent);

        //кнопка загрузки истории
        if (ll.getChildCount()>0) {
            Button ghbtn = (Button) ll.getChildAt(0);
            if (ghbtn.getTag().toString() != "ghbtn") {
                ghbtn = new Button(this);
                ghbtn.setText("старые сообщения");
                ghbtn.setTag("ghbtn");
                // создаем обработчик нажатия
                View.OnClickListener oclBtnGhbtn = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout ll = (LinearLayout) findViewById(R.id.chatContent);
                        TextView tv = new TextView(MainActivity.this);
                        tv.setText("=================");
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        ll.addView(tv, 1);
                        SendCom("chatmess !history " + Integer.toString(chatminid));
                        addLog("chatmess !history "+Integer.toString(chatminid));
                    }
                };
                // присвоим обработчик кнопке
                ghbtn.setOnClickListener(oclBtnGhbtn);
                ll.addView(ghbtn, 0);
            }

        }


        String shou = "";
        String smin = "";
        if (dtime != "none")
        {
            Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5"));
            int gmtOffset = TimeZone.getDefault().getRawOffset()-TimeZone.getTimeZone("GMT+5").getRawOffset();

            int index = dtime.indexOf(":");
            smin = dtime.substring(index + 1, 5);
            shou = dtime.substring(0, index);
            int min = Integer.parseInt(smin);
            int hou = Integer.parseInt(shou);

            currentCalendar.set(Calendar.HOUR_OF_DAY, hou);
            currentCalendar.set(Calendar.MINUTE, min);

            currentCalendar.setTimeInMillis(currentCalendar.getTimeInMillis() + gmtOffset);

            hou = (int)currentCalendar.get(Calendar.HOUR_OF_DAY);
            min = (int)currentCalendar.get(Calendar.MINUTE);

            if (min < 10) {
                shou = Integer.toString(hou) + ":0" + Integer.toString(min);
            } else {
                shou = Integer.toString(hou) + ":" + Integer.toString(min);
            }
        }

        Button btn = new Button(this);
        if(priv){
            btn.setText(shou + " приватно от " + from + ":\n\r" + message);
            btn.setTextColor(Color.parseColor("#ccff0e15"));
        }else {
            btn.setText(shou + " " + from + ":\n" + message);
        }
        btn.setGravity(Gravity.LEFT);
        btn.setTransformationMethod(null);
        btn.setTag(from);


                // создаем обработчик нажатия
                View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nk = v.getTag().toString();
                        showChPopupMenu(v);
                    }
                };

        // присвоим обработчик кнопке
        btn.setOnClickListener(oclBtnCmd);

        if (totop) {
            ll.addView(btn, 1);
        } else {
            ll.addView(btn);
            ScrollView scrollView1 = (ScrollView) findViewById(R.id.scrollViewChat);
            scrollView1.fullScroll(ScrollView.FOCUS_DOWN);
        }



    }

    private String nk = "";

    private void showChPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);

       //popupMenu.inflate(R.menu.popupmenu); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        popupMenu.getMenuInflater().inflate(R.menu.chat_pmenu, popupMenu.getMenu());

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        TextView tv = (TextView)  findViewById(R.id.chatToTextView);
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                String t = tv.getText().toString();
                                if (t.indexOf("Приватно ")==0) tv.setText(nk + ", ");
                                    else tv.setText(t + nk + ", ");
                                nk = tv.getText().toString();
                                return true;
                            case R.id.menu2:
                                tv.setText("Приватно " + nk);
                                nk = "!private "+nk+" ";
                                return true;
                            case R.id.menu3:
                                SendCom("05 " + nk);
                                addLog("05 " + nk);
                                nk = "";
                                tabHost.setCurrentTabByTag(TABS_TAG_1);
                                return true;
                            case R.id.menu4:
                                SendCom("chatmess !chroom? ulist");
                                addLog("chatmess !chroom? ulist");
                                return true;
                            case R.id.menu5:
                                tv.setText("");
                                EditText et = (EditText) findViewById(R.id.chatText);
                                et.setText("");
                                nk = "";
                                return true;
                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }

    public String SpecialXmlEscapeEnc(String input)
    {
        String strXmlText = input;

        int p = strXmlText.indexOf(":amp:#");
        while (p > -1) {
            try
            {
                int e = strXmlText.indexOf(";", p);
                String st = strXmlText.substring(p + 6, e);
                int c = Integer.parseInt(st);
                String pat = strXmlText.substring(p, e + 1);
                char ch = (char)c;
                String rep = String.valueOf(ch);
                strXmlText = strXmlText.replace(pat, rep);
            }
            catch (Exception ex)
            {
                int e = strXmlText.indexOf(";", p);
                String st = strXmlText.substring(p + 6, e);
                strXmlText = strXmlText.replace(":amp:#" + st + ";", "?");
            }
            p = strXmlText.indexOf(":amp:#");
        }

        return strXmlText;
    }

    public String SpecialXmlEscape(String input)
    {
        String strXmlText = "";

        /*if (string.IsNullOrEmpty(input))
            return input;*/


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); ++i)
        {
            int c = (int)input.charAt(i);
            if ((c > 47 && c < 126) || (c > 31 && c < 38) || (c > 1024 && c < 1279) || (c==42)) sb.append((char)input.charAt(i)); else sb.append(":amp:#" + c + ";");
        }

        strXmlText = sb.toString();
        //sb.clear();
        sb = null;

        return strXmlText;
    }

    private void UpdateMap(String x, String y, String code)
    {
        ImageView iv = (ImageView) findViewById(R.id.map);
        Bitmap tempBitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(tempBitmap);

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.background_l);
        tempCanvas.drawBitmap(image, 0, 0, null);

/*        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inInputShareable = true;
        options.inSampleSize = 0;
        options.inScaled = false;
        options.inPreferQualityOverSpeed = false;*/
        Paint transparentpaint = new Paint();
        transparentpaint.setAlpha(200); // 0 - 255

        if (mapC.indexOf(x + ":" + y) < 0) {
            mapC.add(x + ":" + y);
            mapP.add(code);
        }

        //16x16
        int cx = Integer.parseInt(x);
        int cy = Integer.parseInt(y);
        String tx; String ty;
        image = BitmapFactory.decodeResource(getResources(), R.drawable.s01_l);
        int rz = image.getHeight();
        int hmc = (int)(iv.getWidth()/2)-1;
        int vmc = (int)(iv.getHeight()/2);
        int hdc =  (int)(hmc/rz);
        int vdc =  (int)(vmc/rz);
        for (int i = (-1*hdc-1); i <= hdc; i++) {
            for(int j = (-1*vdc); j <= vdc+1; j++) {
                tx = Integer.toString(cx + i);
                ty = Integer.toString(cy + j);
                int pt = mapC.indexOf(tx + ":" + ty);
                if (pt > -1) {
                    Resources r = getResources();
                    try {
                        Class res = R.drawable.class;
                        Field field = res.getField(mapP.get(pt).toString()+"_l");
                        int drawableId = field.getInt(null);
                        image = BitmapFactory.decodeResource(r, drawableId);
                        //image.setPremultiplied(true);
                        //image.setHasAlpha(true);
                        tempCanvas.drawBitmap(image, hmc + i * rz, vmc - j * rz, transparentpaint);
                    }
                    catch (Exception e) {

                    }
                    //int drawableId = r.getIdentifier(mapP.get(pt).toString()+"_l", "drawable", "ru.jabbergames.sofswclient");
                }
            }
        }
        image = BitmapFactory.decodeResource(getResources(), R.drawable.s01_l);
        tempCanvas.drawBitmap(image, hmc, vmc, transparentpaint);
        iv.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

    }

    public void RespPars(String resp) {
        if(resp.indexOf("error")==0){
            addLog("Ошибка. Проверьте, пожалуйста, соединение интернет.");
            tabHost.setCurrentTabByTag(TABS_TAG_4);
        } else {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = docFactory.newDocumentBuilder();
                InputStream is = new ByteArrayInputStream(resp.getBytes("UTF-8"));
                Document document = db.parse(is);

                NodeList nodes = document.getElementsByTagName("resp");
                nodes = nodes.item(0).getChildNodes();

                for (int i = 0; i < nodes.getLength(); i++) {
                    try {
                        Element element = (Element) nodes.item(i);
                        switch (element.getNodeName()) {
                            case "mess":
                                switch (element.getAttribute("type")) {
                                    case "game":
                                        addLog("--->");
                                        ClearGLL();
                                        NodeList mnodes = nodes.item(i).getChildNodes();
                                        for (int j = 0; j < mnodes.getLength(); j++) {
                                            try {
                                                Element melement = (Element) mnodes.item(j);
                                                switch (melement.getNodeName()) {
                                                    case "text":
                                                        addLog(melement.getTextContent());
                                                        AddGText(melement.getTextContent());
                                                        NodeList lnodes = mnodes.item(j).getChildNodes();
                                                        for (int l = 0; l < lnodes.getLength(); l++) {
                                                            try {
                                                                Element gelement = (Element) lnodes.item(l);
                                                                switch (gelement.getNodeName()) {
                                                                    case "lynk":
                                                                        AddLnkButt(gelement.getAttribute("text"), gelement.getAttribute("a"));
                                                                        break;
                                                                }
                                                            } catch (Exception e) {
                                                                // TODO: handle exception
                                                            }
                                                        }
                                                        break;
                                                    case "comm":
                                                        String kay = "";
                                                        String ctxt = "";
                                                        NodeList gnodes = mnodes.item(j).getChildNodes();
                                                        for (int l = 0; l < gnodes.getLength(); l++) {
                                                            try {
                                                                Element gelement = (Element) gnodes.item(l);
                                                                switch (gelement.getNodeName()) {
                                                                    case "kay":
                                                                        kay = gelement.getTextContent();
                                                                        break;
                                                                    case "ctxt":
                                                                        ctxt = gelement.getTextContent();
                                                                        break;
                                                                }
                                                            } catch (Exception e) {
                                                                // TODO: handle exception
                                                            }
                                                        }
                                                        AddButG(kay, ctxt);
                                                        addLog(kay + "- " + ctxt);
                                                        //cont = true;
                                                        break;
                                                    case "point":
                                                        UpdateMap(melement.getAttribute("x"), melement.getAttribute("y"), melement.getAttribute("code"));
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                        }
                                    case "chat":
                                        //cont = true;
                                        String from = "";
                                        String to = "";
                                        String mtext = "";
                                        String dtime = "";
                                        int tid = 1;
                                        boolean totop = false;
                                        NodeList cmnodes = nodes.item(i).getChildNodes();
                                        for (int j = 0; j < cmnodes.getLength(); j++) {
                                            try {
                                                Element gelement = (Element) cmnodes.item(j);
                                                switch (gelement.getNodeName()) {
                                                    case "from":
                                                        from = gelement.getTextContent();
                                                        break;
                                                    case "to":
                                                        to = gelement.getTextContent();
                                                        break;
                                                    case "mtext":
                                                        mtext = SpecialXmlEscapeEnc(gelement.getTextContent());
                                                        break;
                                                    case "dtime":
                                                        dtime = gelement.getTextContent();
                                                        //if (dtime.indexOf("none") == 0) dtime = "";
                                                        break;
                                                    case "mid":
                                                        String ts = gelement.getTextContent();
                                                        tid = Integer.parseInt(ts);
                                                        if (chatminid > tid) chatminid = tid;
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                        }
                                        if((from!="")&(mtext!="")) {
                                            try {
                                                switch (element.getAttribute("totop")) {
                                                    case "1":
                                                        totop = true;
                                                        //histloadproc = false;
                                                        //callhistoryVisibility(Windows.UI.Xaml.Visibility.Collapsed);
                                                        break;
                                                    default:
                                                        totop = false;
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                totop = false;
                                            }

                                            try {
                                                switch (element.getAttribute("room")) {
                                                    case "private":
                                                        AddToChat(from, to, mtext, dtime, true, totop);
                                                        break;
                                                    default:
                                                        AddToChat(from, to, mtext, dtime, false, totop);
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                totop = false;
                                            }
                                        }
                                        break;
                                    case "chatrooms":
                                        //cont = true;
                                        ChatClearAll();
                                        NodeList crnodes = nodes.item(i).getChildNodes();
                                        for (int j = 0; j < crnodes.getLength(); j++) {
                                            try {
                                                String chnum = "";
                                                String chname = "";
                                                String des = "";
                                                String incount = "";
                                                NodeList lnodes = crnodes.item(j).getChildNodes();
                                                for (int l = 0; l < lnodes.getLength(); l++) {
                                                    try {
                                                        Element gelement = (Element) lnodes.item(l);
                                                        switch (gelement.getNodeName()) {
                                                            case "num":
                                                                chnum = gelement.getTextContent();
                                                                break;
                                                            case "name":
                                                                chname = gelement.getTextContent();
                                                                break;
                                                            case "des":
                                                                des = gelement.getTextContent();
                                                                break;
                                                            case "incount":
                                                                incount = gelement.getTextContent();
                                                                break;
                                                        }
                                                    } catch (Exception e) {
                                                        // TODO: handle exception
                                                    }
                                                }
                                                AddChatRoomB(chnum, chname, des, incount);
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                        }

                                        break;
                                    case "chatroomdes":
                                        //cont = true;
                                        String chnm = "";
                                        String chdes = "";
                                        String chincount = "";
                                        NodeList cnodes = nodes.item(i).getChildNodes();
                                        for (int j = 0; j < cnodes.getLength(); j++) {
                                            try {
                                                Element melement = (Element) cnodes.item(j);
                                                switch (melement.getNodeName()) {
                                                    case "name":
                                                        chnm = melement.getTextContent();
                                                        break;
                                                    case "des":
                                                        chdes = melement.getTextContent();
                                                        break;
                                                    case "incount":
                                                        chincount = melement.getTextContent();
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                        }
                                        AddChatRoomD(chnm, chdes, chincount);
                                        break;
                                    case "mappoints":
                                        mapP.clear();
                                        mapC.clear();
                                        NodeList mpnodes = nodes.item(i).getChildNodes();
                                        for (int j = 0; j < mpnodes.getLength(); j++) {
                                            try {
                                                Element melement = (Element) mpnodes.item(j);
                                                switch (melement.getNodeName()) {
                                                    case "mpt":
                                                        mapC.add(melement.getAttribute("x") + ":" + melement.getAttribute("y"));
                                                        mapP.add(melement.getAttribute("c"));
                                                        break;
                                                }

                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                        }
                                        break;
                                    case "shop":
                                        addLog("На данный момент данные возможности реализованы только в клиенте для Windows");
                                        break;
                                }
                                break;
                            case "Commands":
                                ClearButtc();
                                NodeList mnodes = nodes.item(i).getChildNodes();
                                for (int j = 0; j < mnodes.getLength(); j++) {
                                    try {
                                        String kay = "";
                                        String ctxt = "";
                                        NodeList gnodes = mnodes.item(j).getChildNodes();
                                        for (int l = 0; l < gnodes.getLength(); l++) {
                                            try {
                                                Element gelement = (Element) gnodes.item(l);
                                                switch (gelement.getNodeName()) {
                                                    case "kay":
                                                        kay = gelement.getTextContent();
                                                        break;
                                                    case "ctxt":
                                                        ctxt = gelement.getTextContent();
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                        }
                                        AddButC(kay, ctxt);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                                //cont = true;
                                break;
                            case "Settings":
                                //addLog("На данный момент данные возможности реализованы только в клиенте для Windows");
                                break;
                            case "perdata":
                                NodeList pnodes = nodes.item(i).getChildNodes();
                                for (int j = 0; j < pnodes.getLength(); j++) {
                                    try {
                                        Element gelement = (Element) pnodes.item(j);
                                        switch (gelement.getNodeName()) {
                                            case "pname":
                                                SetPname(gelement.getTextContent());
                                                break;
                                            case "plev":
                                                SetPlev(gelement.getAttribute("ldes"), gelement.getAttribute("lev"));
                                                break;
                                            case "php":
                                                SetPHP(gelement.getAttribute("hpdes"), gelement.getAttribute("hp"), gelement.getAttribute("hpmax"));
                                                break;
                                            case "psp":
                                                SetPSP(gelement.getAttribute("spdes"), gelement.getAttribute("sp"), gelement.getAttribute("spmax"));
                                                break;
                                            case "ppt":
                                                SetPPT(gelement.getAttribute("ptdes"), gelement.getAttribute("pt"), gelement.getAttribute("ptmax"));
                                                break;
                                            case "atten":
                                                SetAtten(gelement.getAttribute("on"));
                                                break;
                                        }
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                                //cont = true;
                                break;
                            case "error":
                                addLog(element.getNodeValue());
                                break;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                addLog("Ошибка. Возможно следует проверить соединение интернет.");
                tabHost.setCurrentTabByTag(TABS_TAG_4);
            }
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarMine);
            pb.setVisibility(View.INVISIBLE);
        }
    }



    public void SendComN(String cstr) {
        if (cstr != "") {
            //addLog("<---");
            new LongOperation() {
                @Override
                public void onPostExecute(String result) {
                    RespPars(result);
                }
            }.execute(SpecialXmlEscape(cstr));
        } else {
            addLog("---");
        }

    }


    class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String str="error";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://sofsw.jabbergames.ru/g.php");

            try
            {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("i", getDeviceId()));
                nameValuePairs.add(new BasicNameValuePair("j", params[0]));
                nameValuePairs.add(new BasicNameValuePair("v", ClVer));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);

                HttpEntity httpEntity = response.getEntity();
                str = EntityUtils.toString(httpEntity, HTTP.UTF_8);
            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
                addLog("Подключение невозможно. Проверьте, пожалуйста, соединение интернет.");
                tabHost.setCurrentTabByTag(TABS_TAG_4);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                addLog("Ошибка. Проверьте, пожалуйста, соединение интернет.");
                tabHost.setCurrentTabByTag(TABS_TAG_4);
            }

            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            //might want to change "executed" for the returned string passed into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
