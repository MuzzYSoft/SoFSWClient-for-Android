package ru.jabbergames.sofswclient;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.PopupMenu;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

public class ChatFragment extends Fragment {
    public interface onSomeEventListenerCh {
        public void addLog(String s);
        public void SendCom(String comstr);
        public void setCurrentIt(int i);
        void isChatFr();
    }
    View vc;
    onSomeEventListenerCh someEventListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListenerCh) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
    int chatminid = 2147483647;

    public static final ChatFragment newInstance(String message)
    {
        ChatFragment f = new ChatFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_chat, container, false);
        vc=v;
        someEventListener.SendCom("chatmess !chroom? descr");
        someEventListener.addLog("chatmess !chroom? descr");
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.chatContent);
        //if (ll.getChildCount() == 0) someEventListener.SendCom("chatmess !history");
        Button btn = (Button) v.findViewById(R.id.chatRoomSelButt);
        // создаем обработчик нажатия
        View.OnClickListener oclBtnCmd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout) vc.findViewById(R.id.chatContent);
                ll.removeAllViewsInLayout();
                someEventListener.SendCom("chatmess !chroom? list");
                someEventListener.addLog("chatmess !chroom? list");
            }
        };
        // присвоим обработчик кнопке
        btn.setOnClickListener(oclBtnCmd);
        Button btncs = (Button) v.findViewById(R.id.chatSendButton);
        // создаем обработчик нажатия
        View.OnClickListener oclCBtnCmd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) vc.findViewById(R.id.chatText);
                TextView tv = (TextView) vc.findViewById(R.id.chatToTextView);
                String disp = tv.getText().toString();
                if (disp.length() == 0){
                    nk = "";
                }
                if (et.getText().length() > 0) {
                    someEventListener.SendCom("chatmess " + nk + et.getText().toString());
                    someEventListener.addLog("chatmess " + nk + et.getText().toString());
                    et.setText("");
                }
                Utils.seeHist=false;
            }
        };
        // присвоим обработчик кнопке
        btncs.setOnClickListener(oclCBtnCmd);
        return v;
    }

    protected void AddChatRoomD(String chnm, String chdes, String chincount,View v) {
            TextView tv = (TextView) v.findViewById(R.id.chatRoomName);
            tv.setText(chnm);
            tv = (TextView) v.findViewById(R.id.chatRoomCount);
            tv.setText(getString(R.string.room_cnt) + chincount + getString(R.string.room_cnt_p));
            tv = (TextView) v.findViewById(R.id.chatRoomDescr);
            tv.setText(chdes);
    }

    protected void AddChatRoomB(String chnum, String chname, String des, String incount,View v) {
            LinearLayout ll = (LinearLayout) v.findViewById(R.id.chatContent);
            Button btn = new Button(getActivity());
            String prom = "Комната: " + chname + "  {" + incount + getString(R.string.room_cnt_p) + "}\n" + des;
            btn.setText(prom);
            btn.setTag("chatmess !chroom! " + chnum);
            btn.setTransformationMethod(null);
            // создаем обработчик нажатия
            View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String com = (String) v.getTag();
                    someEventListener.SendCom(com);
                    someEventListener.addLog(com);
                    LinearLayout ll = (LinearLayout) vc.findViewById(R.id.chatContent);
                    ll.removeAllViewsInLayout();
                    chatminid = 2147483647;
                }
            };

            // присвоим обработчик кнопке
            btn.setOnClickListener(oclBtnCmd);

            ll.addView(btn);
    }

    protected void AddToChat(String from, String to, String message, String dtime, boolean priv, boolean totop, int tid, View v) throws IOException {

            String imageUrl;

            LinearLayout ll = (LinearLayout) v.findViewById(R.id.chatContent);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int marginInDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 3, getResources()
                            .getDisplayMetrics());
            lp.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT;

            if (chatminid > tid) chatminid = tid;
            Utils.lastMessId = Utils.lastMessId < tid ? tid:Utils.lastMessId;

            // вставка картинки
            int start = message.indexOf("{img=");
            if(start >= 0){
                int end = message.indexOf("}");
                imageUrl = message.substring(start + 5, end);

                ImageView iv = (ImageView) new ImageView(getActivity());
                Picasso.get().load(imageUrl).into(iv);

                iv.setLayoutParams(lp);
                ll.addView(iv);

                message = message.substring(0, start==0?0:start - 1) + message.substring(end + 1);
            }

            //кнопка загрузки истории
            if (ll.getChildCount() > 0) {
                Button ghbtn = (Button) ll.getChildAt(0);
                if (!ghbtn.getTag().toString().equals("ghbtn")) {
                    ghbtn = new Button(getActivity());
                    ghbtn.setBackgroundColor(0x3E838383);
                    ghbtn.setLayoutParams(lp);
                    ghbtn.setGravity(Gravity.START);
                    ghbtn.setTransformationMethod(null);
                    ghbtn.setText("старые сообщения");
                    ghbtn.setTag("ghbtn");
                    // создаем обработчик нажатия
                    View.OnClickListener oclBtnGhbtn = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LinearLayout ll = (LinearLayout) vc.findViewById(R.id.chatContent);
                            TextView tv = new TextView(getActivity());
                            tv.setText("=================");
                            tv.setGravity(Gravity.CENTER_HORIZONTAL);
                            ll.addView(tv, 1);
                            Utils.seeHist = true;
                            someEventListener.SendCom("chatmess !history " + Integer.toString(chatminid));
                            someEventListener.addLog("chatmess !history " + Integer.toString(chatminid));
                        }
                    };
                    // присвоим обработчик кнопке
                    ghbtn.setOnClickListener(oclBtnGhbtn);
                    ll.addView(ghbtn, 0);
                }

            }

            // местное время
            String shou = "";
            String smin = "";
            if (!dtime.equals("none")) {
                Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+5"));
                int gmtOffset = TimeZone.getDefault().getRawOffset() - TimeZone.getTimeZone("GMT+5").getRawOffset();

                int index = dtime.indexOf(":");
                smin = dtime.substring(index + 1, 5);
                shou = dtime.substring(0, index);
                int min = Integer.parseInt(smin);
                int hou = Integer.parseInt(shou);

                currentCalendar.set(Calendar.HOUR_OF_DAY, hou);
                currentCalendar.set(Calendar.MINUTE, min);

                currentCalendar.setTimeInMillis(currentCalendar.getTimeInMillis() + gmtOffset);

                hou = (int) currentCalendar.get(Calendar.HOUR_OF_DAY);
                min = (int) currentCalendar.get(Calendar.MINUTE);

                if (min < 10) {
                    shou = Integer.toString(hou) + ":0" + Integer.toString(min);
                } else {
                    shou = Integer.toString(hou) + ":" + Integer.toString(min);
                }
            }

            Button btn = new Button(getActivity());
            btn.setBackgroundColor(0x3E838383);
            btn.setLayoutParams(lp);
            btn.setGravity(Gravity.START);
            btn.setTransformationMethod(null);
            someEventListener.isChatFr();

            if (priv) {
                if (Utils.toastPrMesIsAcc) {
                    String ffrom;
                    ffrom = from;
                    Toast toastPriv = Toast.makeText(getActivity().getApplicationContext(),
                            "Приватное сообщение от " + ffrom, Toast.LENGTH_SHORT);
                    toastPriv.setGravity(Gravity.BOTTOM, 0, 0);
                    toastPriv.show();
                }
                btn.setText(shou + " приватно от " + from + ":\n\r" + message);
                btn.setTextColor(Color.parseColor("#ccff0e15"));
            } else {
                String prom=shou+ " " + from + ":\n" + message;
                btn.setText(prom);
            }
            btn.setGravity(Gravity.START);
            btn.setTransformationMethod(null);
            btn.setTag(from);

            // создаем обработчик нажатия
            View.OnClickListener oclBtnCmdddd = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    nk = v.getTag().toString();
                    showChPopupMenu(v);
                }
            };

            // присвоим обработчик кнопке
            btn.setOnClickListener(oclBtnCmdddd);

            if (totop) {
                ll.addView(btn, 1);
            } else {
                ll.addView(btn);
                if (!Utils.seeHist) {
                    final ScrollView sv = (ScrollView) v.findViewById(R.id.scrollViewChat);
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            }
    }

    private String nk = "";
    protected void showChPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);

        //popupMenu.inflate(R.menu.popupmenu); // Для Android 4.0
        // для версии Android 3.0 нужно использовать длинный вариант
        popupMenu.getMenuInflater().inflate(R.menu.chat_pmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                    TextView tv = (TextView) vc.findViewById(R.id.chatToTextView);
                    switch (item.getItemId()) {
                        case R.id.menu1:
                            String t = tv.getText().toString();
                            String prom;
                            if (t.indexOf("Приватно ") == 0) {
                                prom=nk + ", ";
                                tv.setText(prom);
                            }
                            else {
                                prom=t+ nk + ", ";
                                tv.setText(prom);
                            }
                            nk = tv.getText().toString();
                            return true;
                        case R.id.menu2:
                            tv.setText("Приватно " + nk);
                            nk = "!private " + nk + " ";
                            return true;
                        case R.id.menu3:
                            GameFragment.HideGameView();
                            someEventListener.SendCom("05 " + nk);
                            someEventListener.addLog("05 " + nk);
                            nk = "";
                            int i=0;
                            someEventListener.setCurrentIt(i);
                            return true;
                        case R.id.menu4:
                            someEventListener.SendCom("chatmess !chroom? ulist");
                            someEventListener.addLog("chatmess !chroom? ulist");
                            return true;
                        case R.id.menu5:
                            tv.setText("");
                            EditText et = (EditText) vc.findViewById(R.id.chatText);
                            et.setText("");
                            nk = "";
                            return true;
                        default:
                            nk = "";
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

    protected void ChatClearAll(View v) {
        LinearLayout ll = (LinearLayout) v.findViewById(R.id.chatContent);
        ll.removeAllViewsInLayout();
    }
}