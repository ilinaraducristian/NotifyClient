package com.reydw.notifyclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
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
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = new ObjectInputStream(byteArrayInputStream);
        onMessageReceived((NotificationForClient) objectInput.readObject());
        objectInput.close();
      } catch (Exception e) {
        Log.i(TAG, "BluetoothClient: connection closed");
        Log.e(TAG, "BluetoothClient run()", e);
        return;
      }
    }
  }

  public abstract void onMessageReceived(NotificationForClient notificationForClient);

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

  @SuppressWarnings({"unused", "SpellCheckingInspection", "FieldCanBeLocal"})
  class NotificationForClient implements Serializable {

    private static final long serialVersionUID = 69;

    private final String appname;
    private final String title;
    private final String text;
    private final String subtext;

    NotificationForClient(String appname, String title, String text, String subtext) {
      this.appname = appname;
      this.title = title;
      this.text = text;
      this.subtext = subtext;
    }

    @Override
    public String toString() {
      return "NotificationForClient{" +
        "appname='" + appname + '\'' +
        ", title='" + title + '\'' +
        ", text='" + text + '\'' +
        ", subtext='" + subtext + '\'' +
        '}';
    }
  }

}
