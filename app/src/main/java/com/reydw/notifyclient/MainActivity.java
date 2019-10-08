package com.reydw.notifyclient;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
  private Button refreshButton;
  private RecyclerViewAdapter pairedDevicesRecyclerViewAdapter;

  private BluetoothClient bluetoothClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pairedBluetoothDevices = new ArrayList<>();
    adapter = BluetoothAdapter.getDefaultAdapter();
    refreshButton = findViewById(R.id.refreshButton);
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

    final Activity self = this;

    refreshButton.setEnabled(true);
    refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        bluetoothClient.sendMessage("foobar".getBytes());
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        final PowerManager.WakeLock wl = pm.newWakeLock( PowerManager.ON_AFTER_RELEASE | PowerManager.ACQUIRE_CAUSES_WAKEUP, "NotifyClient::WLTAG");
      }
    });

    if(adapter != null) {
      if(adapter.isEnabled()) {
        refreshButton.setEnabled(true);
        listPairedDevices();
      }else {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetoothIntent, BLUETOOTH_REQUEST_CODE);
      }
    }else {
      Toast.makeText(this, "Device doesn't have bluetooth capabilities", Toast.LENGTH_SHORT).show();
    }

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
            NotificationAction notificationAction = (NotificationAction)action;
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
    refreshButton.setEnabled(false);
    Set<BluetoothDevice> pairedBluetoothDevices = adapter.getBondedDevices();
    this.pairedBluetoothDevices.clear();
    if (pairedBluetoothDevices.size() > 0) {
      this.pairedBluetoothDevices.addAll(pairedBluetoothDevices);
    }
    pairedDevicesRecyclerViewAdapter.notifyDataSetChanged();
    refreshButton.setEnabled(true);
  }

}
