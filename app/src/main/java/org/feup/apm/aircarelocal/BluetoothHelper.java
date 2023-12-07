package org.feup.apm.aircarelocal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothHelper {

    private static final String TAG = "BluetoothHelper";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String sensorAddress;
    private ConnectionListener connectionListener;

    public interface ConnectionListener {
        void onConnectionResult(boolean isConnected);
    }

    public BluetoothHelper(ConnectionListener listener) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sensorAddress = getSensorAddress();
        connectionListener = listener;

    }

    private String getSensorAddress() {
        // Get the list of paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // Iterate through the list of paired devices
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            String deviceAddress = device.getAddress(); // This is the Bluetooth address

            if (deviceName.equals("AmbiUnit")) {
                return deviceAddress;
            }
        }
        return null; //null if device is not in paired devices - temporary!!!
    }

    public void initializeBluetooth() {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Device doesn't support Bluetooth");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is not enabled");
            return;
        }
    }

    public void connectToSensor() {
        if (!sensorAddress.equals(null)) {
            new ConnectTask().execute();
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(sensorAddress);
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();
                Log.d(TAG, "Connected to AmbiUnit");
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to AmbiUnit", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean connectionSuccessful) {
            if (connectionListener != null) {
                connectionListener.onConnectionResult(connectionSuccessful);
            }
        }

    }

    public void closeBluetoothConnection() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            Log.d(TAG, "Bluetooth connection closed");
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth connection", e);
        }
    }
}

