package org.feup.apm.aircarelocal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

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
    private Context context;
    private String latestSensorData;
    private boolean isConnected = false;

    public interface ConnectionListener {
        void onConnectionResult(boolean isConnected);
    }

    public BluetoothHelper(Context context, ConnectionListener listener) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sensorAddress = getSensorAddress();
        connectionListener = listener;

    }

    private String getSensorAddress() {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT)
                        == PackageManager.PERMISSION_GRANTED) {

            if (bluetoothAdapter != null) {

                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices != null) {
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceAddress = device.getAddress(); // This is the Bluetooth address

                        if (!deviceName.equals(null)) {
                            Log.d(TAG, "Found Bluetooth Device - Name: " + deviceName + ", Address: " + deviceAddress);
                        } else {
                            Log.e(TAG, "Could not access device name.");

                        }

                        if ("AmbiUnit 23".equals(deviceName)) {
                            return deviceAddress;
                        }
                    }
                } else {
                    Log.e(TAG, "pairedDevices is null. Could not getBondedDevices.");

                }
            }
        }
        Log.e(TAG, "BLUETOOTH_CONNECT Permission denied.");
        return null;
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
        Log.d(TAG, "Bluetooth is enabled");
    }

    public void connectToSensor() {
        if (sensorAddress != null && !"null".equals(sensorAddress)) {
                new ConnectTask().execute();
        } else {
            Log.e(TAG, "Sensor address is null or \"null\". Cannot connect.");
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    // Check for Bluetooth connect permission
                    if (context != null &&
                            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT)
                                    == PackageManager.PERMISSION_GRANTED) {

                        if(!isConnected) {
                            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(sensorAddress);
                            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                            bluetoothSocket.connect();
                            isConnected = true; // Update connection status
                        }
                        Log.d(TAG, "Connected to AmbiUnit");

                        inputStream = bluetoothSocket.getInputStream();
                        outputStream = bluetoothSocket.getOutputStream();
                        latestSensorData = readSensorData();
                        return true;
                    } else {
                        Log.e(TAG, "Bluetooth connect permission not granted");
                        return false;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error connecting to AmbiUnit. Retrying...", e);
                    retryCount++;
                    // Sleep for a short duration before retrying
                    try {
                        Thread.sleep(1000); // Adjust the sleep duration as needed
                    } catch (InterruptedException ex) {
                        Log.e(TAG, "Thread sleep interrupted", ex);
                    }
                }
            }
            Log.e(TAG, "Failed to connect after multiple attempts");
            return false;
        }

        private String readSensorData() throws IOException {
            StringBuilder data = new StringBuilder();
            int byteRead;
            while ((byteRead = inputStream.read()) != -1) {
                char character = (char) byteRead;
                data.append(character);
                if (character == '\n') {
                    break;
                }
            }

            if (!data.toString().isEmpty()) {
                latestSensorData = data.toString();
            }
            Log.d(TAG, "Received sensor data: " + data.toString());

            return latestSensorData;
        }

        @Override
        protected void onPostExecute(Boolean connectionSuccessful) {
            if (connectionListener != null) {
                connectionListener.onConnectionResult(connectionSuccessful);
            }
        }

    }
    public String getLatestSensorData() {
        return latestSensorData;
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

