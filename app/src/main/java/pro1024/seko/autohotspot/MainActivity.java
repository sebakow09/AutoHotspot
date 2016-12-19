package pro1024.seko.autohotspot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static WifiAP wifiAp;
    private WifiManager wifi;
    private MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiAp = new WifiAP();
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        initReceiver();

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void initReceiver(){
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onDestroy(){
        finishReceiver();
        super.onDestroy();
    }

    private void finishReceiver(){
        unregisterReceiver(myReceiver);
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            TextView textView_1 = (TextView)findViewById(R.id.textView_1);
            TextView textView_2 = (TextView)findViewById(R.id.textView_2);
            TextView textView_3 = (TextView)findViewById(R.id.textView_3);

            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, filter);

            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            if (isCharging == true){
                if (wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING) {
                } else {
                    wifiAp.toggleWiFiAP(wifi, MainActivity.this);
                }
                textView_1.setText("Ladowarka podlaczona");
                textView_2.setText("WiFi wylaczone");
                textView_3.setText("Tethering wlaczony");
            }else {
                if (wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING) {
                    wifiAp.toggleWiFiAP(wifi, MainActivity.this);
                }
                textView_1.setText("Ladowarka ODLACZONA");
                if (wifi.isWifiEnabled()) textView_2.setText("WiFi wlaczone");
                else textView_2.setText("WiFi WYLACZONE");
                textView_3.setText("Tethering WYLACZONY");
            }
        }
    }

    public static void updateStatusDisplay() {
        if (wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLED || wifiAp.getWifiAPState()==wifiAp.WIFI_AP_STATE_ENABLING) {
            //btnWifiToggle.setText("Turn off");
            //findViewById(R.id.bg).setBackgroundResource(R.drawable.bg_wifi_on);
        } else {
            //btnWifiToggle.setText("Turn on");
            //findViewById(R.id.bg).setBackgroundResource(R.drawable.bg_wifi_off);
        }
    }
}