cd F:\Workspace\AndroidStudio-sdk\platform-tools
adb root
adb remount
adb push F:\Workspace\DEMO\SCAN\ScanServiceNew4710\se4710\build\outputs\apk\debug\se4710-debug.apk /system/app/
adb reboot
cd F:\Workspace\DEMO\SCAN\ScanServiceNew4710