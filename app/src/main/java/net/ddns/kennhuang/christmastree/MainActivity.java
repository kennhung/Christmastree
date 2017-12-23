package net.ddns.kennhuang.christmastree;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static Context mContext;
    public static String status, msg;
    PresentEngine presentEngine;
    TextView statusBar, msgBar;
    ToggleButton togglebutton;

    String ssid = "S2BTree";
    String password = "treeeeeeee";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        statusBar = (TextView) findViewById(R.id.textView);
        msgBar = (TextView) findViewById(R.id.textView2);
        togglebutton = (ToggleButton) findViewById(R.id.toggleButton);
        presentEngine = new PresentEngine();
        final Thread t = new Thread(presentEngine);
        t.setName("PresentEngine");
        t.start();
        status = "Startup";
        msg = "";
        msgBar.setText(" ");
        new Thread() {
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            msgBar.setText(msg);
                            statusBar.setText(status);
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            public void run() {
                while (true) {
                    WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                    if (manager.isWifiEnabled()) {
                        WifiInfo wifiInfo = manager.getConnectionInfo();
                        if (wifiInfo != null) {
                            NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                            if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                                if (!wifiInfo.getSSID().equals("\"" + ssid + "\"")) {
                                    System.out.println("Not connect to tree: " + wifiInfo.getSSID());
                                    WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                                    for (WifiConfiguration i : list) {
                                        if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                                            wifiManager.disconnect();
                                            wifiManager.enableNetwork(i.networkId, true);
                                            wifiManager.reconnect();
                                            System.out.println("Connecting to tree");
                                            break;
                                        }
                                    }
                                }


                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.

                start();
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.startPresentChinese).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentEngine.stopAll();
                presentEngine.startPresent(1);
            }
        });
        findViewById(R.id.startPresentEng).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentEngine.stopAll();
                presentEngine.startPresent(2);
            }
        });
        findViewById(R.id.stopAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentEngine.stopAll();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentEngine.stopAll();
                presentEngine.startPresent(3);
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presentEngine.stopAll();
                presentEngine.startPresent(4);
            }
        });

        togglebutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                compoundButton.setChecked(b);

                presentEngine.setAutoSpeak(b);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presentEngine.stopAll();
    }

}
