v1.0 20170421
==================================
I begin to develop this app from 2017/04/14, used 6 days. 
Decompiled the old wifilogin app which was deleloped at 2014. but all the souce code was missing because hard disk crash.
This version is developed by "Android studio", the former wifilogin is developed by Eclipse.

v1.1 20170421
==================================
in order to reduce access of gmp.oracle.com, after wifi is connected, check if internet is ready, if ready, don't login again

v1.2 20170719
==================================
The URL used to do login and logout is changed from "https://webauth-redirect.oracle.com/login.html" to "http://webauth-redirect.oracle.com/login.html"

v1.3 20171219
==================================
1. Added one foreground service so that it will not be killed automatically by system
   Reason: on my android device 4.4.2, this app always be automatically killed by system,
   because it's just a background process, priority is too low.
2. Added a timer task to check internet connectivity every 5 minutes.

v1.4 20180206
==================================
1. support new WIFI of corporation. if "clean-internet24" is found, use it rather that "clear-guest", no wifi login for this SSID.
2. automatically close mobile data connection when app started. plan to turn on mobile data but it can't work. 
   maybe it's related to SDK version. need more investigation.

v1.5 20180612
==================================
1. Support setting SSID which will be connected automatically
2. Add Hide button to shift main activity to background
3. Automatically resize controls in the main activity, so that it support different screen size(for XiaoMi Mix2S).

