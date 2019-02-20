package com.bunker.bunker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bunker.bunker.data.model.UserData

class UserViewModel : ViewModel() {
  val userData: LiveData<UserData> = UserLiveData()
}
