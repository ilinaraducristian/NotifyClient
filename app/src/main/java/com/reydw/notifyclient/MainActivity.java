package com.reydw.notifyclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.reydw.notifyclient.actions.NotificationAction;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "NotifyDebuggingTAG";
  public static final int BLUETOOTH_REQUEST_CODE = 1;
  public static final String BLUETOOTH_UUID = "a7b99f7b-47fe-4180-b25f-3cbf556b7d9b";

  private ArrayList<BluetoothDevice> pairedBluetoothDevices;
  private BluetoothAdapter adapter;
  private RecyclerViewAdapter pairedDevicesRecyclerViewAdapter;

  private BluetoothClient bluetoothClient;
  private Thread updatePairedDevicesListTimer;
  private SharedPreferences preferences;

  private boolean arePairedDevicesUpdating = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pairedBluetoothDevices = new ArrayList<>();
    adapter = BluetoothAdapter.getDefaultAdapter();
    RecyclerView pairedDevicesRecyclerView = findViewById(R.id.pairedDevicesRecyclerView);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

//    final Window win = getWindow();
//    win.addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//      WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//    final Window win = getWindow();
//    win.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//      WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//      WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//      WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
//      WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );

    pairedDevicesRecyclerViewAdapter = new RecyclerViewAdapter(pairedBluetoothDevices) {
      @Override
      public void onDeviceSelected(int index) {
        connectToServer(pairedBluetoothDevices.get(index));
      }
    };
    pairedDevicesRecyclerView.setAdapter(pairedDevicesRecyclerViewAdapter);
    pairedDevicesRecyclerView.setLayoutManager(linearLayoutManager);

    updatePairedDevicesListTimer = new Thread(new Runnable() {
      @Override
      public void run() {
        while(arePairedDevicesUpdating) {
          listPairedDevices();
          try {
            Thread.sleep(1000);
          }catch(InterruptedException e) {
            Log.e(TAG, "updatePairedDevicesListTimer while loop", e);
          }
        }
      }
    });

    preferences = getPreferences(Context.MODE_PRIVATE);
//    SharedPreferences.Editor editor = preferences.edit();
    String lastDevice = preferences.getString("lastDevice", null);
    if(lastDevice == null) {

    }else {

    }

//    if(adapter != null) {
//      if(adapter.isEnabled()) {
//        listPairedDevices();
//      }else {
//        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST_CODE);
//      }
//    }else {
//      Toast.makeText(this, "Device doesn't have bluetooth capabilities", Toast.LENGTH_SHORT).show();
//    }

  }

  @Override
  protected void onPause() {
    super.onPause();
    arePairedDevicesUpdating = false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    switch(requestCode) {
      case BLUETOOTH_REQUEST_CODE:
        if(resultCode == -1) {
          //user granted access
          listPairedDevices();
        }else if(resultCode == 0) {
          //user denied access
          Toast.makeText(this, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
        }
        break;
    }
  }

  public void connectToServer(BluetoothDevice server) {
    bluetoothClient = new BluetoothClient(server){
      @Override
      public void onMessageReceived(NotificationAction action) {
        Log.i(TAG, "onMessageReceived: ");
        String actionName = "";
        switch(actionName) {
          case "NotificationAction":
            NotificationAction notificationAction = action;
            String appname = notificationAction.getAppname();
            String title = notificationAction.getTitle();
            break;
        }
        Log.i(TAG, "Message received: ");
        Log.i(TAG, action.toString());
      }
    };
    bluetoothClient.start();
  }

  private void listPairedDevices() {
    if(adapter == null) {
      arePairedDevicesUpdating = false;
    }else if(!adapter.isEnabled()) {
      arePairedDevicesUpdating = false;
    }else {
      Set<BluetoothDevice> pairedBluetoothDevices = adapter.getBondedDevices();
      this.pairedBluetoothDevices.clear();
      if (pairedBluetoothDevices.size() > 0) {
        this.pairedBluetoothDevices.addAll(pairedBluetoothDevices);
      }
      BluetoothDevice dv = this.pairedBluetoothDevices.get(0);
      BluetoothClass cls = dv.getBluetoothClass();
//      cls.

      pairedDevicesRecyclerViewAdapter.notifyDataSetChanged();
    }
  }

}
