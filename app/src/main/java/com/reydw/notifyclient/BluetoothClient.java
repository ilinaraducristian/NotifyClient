package com.reydw.notifyclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Parcel;
import android.util.Log;

import com.reydw.notifyclient.actions.NotificationAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class BluetoothClient extends Thread {

  private static final String TAG = MainActivity.TAG;
  private static final String BLUETOOTH_UUID = MainActivity.BLUETOOTH_UUID;
  private static final int MESSAGE_SIZE = 1024;

  private BluetoothSocket server;
  private InputStream is;
  private OutputStream os;

  BluetoothClient(BluetoothDevice server) {
    try {
      server.get
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
      byte[] bytes = new byte[MESSAGE_SIZE];
      try {
        is.read(bytes);
//        for (byte b : bytes) {
//          String st = String.format("%02X", b);
//          Log.i(TAG + "plm", st);
//        }
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        NotificationAction action = NotificationAction.CREATOR.createFromParcel(parcel);
        onMessageReceived(action);
      } catch (Exception e) {
        Log.i(TAG, "BluetoothClient: connection closed");
        Log.e(TAG, "BluetoothClient run()", e);
        return;
      }
    }
  }

  public abstract void onMessageReceived(NotificationAction notificationForClient);

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
