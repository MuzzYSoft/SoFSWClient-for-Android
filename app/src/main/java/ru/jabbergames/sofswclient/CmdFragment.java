package ru.jabbergames.sofswclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class CmdFragment extends Fragment {
    public interface onSomeEventListenerCmd {
        public void SendCom(String comstr);
    }

    onSomeEventListenerCmd someEventListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListenerCmd) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }
    View vv;
    Button btnCmdSend;
    Button btnCmdClear;
    public static final CmdFragment newInstance(String message)
    {
        CmdFragment f = new CmdFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_cmd, container, false);
        vv=v;
        btnCmdSend = (Button) v.findViewById(R.id.cmdSendButton);
        btnCmdClear=(Button)v.findViewById(R.id.cmdClearButton);
        // создаем обработчик нажатия
        View.OnClickListener oclBtnCmd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mCmdText = (EditText) vv.findViewById(R.id.cmdText);
                String scom = mCmdText.getText().toString();
                if (scom != "") {
                    someEventListener.SendCom(mCmdText.getText().toString());
                    addLog(mCmdText.getText().toString());
                }
                mCmdText.setText("");
            }
        };
        // создаем обработчик нажатия для кнопки очистить
        View.OnClickListener oclBtnCmdClr = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView mCmdText = (TextView)vv.findViewById(R.id.logTextView);
                if (mCmdText != null) {
                    mCmdText.setText("");
                }
            }
        };
        // присвоим обработчик кнопке
        btnCmdClear.setOnClickListener(oclBtnCmdClr);
        btnCmdSend.setOnClickListener(oclBtnCmd);

        //авторпрокрутка
        TextView textView1 = (TextView) v.findViewById(R.id.logTextView);
        textView1.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

                ScrollView scrollView1 = (ScrollView) vv.findViewById(R.id.scrollViewCons);
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


        return v;
    }
    public void addLog(String addstr) {
        if (getView() != null) {
            TextView mCmdText = (TextView) getView().findViewById(R.id.logTextView);
            if (mCmdText != null) {
                mCmdText.append("\n\r" + addstr);
            }
        }
    }

}