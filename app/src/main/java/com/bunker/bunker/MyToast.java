package com.bunker.bunker;

import android.content.Context;
import android.widget.Toast;
public class MyToast {
    private static Toast mToastCurrent;

    public static void EndCurrentToast(){
        if(mToastCurrent != null){
            mToastCurrent.cancel();
        }
    }

    public static void ShowToast(String str, Context pThis){
        EndCurrentToast();
        mToastCurrent = Toast.makeText(pThis, str, Toast.LENGTH_SHORT);
        mToastCurrent.show();
    }
}
