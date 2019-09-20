package com.reydw.notifyclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class BluetoothClient extends Thread {

  private static final String TAG = MainActivity.TAG;
  private static final String BLUETOOTH_UUID = MainActivity.BLUETOOTH_UUID;
  private BluetoothSocket server;
  private InputStream is;
  private OutputStream os;

  BluetoothClient(BluetoothDevice server) {
    try {
      this.server = server.createRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID));
      this.server.connect();
      is = this.server.getInputStream();
      os = this.server.getOutputStream();
    } catch (IOException e) {
      Log.e(TAG, "BluetoothClient constructor()", e);
    }
  }

  @Override
  public void run() {
    if(server == null) return;
    while(true) {
      byte[] bytes = new byte[128];
      try {
        is.read(bytes);
        onMessageReceived(bytes);
      } catch (IOException e) {
        Log.e(TAG, "BluetoothClient run()", e);
        return;
      }
    }
  }

  public abstract void onMessageReceived(byte[] bytes);

  public void sendMessage(byte[] bytes) {
    try {
      os.write(bytes);
    } catch (IOException e) {
      Log.e(TAG, "BluetoothClient sendMessage()", e);
    }
  }

  private void cancel() {
    if(server != null) {
      try {
        server.close();
      } catch (IOException e) {
        Log.e(TAG, "BluetoothClient cancel()", e);
      }
    }
  }

}
