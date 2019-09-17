cd F:\Workspace\AndroidStudio-sdk\platform-tools
adb root
adb remount
adb push F:\Workspace\DEMO\SCAN\ScanServiceNew\cm60\build\outputs\apk\debug\cm60-debug.apk /system/app/
adb reboot
cd F:\Workspace\DEMO\SCAN\ScanServiceNew\