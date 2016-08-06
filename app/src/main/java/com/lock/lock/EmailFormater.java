package com.lock.lock;

public class EmailFormater {
  private String mUserName = "";
  private String mHost = "";
  private boolean mIsValid = false;

  public String getHost() {
    return mHost;
  }

  public String getUserName() {
    return mUserName;
  }

  public boolean isValid() {
    return mIsValid;
  }

  public EmailFormater(String email) {
    email += " ";
    String[] fist = email.split("@");
    if(fist.length == 2) {
      mUserName = fist[0].trim();
      String[] second = fist[1].split("\\.");
      boolean isEmpy = false;
      StringBuilder ss = new StringBuilder();
      for(int i = 0; i < second.length; i++) {
        String partialHost = second[i].trim();
        ss.append(partialHost);
        if(i != second.length - 1) {
          ss.append('.');
        }
        if(partialHost.isEmpty()) {
          isEmpy = true;
          break;
        }
      }
      if(!isEmpy) {
        mHost = ss.toString();
      }
      mIsValid = !isEmpy && !mUserName.isEmpty() && second.length != 0;
    }else {
      mIsValid = false;
    }
  }

  public String GetEmail() {
    StringBuilder ss = new StringBuilder();
    ss.append(mUserName);
    ss.append('@');
    ss.append(mHost);
    return ss.toString();
  }
}