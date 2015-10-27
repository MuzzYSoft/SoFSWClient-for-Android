package ru.jabbergames.sofswclient;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class GameFragment extends Fragment {
    public interface onSomeEventListenerGm {
        public void addLog(String s);
        public void SendCom(String comstr);
        public int generateViewId();
        public void ChangeTitle(boolean light);
        public void setCurrentIt(int i);
    }

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private int STextEditID;
    onSomeEventListenerGm someEventListenerGm;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListenerGm = (onSomeEventListenerGm) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
    ImageButton btnGoEast;
    ImageButton btnGoSouth;
    ImageButton btnGoNorth;
    ImageButton btnGoWest;
    ImageButton btnCont;
    TextView chatMes;
    View vc;
    int countNewMessage=0;
    private boolean frstTstShw=true;
    private boolean uot;
    View.OnClickListener oclBtnCont = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refreshButtonPressed();
        }
    };

    public static final GameFragment newInstance(String message)
    {
        GameFragment f = new GameFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_game, container, false);
        btnCont = (ImageButton) v.findViewById(R.id.comBarButton0);
        // создаем обработчик нажатия
        btnGoWest = (ImageButton) v.findViewById(R.id.comBarButton1);
        btnGoNorth = (ImageButton) v.findViewById(R.id.comBarButton2);
        btnGoSouth = (ImageButton) v.findViewById(R.id.comBarButton3);
        btnGoEast = (ImageButton) v.findViewById(R.id.comBarButton4);
        chatMes=(TextView)v.findViewById(R.id.chatMes);
        View.OnClickListener oclBtnGoWest = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someEventListenerGm.SendCom("w");
                someEventListenerGm.addLog("w");
            }
        };

        // создаем обработчик нажатия
        View.OnClickListener oclBtnGoNorth = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someEventListenerGm.SendCom("n");
                someEventListenerGm.addLog("n");
            }
        };

        // создаем обработчик нажатия
        View.OnClickListener oclBtnGoSouth = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someEventListenerGm.SendCom("s");
                someEventListenerGm.addLog("s");
            }
        };

        // создаем обработчик нажатия
        View.OnClickListener oclBtnGoEast = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                someEventListenerGm.SendCom("e");
                someEventListenerGm.addLog("e");
            }
        };
        btnCont.setOnClickListener(oclBtnCont);
        btnGoWest.setOnClickListener(oclBtnGoWest);
        btnGoNorth.setOnClickListener(oclBtnGoNorth);
        btnGoSouth.setOnClickListener(oclBtnGoSouth);
        btnGoEast.setOnClickListener(oclBtnGoEast);
        return v;
    }
    protected void AddGText(String txt,View v) {
            if (txt != "") {
                if (txt.contains("бой. (")) {
                    Utils.inFight = true;
                } else {
                    Utils.inFight = false;
                }
                LinearLayout ll = (LinearLayout) v.findViewById(R.id.GameLinearLayout);
                TextView tv = new TextView(v.getContext());
                tv.setText(txt);
                ll.addView(tv);
            }
    }



    protected void AddButG(String kay, String txt,View v,PagerTabStrip titlestrip) {
        vc=v;
        if (txt != "") {
            LinearLayout ll = (LinearLayout) v.findViewById(R.id.GameLinearLayout);
            switch (kay)
            {
                case "name":
                case "X":
                case "N":
                case "Х":
                    //добавить поле ввода
                    EditText et = new EditText(getActivity());
                    et.setHint(txt);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        STextEditID = someEventListenerGm.generateViewId();
                    } else {
                        STextEditID = View.generateViewId();
                    }
                    et.setId(STextEditID);
                    ll.addView(et);

                    // создаем обработчик нажатия
                    View.OnClickListener oclBtnCmdS = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                EditText mCmdText = (EditText) vc.findViewById(STextEditID);
                                String scom = mCmdText.getText().toString();
                                if (scom == "") {
                                    scom = "0";
                                }
                                someEventListenerGm.SendCom(scom);
                                someEventListenerGm.addLog(scom);
                                mCmdText.setText("");
                                btnCont.setOnClickListener(oclBtnCont);
                        }
                        ;
                    };
                    // присвоим обработчик кнопке
                    btnCont.setOnClickListener(oclBtnCmdS);

                    Button btnok = new Button(getActivity());
                    btnok.setText(getString(R.string.OK));
                    btnok.setOnClickListener(oclBtnCmdS);
                    ll.addView(btnok);
                    break;
                default:
                    Button btn = new Button(getActivity());
                    btn.setText(txt);
                    btn.setTag(kay);
                    btn.setTransformationMethod(null);
                    if(kay.contains("swtheme")) {
                        if (txt.contains("Выбрана: Светлая тема")) {
                            // создаем обработчик нажатия
                            View.OnClickListener oclBtnCmdd = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.changeToTheme(getActivity(), Utils.THEME_DARK);
                                    someEventListenerGm.ChangeTitle(false);
                                    String com = (String) v.getTag();
                                    someEventListenerGm.SendCom(com);
                                    someEventListenerGm.addLog(com);
                                    //tabHost.setCurrentTabByTag(tabTags[0]);
                                }
                            };
                            btn.setOnClickListener(oclBtnCmdd);
                        } else{
                            // создаем обработчик нажатия
                            View.OnClickListener oclBtnCmdd = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.changeToTheme(getActivity(), Utils.THEME_LIGHT);
                                    someEventListenerGm.ChangeTitle(true);
                                    String com = (String) v.getTag();
                                    someEventListenerGm.SendCom(com);
                                    someEventListenerGm.addLog(com);
                                    //tabHost.setCurrentTabByTag(tabTags[0]);
                                }
                            };
                            btn.setOnClickListener(oclBtnCmdd);
                        }
                    }else if(kay.contains("swpush_rdy")) {
                        if(txt.contains("выключены"))
                        {
                            // создаем обработчик нажатия
                            View.OnClickListener oclBtnCmdd = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.toastHpIsAcc=true;
                                    String com = (String) v.getTag();
                                    someEventListenerGm.SendCom(com);
                                    someEventListenerGm.addLog(com);
                                    //tabHost.setCurrentTabByTag(tabTags[0]);
                                }
                            };
                            btn.setOnClickListener(oclBtnCmdd);
                        }
                        else{
                            // создаем обработчик нажатия
                            View.OnClickListener oclBtnCmdd = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.toastHpIsAcc=false;
                                    String com = (String) v.getTag();
                                    someEventListenerGm.SendCom(com);
                                    someEventListenerGm.addLog(com);
                                    //tabHost.setCurrentTabByTag(tabTags[0]);
                                }
                            };
                            btn.setOnClickListener(oclBtnCmdd);
                        }
                    }else if(kay.contains("swpush_prmes")) {
                        if(txt.contains("выключены"))
                        {
                            // создаем обработчик нажатия
                            View.OnClickListener oclBtnCmdd = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.toastPrMesIsAcc=true;
                                    String com = (String) v.getTag();
                                    someEventListenerGm.SendCom(com);
                                    someEventListenerGm.addLog(com);
                                    //tabHost.setCurrentTabByTag(tabTags[0]);
                                }
                            };
                            btn.setOnClickListener(oclBtnCmdd);
                        }
                        else{
                            // создаем обработчик нажатия
                            View.OnClickListener oclBtnCmdd = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.toastPrMesIsAcc=false;
                                    String com = (String) v.getTag();
                                    someEventListenerGm.SendCom(com);
                                    someEventListenerGm.addLog(com);
                                    //tabHost.setCurrentTabByTag(tabTags[0]);
                                }
                            };
                            btn.setOnClickListener(oclBtnCmdd);
                        }
                    }
                    else{

                        // создаем обработчик нажатия
                        View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String com = (String) v.getTag();
                                someEventListenerGm.SendCom(com);
                                someEventListenerGm.addLog(com);
                            }
                        };
                        // присвоим обработчик кнопке
                        btn.setOnClickListener(oclBtnCmd);
                        if(Utils.inFight){
                            btnCont.setTag(kay);
                            // создаем обработчик нажатия
                            View.OnClickListener oclBtnCmddd = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utils.inFight=false;
                                    String com = (String) v.getTag();
                                    someEventListenerGm.SendCom(com);
                                    someEventListenerGm.addLog(com);
                                }
                            };
                            btnCont = (ImageButton) v.findViewById(R.id.comBarButton0);
                            btnCont.setOnClickListener(oclBtnCmddd);
                        }else{
                            btnCont = (ImageButton) v.findViewById(R.id.comBarButton0);
                            btnCont.setOnClickListener(oclBtnCont);
                        }
                    }
                    ll.addView(btn);
                    break;
            }
        }
    }

    protected void SetPname(String nm) {
        if (getView() != null) {
        TextView tv = (TextView) getView().findViewById(R.id.player_name_text);
        tv.setText(nm);
    }}

    protected void SetPlev(String de, String lv) {
        if (getView() != null) {
            TextView tv = (TextView) getView().findViewById(R.id.player_lev_text);
            tv.setText("  " + de + lv);
        }
    }

    protected void SetPHP(String hpdes, String hp, String hpmax) {
        if (getView() != null) {
            TextView tv = (TextView) getView().findViewById(R.id.progress_hp_text);
            tv.setText(hpdes + hp + "/" + hpmax);
            if (Utils.toastHpIsAcc) {
                if (hp.equals(hpmax) & uot) {
                    uot = false;
                    Toast toastPriv = Toast.makeText(getActivity().getApplicationContext(),
                            "Жизни героя восстановлены!", Toast.LENGTH_SHORT);
                    toastPriv.setGravity(Gravity.BOTTOM, 0, 0);
                    toastPriv.show();
                } else if (!hp.equals(hpmax)) {
                    uot = true;
                }
            }
            ProgressBar pb = (ProgressBar) getView().findViewById(R.id.progressBarHP);
            pb.setMax(Integer.parseInt(hpmax));
            pb.setProgress(Integer.parseInt(hp));
        }
    }

    protected void SetPSP(String spdes, String sp, String spmax) {
        if (getView() != null) {
            TextView tv = (TextView) getView().findViewById(R.id.progress_sp_text);
            tv.setText(spdes + sp + "/" + spmax);
            ProgressBar pb = (ProgressBar) getView().findViewById(R.id.progressBarSP);
            pb.setMax(Integer.parseInt(spmax));
            pb.setProgress(Integer.parseInt(sp));
        }
    }

    protected void SetPPT(String ptdes, String pt, String ptmax) {
        if (getView() != null) {
            String shpt = pt;
            if (pt.length() > 5) {
                shpt = pt.substring(0, pt.length() - 3) + "k";
            }
            TextView tv = (TextView) getView().findViewById(R.id.progress_pt_text);
            Integer i = Integer.parseInt(ptmax) - Integer.parseInt(pt);
            tv.setText(ptdes + shpt + "/" + i.toString());  // Convert.ToString(Convert.ToInt32(ptmax) - Convert.ToInt32(pt));
            ProgressBar pb = (ProgressBar) getView().findViewById(R.id.progressBarPT);
            pb.setMax(Integer.parseInt(ptmax));
            pb.setProgress(Integer.parseInt(pt));
        }
    }

    protected void SetAtten(String p) {
        if (getView() != null) {
            TextView tv = (TextView) getView().findViewById(R.id.player_atten_text);
            if (p.contains("1")) {
                tv.setText(getString(R.string.level_up_atten));
            } else {
                tv.setText("");
            }
        }
    }
    protected void UpdateMap(String x, String y, String code,View v) {
            ImageView iv = (ImageView) v.findViewById(R.id.map);
            Bitmap prom;
            Bitmap tempBitmap = Bitmap.createBitmap(iv.getWidth(), iv.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas tempCanvas = new Canvas(tempBitmap);
            Bitmap image;
            if (Utils.isLight) {
                image = BitmapFactory.decodeResource(getResources(), R.drawable.background_l);
                prom = Bitmap.createScaledBitmap(image, iv.getWidth(), iv.getHeight(), false);
                tempCanvas.drawBitmap(prom, 0, 0, null);
            } else {
                image = BitmapFactory.decodeResource(getResources(), R.drawable.background_d);
                prom = Bitmap.createScaledBitmap(image, iv.getWidth(), iv.getHeight(), false);
                tempCanvas.drawBitmap(prom, 0, 0, null);
            }
/*        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inInputShareable = true;
        options.inSampleSize = 0;
        options.inScaled = false;
        options.inPreferQualityOverSpeed = false;*/
            Paint transparentpaint = new Paint();
            transparentpaint.setAlpha(200); // 0 - 255

            if (Utils.mapC.indexOf(x + ":" + y) < 0) {
                Utils.mapC.add(x + ":" + y);
                Utils.mapP.add(code);
            }

            //16x16
            int cx = Integer.parseInt(x);
            int cy = Integer.parseInt(y);
            String tx;
            String ty;
            image = BitmapFactory.decodeResource(getResources(), R.drawable.s01_l);
            int rz = image.getHeight();
            int hmc = (int) (iv.getWidth() / 2) - 1;
            int vmc = (int) (iv.getHeight() / 2);
            int hdc = (int) (hmc / rz);
            int vdc = (int) (vmc / rz);
            for (int i = (-1 * hdc - 1); i <= hdc; i++) {
                for (int j = (-1 * vdc); j <= vdc + 1; j++) {
                    tx = Integer.toString(cx + i);
                    ty = Integer.toString(cy + j);
                    int pt = Utils.mapC.indexOf(tx + ":" + ty);
                    if (pt > -1) {
                        Resources r = getResources();
                        String gogo;
                        if (Utils.isLight) {
                            gogo = "_l";
                        } else {
                            gogo = "_d";
                        }
                        try {
                            Class res = R.drawable.class;
                            Field field = res.getField(Utils.mapP.get(pt) + gogo);
                            int drawableId = field.getInt(null);
                            image = BitmapFactory.decodeResource(r, drawableId);
                            //image.setPremultiplied(true);
                            //image.setHasAlpha(true);
                            tempCanvas.drawBitmap(image, hmc + i * rz, vmc - j * rz, transparentpaint);
                        } catch (Exception e) {

                        }
                        //int drawableId = r.getIdentifier(mapP.get(pt).toString()+"_l", "drawable", "ru.jabbergames.sofswclient");
                    }
                }
            }
            image = BitmapFactory.decodeResource(getResources(), R.drawable.s01_l);
            tempCanvas.drawBitmap(image, hmc, vmc, transparentpaint);
            iv.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

        }
    protected void refreshButtonPressed(){
        if(frstTstShw) {
            frstTstShw=false;
            Toast toastPriv = Toast.makeText(getActivity().getApplicationContext(),
                    "Вы нажали кнопку ОК (Enter)", Toast.LENGTH_SHORT);
            toastPriv.setGravity(Gravity.BOTTOM, -30, 0);
            toastPriv.show();
        }
        someEventListenerGm.SendCom("0");
        someEventListenerGm.addLog("0");
    }
    protected void setCountNewMessage(int position) {
        if (position == 2) {
            chatMes.setText("");
            countNewMessage = 0;
        } else countNewMessage += 1;
        if(chatMes!=null) {
            if (countNewMessage != 0) {
                String prom = getString(R.string.ChatMessCounter) + String.valueOf(countNewMessage);
                chatMes.setText(prom);
            } else chatMes.setText("");
        }
    }
}
