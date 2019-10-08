package com.reydw.notifyclient.actions;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class NotificationAction implements Parcelable {

  private String appname;
  private String title;
  private String text;
  private String subtext;

  public NotificationAction(Bundle notification) {
    Object appnameObject = notification.get("appname");
    Object titleObject = notification.get("title");
    Object textObject = notification.get("text");
    Object subtextObject = notification.get("subtext");
    appname = appnameObject == null ? "" : appnameObject.toString();
    title = titleObject == null ? "" : titleObject.toString();
    text = textObject == null ? "" : textObject.toString();
    subtext = subtextObject == null ? "" : subtextObject.toString();
  }

  public static final Parcelable.Creator<NotificationAction> CREATOR = new Parcelable.Creator<NotificationAction>(){

    @Override
    public NotificationAction createFromParcel(Parcel source) throws NullPointerException{
      Bundle bundle = source.readBundle(getClass().getClassLoader());
      if(bundle == null) throw new NullPointerException("Cannot create NotificationAction, bundle cannot be null");
      return new NotificationAction(bundle);
    }

    @Override
    public NotificationAction[] newArray(int size) {
      return new NotificationAction[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    Bundle bundle = new Bundle();
    bundle.putString("appname", this.appname);
    bundle.putString("title", this.title);
    bundle.putString("text", this.text);
    bundle.putString("subtext", this.subtext);
    dest.writeBundle(bundle);
  }

  public String getAppname() {
    return appname;
  }

  public String getTitle() {
    return title;
  }

  public String getText() {
    return text;
  }

  public String getSubtext() {
    return subtext;
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