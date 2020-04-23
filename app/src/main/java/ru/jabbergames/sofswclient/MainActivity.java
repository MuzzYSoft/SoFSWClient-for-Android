package ru.jabbergames.sofswclient;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerTabStrip;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ru.jabbergames.sofswclient.ChatFragment.onSomeEventListenerCh;
import ru.jabbergames.sofswclient.CmdFragment.onSomeEventListenerCmd;
import ru.jabbergames.sofswclient.CommsFragment.onSomeEventListenerCom;
import ru.jabbergames.sofswclient.GameFragment.onSomeEventListenerGm;
public class MainActivity extends FragmentActivity implements onSomeEventListenerCh,onSomeEventListenerGm,onSomeEventListenerCom,onSomeEventListenerCmd {

    private static final int RC_SIGN_IN = 0;
    private String deviceId;
    private String ClVer = "a.1.0.7.7";
    private int STextEditID;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private boolean seeHist=false;
    PagerTabStrip titlestrip;
    int countNewMessage=0;
    String[] title = {"ИГРА", "КОМАНДЫ", "ЧАТ", "КОНСОЛЬ"};
    private List<String> ReqGm = new ArrayList<String>();
    private int tick = 0;
    ViewPager pager;
    GameFragment gmFr;
    ChatFragment chatFr;
    CommsFragment commsFr;
    CmdFragment cmdFr;
    MyPageAdapter pageAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_main);
        if(Utils.flag){
            SendCom("getcomms");
            SendCom("getmappoints");
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        List<Fragment> fragments = getFragments();
        gmFr=(GameFragment)fragments.get(0);
        commsFr=(CommsFragment)fragments.get(1);
        chatFr=(ChatFragment)fragments.get(2);
        cmdFr=(CmdFragment)fragments.get(3);
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        pager = (ViewPager)findViewById(R.id.viewpager);
        titlestrip=(PagerTabStrip)findViewById(R.id.titlestrip);
        if(!Utils.isLight){
            pager.setBackgroundResource(R.drawable.background_d);
            titlestrip.setTextColor(Color.WHITE);
            titlestrip.setBackgroundColor(Color.TRANSPARENT);
        }else{
            pager.setBackgroundResource(R.drawable.background_l);
            titlestrip.setTextColor(Color.BLACK);
            titlestrip.setBackgroundColor(Color.TRANSPARENT);
        }
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.GINGERBREAD_MR1){
        pager.setPageTransformer(true, new ZoomOutPageTransformer());}
        pager.setAdapter(pageAdapter);
        pager.setOffscreenPageLimit(4);
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();

        mTimer.schedule(mMyTimerTask, 1100, 1100);
        SendCom("0");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            setDeviceId(account.getEmail());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            cmdFr.addLog("signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    public  void isChatFr(){
        gmFr.setCountNewMessage(pager.getCurrentItem());
    }

    public void ChangeTitle(boolean light){
        if(light) {
            titlestrip.setTextColor(Color.BLACK);
            titlestrip.setBackgroundColor(Color.TRANSPARENT);
        }
        else {
            titlestrip.setTextColor(Color.TRANSPARENT);
            titlestrip.setBackgroundColor(Color.BLACK);
        }
    }
    public void setCurrentIt(int i) {
        pager.setCurrentItem(i);
    }
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(pager.getCurrentItem()==2)
                    gmFr.setCountNewMessage(2);
                    if (ReqGm.size() != 0) { //(ReqCur == ReqCnt) {
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

    public void addLog(String s){
        cmdFr.addLog(s);
    }

    private void ClearGLL() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.GameLinearLayout);
        ll.removeAllViewsInLayout();
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

    public int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
    public void RespPars(String resp) {
        if(resp.indexOf("error")==0){
            cmdFr.addLog("Ошибка. Проверьте, пожалуйста, соединение интернет.");
            //tabHost.setCurrentTabByTag(tabTags[3]);
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
                                        cmdFr.addLog("--->");
                                        ClearGLL();
                                        NodeList mnodes = nodes.item(i).getChildNodes();
                                        for (int j = 0; j < mnodes.getLength(); j++) {
                                            try {
                                                Element melement = (Element) mnodes.item(j);
                                                switch (melement.getNodeName()) {
                                                    case "text":
                                                        cmdFr.addLog(melement.getTextContent());
                                                        gmFr.AddGText(melement.getTextContent(),gmFr.getView());
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
                                                        gmFr.AddButG(kay, ctxt,gmFr.getView(),titlestrip);
                                                        cmdFr.addLog(kay + "- " + ctxt);
                                                        //cont = true;
                                                        break;
                                                    case "point":
                                                        gmFr.UpdateMap(melement.getAttribute("x"), melement.getAttribute("y"), melement.getAttribute("code"),gmFr.getView());
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
                                                        //if (chatminid > tid) chatminid = tid;
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
                                                        chatFr.AddToChat(from, to, mtext, dtime, true, totop, tid, chatFr.getView());
                                                        break;
                                                    default:
                                                        chatFr.AddToChat(from, to, mtext, dtime, false, totop, tid, chatFr.getView());
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                totop = false;
                                            }
                                        }
                                        break;
                                    case "chatrooms":
                                        //cont = true;
                                        chatFr.ChatClearAll(chatFr.getView());
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
                                                chatFr.AddChatRoomB(chnum, chname, des, incount,chatFr.getView());
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
                                        chatFr.AddChatRoomD(chnm, chdes, chincount,chatFr.getView());
                                        break;
                                    case "mappoints":
                                        Utils.mapP.clear();
                                        Utils.mapC.clear();
                                        NodeList mpnodes = nodes.item(i).getChildNodes();
                                        for (int j = 0; j < mpnodes.getLength(); j++) {
                                            try {
                                                Element melement = (Element) mpnodes.item(j);
                                                switch (melement.getNodeName()) {
                                                    case "mpt":
                                                        Utils.mapC.add(melement.getAttribute("x") + ":" + melement.getAttribute("y"));
                                                        Utils.mapP.add(melement.getAttribute("c"));
                                                        break;
                                                }

                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
                                        }
                                        break;
                                    case "shop":
                                        cmdFr.addLog("На данный момент данные возможности реализованы только в клиенте для Windows");
                                        break;
                                }
                                break;
                            case "Commands":
                                commsFr.ClearButtc();
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
                                        commsFr.AddButC(kay, ctxt,commsFr.getView());
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                                //cont = true;
                                break;
                            case "Settings":
                                if(Utils.flag) {
                                    NodeList setnodes = nodes.item(i).getChildNodes();
                                    for (int j = 0; (j < setnodes.getLength())&Utils.flag; j++) {
                                        try {
                                            Element setmelement = (Element) setnodes.item(j);
                                            switch (setmelement.getNodeName()) {
                                                case "Theme":
                                                    switch (setmelement.getTextContent()) {
                                                        case "Dark":
                                                            Utils.isLight=false;
                                                            break;
                                                        case "Light":
                                                            Utils.isLight=true;
                                                            break;
                                                    }

                                                    break;
                                                case "push_rdy":
                                                    switch (setmelement.getTextContent()) {
                                                        case "0":
                                                            Utils.toastHpIsAcc=false;
                                                            break;
                                                        case "1":
                                                            Utils.toastHpIsAcc=true;
                                                            break;
                                                    }

                                                    break;
                                                case "push_prmes":
                                                    switch (setmelement.getTextContent()) {
                                                        case "0":
                                                            Utils.toastPrMesIsAcc=false;
                                                            break;
                                                        case "1":
                                                            Utils.toastPrMesIsAcc=true;
                                                            break;
                                                    }

                                                    break;
                                                case "push_chmes":
                                                    switch (setmelement.getTextContent()) {
                                                        case "0":
                                                            Utils.toastChMesIsAcc=false;
                                                            Utils.flag=false;
                                                            if(!Utils.isLight){
                                                                Utils.changeToTheme(this,Utils.THEME_DARK);
                                                                titlestrip.setTextColor(Color.WHITE);
                                                                titlestrip.setBackgroundColor(Color.BLACK);
                                                            }
                                                            break;
                                                        case "1":
                                                            Utils.toastChMesIsAcc=true;
                                                            Utils.flag=false;
                                                            if(!Utils.isLight){
                                                                Utils.changeToTheme(this,Utils.THEME_DARK);
                                                                titlestrip.setTextColor(Color.BLACK);
                                                                titlestrip.setBackgroundColor(Color.WHITE);
                                                            }
                                                            break;
                                                    }

                                                    break;
                                            }

                                        } catch (Exception e) {
                                            // TODO: handle exception
                                        }
                                    }
                                }
                                break;
                            case "perdata":
                                NodeList pnodes = nodes.item(i).getChildNodes();
                                for (int j = 0; j < pnodes.getLength(); j++) {
                                    try {
                                        Element gelement = (Element) pnodes.item(j);
                                        switch (gelement.getNodeName()) {
                                            case "pname":
                                                gmFr.SetPname(gelement.getTextContent());
                                                break;
                                            case "plev":
                                                gmFr.SetPlev(gelement.getAttribute("ldes"), gelement.getAttribute("lev"));
                                                break;
                                            case "php":
                                                gmFr.SetPHP(gelement.getAttribute("hpdes"), gelement.getAttribute("hp"), gelement.getAttribute("hpmax"));
                                                break;
                                            case "psp":
                                                gmFr.SetPSP(gelement.getAttribute("spdes"), gelement.getAttribute("sp"), gelement.getAttribute("spmax"));
                                                break;
                                            case "ppt":
                                                gmFr.SetPPT(gelement.getAttribute("ptdes"), gelement.getAttribute("pt"), gelement.getAttribute("ptmax"));
                                                break;
                                            case "atten":
                                                gmFr.SetAtten(gelement.getAttribute("on"));
                                                break;
                                        }
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                                //cont = true;
                                break;
                            case "error":
                                cmdFr.addLog(element.getNodeValue());
                                break;
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
                cmdFr.addLog("Ошибка. Возможно следует проверить соединение интернет.");
                //tabHost.setCurrentTabByTag(tabTags[3]);
            }
            //ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarMine);
            //pb.setVisibility(View.INVISIBLE);
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
            cmdFr.addLog("---");
        }

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
                cmdFr.addLog("Подключение невозможно. Проверьте, пожалуйста, соединение интернет.");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                cmdFr.addLog("Ошибка. Проверьте, пожалуйста, соединение интернет.");
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

    public void SendCom(String comstr)
    {
        ReqGm.add(ReqGm.size(), comstr);
    }

    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<Fragment>();

        fList.add(GameFragment.newInstance("Игра"));
        fList.add(CommsFragment.newInstance("Команды"));
        fList.add(ChatFragment.newInstance("Чат"));
        fList.add(CmdFragment.newInstance("Консоль"));
        return fList;
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;


        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return this.fragments.get(0);
                case 1:
                    SendCom("getcomms");
                    return this.fragments.get(1);
                case 2:
                    countNewMessage=0;
                    gmFr.setCountNewMessage(1000);
                    return this.fragments.get(2);
                case 3:
                    return this.fragments.get(3);
                default:
                    return this.fragments.get(0);
            }
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }


    }
}