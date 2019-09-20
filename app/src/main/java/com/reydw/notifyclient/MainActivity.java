package com.reydw.notifyclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

  public static final String TAG = "BLUETOOTH";
  public static final int BLUETOOTH_REQUEST_CODE = 1;
  public static final String BLUETOOTH_UUID = "a7b99f7b-47fe-4180-b25f-3cbf556b7d9b";

  private ArrayList<BluetoothDevice> pairedBluetoothDevices;
  private BluetoothAdapter adapter;
  private Button refreshButton;
  private RecyclerView pairedDevicesRecyclerView;
  private RecyclerViewAdapter pairedDevicesRecyclerViewAdapter;
  private LinearLayoutManager linearLayoutManager;

  private BluetoothClient bluetoothClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pairedBluetoothDevices = new ArrayList<>();
    adapter = BluetoothAdapter.getDefaultAdapter();
    refreshButton = findViewById(R.id.refreshButton);
    pairedDevicesRecyclerView = findViewById(R.id.pairedDevicesRecyclerView);
    linearLayoutManager = new LinearLayoutManager(this);

    pairedDevicesRecyclerViewAdapter = new RecyclerViewAdapter(pairedBluetoothDevices) {
      @Override
      public void onDeviceSelected(int index) {
        connectToServer(pairedBluetoothDevices.get(index));
      }
    };
    pairedDevicesRecyclerView.setAdapter(pairedDevicesRecyclerViewAdapter);
    pairedDevicesRecyclerView.setLayoutManager(linearLayoutManager);

    refreshButton.setEnabled(false);
    refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        bluetoothClient.sendMessage("foobar".getBytes());
      }
    });

    if(adapter != null) {
      refreshButton.setEnabled(true);
      if(adapter.isEnabled()) {
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
      public void onMessageReceived(byte[] bytes) {
        Log.i(TAG, "Message received: " + new String(bytes));
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
