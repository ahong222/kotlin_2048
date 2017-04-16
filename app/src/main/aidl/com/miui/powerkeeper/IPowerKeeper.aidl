// IPowerKeeper.aidl
package com.miui.powerkeeper;

// Declare any non-default types here with import statements
import android.os.Bundle;
import java.util.List;
interface IPowerKeeper{
    boolean getLowFpsMode();
    int getPowerSaveAppConfigure(in Bundle paramBundle1, in Bundle paramBundle2);
    List getResult();
    String readUserProfile();
    void restrictAppQuick(int paramInt);
    void setLowFpsMode(boolean paramBoolean);
    int setPowerSaveAppConfigure(in Bundle paramBundle);
    boolean writeUserProfile(String paramString);
}