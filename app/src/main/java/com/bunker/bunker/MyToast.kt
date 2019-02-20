package com.bunker.bunker

import android.content.Context
import android.widget.Toast

object MyToast {
  private var mToastCurrent: Toast? = null

  fun EndCurrentToast() {
    mToastCurrent?.cancel()
  }

  fun ShowToast(str: String, pThis: Context) {
    EndCurrentToast()
    mToastCurrent = Toast.makeText(pThis, str, Toast.LENGTH_SHORT)
    mToastCurrent?.show()
  }
}
