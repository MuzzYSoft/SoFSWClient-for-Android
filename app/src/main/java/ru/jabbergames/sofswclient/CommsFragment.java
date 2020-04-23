package ru.jabbergames.sofswclient;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
        return v;
    }
    protected void ClearButtc() {
        if (getView() != null) {
            LinearLayout ll = (LinearLayout) getView().findViewById(R.id.ComButtLay);
            ll.removeAllViewsInLayout();
        }
    }

    protected void AddButC(String kay, String txt,View v) {
        if(!Utils.flag) {
            if (txt != "") {
                LinearLayout ll = (LinearLayout) v.findViewById(R.id.ComButtLay);
                Button btn = new Button(getActivity());
                btn.setText(txt);
                btn.setTag(kay);
                // создаем обработчик нажатия
                View.OnClickListener oclBtnCmd = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        }
    }


}