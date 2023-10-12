package net.kccistc.AiotClient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment4WebvView extends Fragment {

  ClientThread clientThread;
  ToggleButton toggleButtonConnect;
  Button buttonSend;
  TextView textViewRecv;
  ScrollView scrollViewRecv;
  SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm:ss");

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment4webview, container, false);

    EditText editTextIp = view.findViewById(R.id.editTextTextIp);
    EditText editTextPort = view.findViewById(R.id.editTextTextPort);
    scrollViewRecv = view.findViewById(R.id.scrollViewRecv);
    textViewRecv = view.findViewById(R.id.textViewRecv);
    toggleButtonConnect = view.findViewById(R.id.toggleButtonConnect);

    EditText editTextSend = view.findViewById(R.id.editTextTextSend);
    buttonSend = view.findViewById(R.id.buttonSend);
    buttonSend.setEnabled(false);
    toggleButtonConnect.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        if (toggleButtonConnect.isChecked()) {
          String Ip = editTextIp.getText().toString();
          int Port = Integer.parseInt(editTextPort.getText().toString());
          clientThread = new ClientThread(Ip, Port);
          clientThread.start();
          toggleButtonConnect.setChecked(false);
        } else {
          clientThread.stopClient();
          buttonSend.setEnabled(false);
//                    toggleButtonConnect.setChecked(true);
        }
      }
    });
    buttonSend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String strSend = editTextSend.getText().toString();
        clientThread.sendData(strSend);
        editTextSend.setText("");
      }
    });
    return view;
  }

  void recvDataProcess(String data) {
    Date date = new Date();
    String strDate = dateFormat.format(date);
    strDate = strDate + " " + data;
    textViewRecv.append(strDate);
    scrollViewRecv.fullScroll(View.FOCUS_DOWN);
  }

  void newConnectedButtonEnable() {
    toggleButtonConnect.setChecked(true);
    buttonSend.setEnabled(true);
  }
}