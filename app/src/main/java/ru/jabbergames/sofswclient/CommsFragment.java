package ru.jabbergames.sofswclient;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

public class CommsFragment extends Fragment {
    public interface onSomeEventListenerCom {
        public void addLog(String s);
        public void SendCom(String comstr);
        public void setCurrentIt(int i);
    }
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    onSomeEventListenerCom someEventListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListenerCom) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
    public static CommsFragment newInstance(String message)
    {
        CommsFragment f = new CommsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_com_buts, container, false);

        Switch sw = (Switch) v.findViewById(R.id.switchProtect);
        sw.setChecked(Utils.ProtectTraffic);

        return v;
    }
    protected void ClearButtc() {
        if (getView() != null) {
            LinearLayout ll = (LinearLayout) getView().findViewById(R.id.ComButtLay);
            ll.removeAllViewsInLayout();
        }
    }

    protected void AddButC(String kay, String txt,View v) {
        //if(!Utils.flag) {
            if (txt != "") {
                LinearLayout ll = (LinearLayout) v.findViewById(R.id.ComButtLay);
                Button btn = new Button(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                int marginInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 3, getResources()
                                .getDisplayMetrics());
                lp.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);
                lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
                btn.setBackgroundColor(0x98838383);
                btn.setLayoutParams(lp);
                btn.setGravity(Gravity.START);
                btn.setTransformationMethod(null);
                btn.setText(txt);
                btn.setTag(kay);
                // создаем обработчик нажатия
                View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GameFragment.HideGameView();
                        String com = (String) v.getTag();
                        someEventListener.SendCom(com);
                        someEventListener.addLog(com);
                        someEventListener.setCurrentIt(0);
                    }
                };

                // присвоим обработчик кнопке
                btn.setOnClickListener(oclBtnCmd);
                ll.addView(btn);
            }
        //}
    }


}