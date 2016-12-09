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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyReceiver myReceiver;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initReceiver();
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
            wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            TextView textView_1 = (TextView)findViewById(R.id.textView_1);
            TextView textView_2 = (TextView)findViewById(R.id.textView_2);
            /*if (intent.getAction().equalsIgnoreCase("android.intent.action.ACTION_POWER_CONNECTED")){
                wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);

            }
            else {
                wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(false);
            }*/


            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, filter);

            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            if (isCharging == true){
                wifiManager.setWifiEnabled(false);
                textView_1.setText("Ladowarka podlaczona");
                textView_2.setText("WiFi wylaczone");
                //Toast.makeText(getApplicationContext(), "Ladowarka podlaczona, WiFi wylaczone.", Toast.LENGTH_SHORT).show();
            }
            if(isCharging == false) {
                wifiManager.setWifiEnabled(true);
                textView_1.setText("Ladowarka ODLACZONA");
                textView_2.setText("WiFi WLACZONE");
                //Toast.makeText(getApplicationContext(), "Ladowarka ODLACZONA, WiFi WLACZONE.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
