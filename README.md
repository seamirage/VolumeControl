# VolumeControl

This app allows to bypass the high volume warning without root priviledges. </br>
It requires to reboot the device from time to time (or, alternatively, switch to another user and log in again).

**How to use**
1. Build and install the app
2. Grant the permission 
  ```bash
   adb shell pm grant com.trueapps.volumecontrol android.permission.WRITE_SECURE_SETTINGS
  ```
3. Wait for a notification. It will be sent at the warning's approach.
