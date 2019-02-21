package com.bunker.bunker.data

class EmailFormatter(email: String) {
  var userName = ""
    private set
  var host = ""
    private set
  var isValid = false
    private set

  init {
    var email = email
    email += " "
    val fist = email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if(fist.size == 2) {
      userName = fist[0].trim { it <= ' ' }
      val second = fist[1].split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      var isEmpy = false
      val ss = StringBuilder()
      for(i in second.indices) {
        val partialHost = second[i].trim { it <= ' ' }
        ss.append(partialHost)
        if(i != second.size - 1) {
          ss.append('.')
        }
        if(partialHost.isEmpty()) {
          isEmpy = true
          break
        }
      }
      if(!isEmpy) {
        host = ss.toString()
      }
      isValid = !isEmpy && !userName.isEmpty() && second.isNotEmpty()
    } else {
      isValid = false
    }
  }

  fun GetEmail(): String {
    return "$userName@$host"
  }
}