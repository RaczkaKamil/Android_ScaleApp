package com.example.scaleapp.ui.Measurement;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import com.example.scaleapp.Model.BluetoothFrameAdapter;
import com.example.scaleapp.Model.BluetoothLEService;
import com.example.scaleapp.Model.BluetoothUtils;
import com.example.scaleapp.Model.ConnectionManager;
import com.example.scaleapp.Model.Constants;
import com.example.scaleapp.Model.SampleGattAttributes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.scaleapp.R;
import java.util.ArrayList;
import java.util.List;

public class MeasurementActivity extends AppCompatActivity {
    BluetoothDevice bluetoothDeviceSearch;
    BluetoothDevice bluetoothDevice;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLEService mBluetoothLEService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothFrameAdapter valueList = new BluetoothFrameAdapter();
    private String deviceAddress;

    TextView tf_data;
    TextView tf_deviceID;
    Button button_save;

    private String dataValue;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Measuring");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra("ScaleID");

        tf_data = findViewById(R.id.tf_data);
        tf_deviceID = findViewById(R.id.tf_deviceID);
        button_save = findViewById(R.id.button_save);
        button_save.setOnClickListener(v -> {
            setDialog();
        });

        startSearching();
    }

    private void setDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Weight record");
        alert.setMessage("Enter product name");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveMeasurement(input.getText().toString(), dataValue);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void saveMeasurement(String name, String value) {

        ConnectionManager connectionManager = new ConnectionManager();

        Thread thread = new Thread(() -> connectionManager.sendDataToBase(name, Integer.parseInt(value)));
        thread.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void showSearch() {
        if (deviceAddress.contains(bluetoothDeviceSearch.getAddress())) {
            bluetoothDevice = bluetoothDeviceSearch;
            mBluetoothLEService.connect(bluetoothDevice.getAddress());
        }
    }


    @SuppressLint("SetTextI18n")
    private void displayData2(String data) {
        try {
            int dataNumberValue = Integer.parseInt(data.replace("%", ""));

            if (dataNumberValue == 250) {
                valueList.clear();
            } else {

                valueList.setMeasureObiectList(dataNumberValue);


                if (valueList.getSize() == 2) {
                    if (valueList.getMeasure() < 2500) {
                        tf_data.setText(Integer.toString(valueList.getMeasure()));
                        dataValue = Integer.toString(valueList.getMeasure());
                        valueList.clear();
                    }
                }
            }

        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void receiving_data() {
        if (mNotifyCharacteristic != null) {

            final int charaProp = mNotifyCharacteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                mBluetoothLEService.readCharacteristic(mNotifyCharacteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mBluetoothLEService.setCharacteristicNotification(mNotifyCharacteristic, true);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        String serviceString = "unknown service";

        for (BluetoothGattService gattService : gattServices) {

            uuid = gattService.getUuid().toString();

            serviceString = SampleGattAttributes.lookup(uuid);

            if (serviceString != null) {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    mNotifyCharacteristic = gattCharacteristic;
                    return;
                }
            }
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                final String action = intent.getAction();
                if (BluetoothLEService.ACTION_GATT_CONNECTED.equals(action)) {
                    invalidateOptionsMenu();
                    tf_deviceID.setText(bluetoothDeviceSearch.getName());
                } else if (BluetoothLEService.ACTION_GATT_DISCONNECTED.equals(action)) {

                } else if (BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    try {
                        displayGattServices(mBluetoothLEService.getSupportedGattServices());
                        receiving_data();
                    } catch (NullPointerException e) {
                        e.fillInStackTrace();
                    }

                } else if (BluetoothLEService.ACTION_DATA_AVAILABLE.equals(action)) {

                    displayData2(intent.getStringExtra(BluetoothLEService.EXTRA_DATA));
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onResume() {
        super.onResume();
        try {


            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.REQUEST_LOCATION_ENABLE_CODE);
            }

            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, "Your devices that don't support BLE", Toast.LENGTH_LONG).show();
                finish();
            }
            if (!mBluetoothAdapter.enable()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, Constants.REQUEST_BLUETOOTH_ENABLE_CODE);
            }
            registerReceiver(mGattUpdateReceiver, GattUpdateIntentFilter());
            if (mBluetoothLEService != null) {
                final boolean result = mBluetoothLEService.connect(bluetoothDevice.getAddress());
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }

    private static IntentFilter GattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLEService = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_BLUETOOTH_ENABLE_CODE && resultCode == RESULT_CANCELED) {
            finish();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void startSearching() {
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(getApplicationContext());
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isLocationEnabled()) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, Constants.REQUEST_LOCATION_ENABLE_CODE);
        startScanning(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startScanning(final boolean enable) {
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            List<ScanFilter> scanFilters = new ArrayList<>();
            final ScanSettings settings = new ScanSettings.Builder().build();
            ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(SampleGattAttributes.UUID_BATTERY_SERVICE)).build();
            scanFilters.add(scanFilter);

            try {
                bluetoothLeScanner.startScan(scanFilters, settings, scanCallback);
            } catch (NullPointerException e) {
                e.fillInStackTrace();
            }


        }
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLEService = ((BluetoothLEService.LocalBinder) service).getService();
            if (!mBluetoothLEService.initialize()) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLEService = null;
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            bluetoothDeviceSearch = result.getDevice();
            try {
                Intent gattServiceIntent = new Intent(getApplicationContext(), BluetoothLEService.class);
                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                showSearch();
            } catch (NullPointerException e) {
                e.fillInStackTrace();
            }


        }


        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}