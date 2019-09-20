package com.reydw.notifyclient;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public abstract class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

  private ArrayList<BluetoothDevice> pairedBluetoothDevices;

  RecyclerViewAdapter(ArrayList<BluetoothDevice> pairedBluetoothDevices) {
    this.pairedBluetoothDevices = pairedBluetoothDevices;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.paired_device, viewGroup, false);
    ViewHolder viewHolder = new ViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
    viewHolder.pairedDeviceButton.setText(pairedBluetoothDevices.get(i).getName());
    viewHolder.pairedDeviceButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i("BLUETOOTH", String.format("Device %s selected", pairedBluetoothDevices.get(viewHolder.getAdapterPosition())));
        onDeviceSelected(viewHolder.getAdapterPosition());
      }
    });
  }

  @Override
  public int getItemCount() {
    return pairedBluetoothDevices.size();
  }

  public abstract void onDeviceSelected(int index);

  class ViewHolder extends RecyclerView.ViewHolder {

    Button pairedDeviceButton;
    RelativeLayout pairedDeviceLayout;

    ViewHolder(View pairedDeviceView) {
      super(pairedDeviceView);
      pairedDeviceButton = pairedDeviceView.findViewById(R.id.pairedDeviceButton);
      pairedDeviceLayout = pairedDeviceView.findViewById(R.id.pairedDeviceLayout);
    }

  }

}

