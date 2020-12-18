package com.example.scaleapp.ui.ScaleSearcher;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.example.scaleapp.Model.BluetoothLEService;
import com.example.scaleapp.Model.BluetoothUtils;
import com.example.scaleapp.Model.SampleGattAttributes;
import com.example.scaleapp.R;
import com.example.scaleapp.ui.Measurement.MeasurementActivity;


import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class ScaleSearcherFragment extends Fragment {
    private ArrayList<Scale> scaleList = new ArrayList<>();
    private ScaleSearcherViewModel scaleSearcherViewModel;


    private ListView online_list;
    ScaleListAdapter customAdapter;
    BluetoothDevice bluetoothDeviceSearch;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLEService mBluetoothLEService;


    @RequiresApi(api = Build.VERSION_CODES.P)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        scaleSearcherViewModel =
                new ViewModelProvider(this).get(ScaleSearcherViewModel.class);


        View root = inflater.inflate(R.layout.fragment_scale_searcher, container, false);


        online_list = root.findViewById(R.id.scale_list);
        customAdapter = new ScaleListAdapter(scaleList, getContext());


        final TextView textView = root.findViewById(R.id.text_home);
        scaleSearcherViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                textView.setText(s);

            }
        });

        scaleSearcherViewModel.getScale().observe(getViewLifecycleOwner(), new Observer<ArrayList<Scale>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Scale> s) {
                scaleList.addAll(s);
                customAdapter.notifyDataSetChanged();

            }
        });
        setList();
        startSearching();
        return root;
    }


    public void showSearch() {
        if (!isScaleExistInList(bluetoothDeviceSearch.getAddress())) {
            scaleList.add(new Scale(bluetoothDeviceSearch.getAddress(), bluetoothDeviceSearch.getName()));
            customAdapter.notifyDataSetChanged();
        }
    }

    private boolean isScaleExistInList(String ID) {

        for (Scale scale :
                scaleList) {
            if (scale.getID().contains(ID)) {
                return true;
            }
        }
        return false;
    }


    private void setList() {
        online_list.setAdapter(customAdapter);
        online_list.setClickable(false);
        online_list.setOnItemClickListener((adapterView, view, i, l) -> {
            int index = (int) l;
            connectToChosedDevice(index);
        });
    }


    private void connectToChosedDevice(int index) {
        Intent intent = new Intent();
        intent.setClass(getContext(), MeasurementActivity.class);
        intent.putExtra("ScaleID", scaleList.get(index).getID());
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void startSearching() {
        mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(getContext().getApplicationContext());
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        if (!manager.isLocationEnabled()) {
            getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //  ctx.startActivityForResult(intent, Constants.REQUEST_LOCATION_ENABLE_CODE);
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
                Intent gattServiceIntent = new Intent(getContext(), BluetoothLEService.class);
                getContext().bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
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


}