package net.kccistc.AiotClient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


public class Fragment2Dashboard extends Fragment {
  MainActivity mainActivity;
  Switch switchLed;
  Switch switchBlinds;
  Switch switchAir;
  TextView textViewIllumination;
  TextView textViewTemp;
  TextView textViewHumi;
  ImageView imageViewLed;
  ImageView imageViewBlinds;
  ImageView imageViewAir;
  Button buttonCondition;
  Button buttonControl;
  boolean switchLedFlag = false;
  boolean switchBlindsFlag = false;
  boolean switchAirFlag = false;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment2dashboard, container, false);
    mainActivity = (MainActivity) getActivity();
    textViewIllumination = view.findViewById(R.id.textViewIllumination);
    textViewTemp = view.findViewById(R.id.textViewTemp);
    textViewHumi = view.findViewById(R.id.textViewHumi);
    imageViewLed = view.findViewById(R.id.imageViewLed);
    imageViewBlinds = view.findViewById(R.id.imageViewBlinds);
    imageViewAir = view.findViewById(R.id.imageViewAir);

    buttonCondition = view.findViewById(R.id.buttonCondition);
    buttonCondition.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (ClientThread.socket != null) {
          mainActivity.clientThread.sendData(ClientThread.arduinoId + "GETSENSOR");
        } else
          Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
      }
    });
    buttonControl = view.findViewById(R.id.buttonControl);
    buttonControl.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (ClientThread.socket != null) {
          mainActivity.clientThread.sendData(ClientThread.arduinoId + "GETSW");
        } else
          Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
      }
    });
    switchLed = view.findViewById(R.id.switchLed);
    switchLed.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (ClientThread.socket != null) {
          if (switchLed.isChecked()) {
            mainActivity.clientThread.sendData(ClientThread.arduinoId + "LAMPON");
            //                       switchLed.setChecked(false);
          } else {
            mainActivity.clientThread.sendData(ClientThread.arduinoId + "LAMPOFF");
            //                       switchLed.setChecked(true);
          }
        } else
          Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
      }
    });

    switchBlinds = view.findViewById(R.id.switchBlinds);
    switchBlinds.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (ClientThread.socket != null) {
          if (switchBlinds.isChecked()) {
            mainActivity.clientThread.sendData(ClientThread.arduinoId + "GASON");
            switchBlinds.setChecked(false);
          } else {
            mainActivity.clientThread.sendData(ClientThread.arduinoId + "GASOFF");
            switchBlinds.setChecked(true);
          }
        } else
          Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
      }
    });
    switchAir = view.findViewById(R.id.switchAir);
    switchAir.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (ClientThread.socket != null) {
          if (switchAir.isChecked()) {
            mainActivity.clientThread.sendData(ClientThread.arduinoId + "AIRON");
            switchAir.setChecked(false);
          } else {
            mainActivity.clientThread.sendData(ClientThread.arduinoId + "AIROFF");
            switchAir.setChecked(true);
          }
        } else
          Toast.makeText(getActivity(), "login 확인", Toast.LENGTH_SHORT).show();
      }
    });
    return view;
  }

  public void recvDataProcess(String recvData) {
    String[] splitLists = recvData.toString().split("\\[|]|@|\\n");
    if (splitLists[2].equals("GETSW")) {
      if (splitLists[3].equals("0"))
        imageButtonUpdate("LAMPOFF");
      else if (splitLists[3].equals("1")) {
        imageButtonUpdate("LAMPON");
      }
      if (splitLists[4].equals("0"))
        imageButtonUpdate("GASOFF");
      else if (splitLists[4].equals("1"))
        imageButtonUpdate("GASON");
      if (splitLists[4].equals("0"))
        imageButtonUpdate("AIROFF");
      else if (splitLists[4].equals("1"))
        imageButtonUpdate("AIRON");
    } else if (splitLists[2].equals("SENSOR"))
      updateTextView(splitLists[3], splitLists[4], splitLists[5]);
    else
      imageButtonUpdate(splitLists[2]);
  }

  public void imageButtonUpdate(String cmd) {
    if (cmd.equals("LAMPON")) {
      imageViewLed.setImageResource(R.drawable.led_on);
      switchLed.setChecked(true);
      switchLedFlag = true;
    } else if (cmd.equals("LAMPOFF")) {
      imageViewLed.setImageResource(R.drawable.led_off);
      switchLed.setChecked(false);
      switchLedFlag = false;
    } else if (cmd.equals("GASON")) {
      imageViewBlinds.setImageResource(R.drawable.blinds_on);
      switchBlinds.setChecked(true);
      switchBlindsFlag = true;
    } else if (cmd.equals("GASOFF")) {
      imageViewBlinds.setImageResource(R.drawable.blinds_off);
      switchBlinds.setChecked(false);
      switchBlindsFlag = false;
    } else if (cmd.equals("AIRON")) {
      imageViewAir.setImageResource(R.drawable.air_on);
      switchAir.setChecked(true);
      switchAirFlag = true;
    } else if (cmd.equals("AIROFF")) {
      imageViewAir.setImageResource(R.drawable.air_off);
      switchAir.setChecked(false);
      switchAirFlag = true;
    }
  }

  public void updateTextView(String cds, String temp, String humi) {
    textViewIllumination.setText(cds + "%");    //Illumination
    textViewTemp.setText(temp + "℃");          //Temperature
    textViewHumi.setText(humi + "%");          //humidity
  }
}
