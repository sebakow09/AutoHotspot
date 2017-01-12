package pro1024.seko.autohotspot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static WifiAP wifiAp;
    private WifiManager wifi;
    private MyReceiver myReceiver;
    final double [] OTOld = new double [1];
    double currentData = 0;
    double currentData_2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiAp = new WifiAP();
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        initReceiver();

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView infoView = (TextView) findViewById(R.id.traffic_info);
                                String info = "";

                                info += "Dane komórkowe:\n";
                                info += ("\tOdebrane: " + TrafficStats.getMobileRxBytes() / 1024 + " KB(" + TrafficStats.getMobileRxBytes() / 1048576 + " MB)");
                                info += ("\tWysłane: " + TrafficStats.getMobileTxBytes() / 1024 + " KB(" + TrafficStats.getMobileTxBytes() / 1048576 + " MB)");

                                info += "\nWszystkie interfejsy sieciowe:\n";
                                info += ("\tOdebrane: " + TrafficStats.getTotalRxBytes() / 1024 + " KB(" + TrafficStats.getTotalRxBytes() / 1048576 + " MB)");
                                info += ("\tWysłane: " + TrafficStats.getTotalTxBytes() / 1024 + " KB(" + TrafficStats.getTotalTxBytes() / 1048576 + " MB)");

                                infoView.setText(info);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                double overallTraffic = TrafficStats.getTotalRxBytes()/1024;
                currentData = currentData + overallTraffic - OTOld [0];
                currentData_2 += currentData;

                if(currentData_2<500){
                    /*TextView view2 = (TextView) findViewById(R.id.yn);
                    view2.setText("Czy przekroczyło 500KB? - NIE " + currentData + " i " + currentData_2);*/
                    finishReceiver();
                    wifiAp.toggleWiFiAP(wifi, MainActivity.this);
                    killApp();
                }
                else{
                    /*TextView view2 = (TextView) findViewById(R.id.yn);
                    view2.setText("Czy przekroczyło 500KB? - TAK " + currentData + " i " + currentData_2);*/
                    currentData = 0;
                    currentData_2 = 0;
                }

                /*TextView view1 = (TextView) findViewById(R.id.test);
                view1.setText("Current Data = " + currentData);*/

                OTOld [0] = overallTraffic;
                currentData = 0;

                handler.postDelayed(this, 300000);
            }
        }, 300000 );

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

    public void killApp(){
        MainActivity.super.onDestroy();
        finish();
        System.exit(0);
    }
}