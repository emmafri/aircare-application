package org.feup.apm.aircarelocal;

import android.app.Activity;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.Manifest;
public class BluetoothHelper {

    private static final String TAG = "BluetoothHelper";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int MY_BLUETOOTH_PERMISSION_REQUEST = 1;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String sensorAddress;
    private ConnectionListener connectionListener;
    private Context context;
    private String latestSensorData;
    private DatabaseHelper databaseHelper;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    private boolean isBluetoothOn = false;



    public BluetoothHelper(Context context, ConnectionListener listener) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectionListener = listener;
        databaseHelper = new DatabaseHelper(context);
        registerBluetoothStateReceiver(context);

    }
    private void registerBluetoothStateReceiver(Context context) {
        // Register a BroadcastReceiver to listen for Bluetooth state changes
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothStateReceiver, filter);
    }
    public interface ConnectionListener {
        void onConnectionResult(boolean isConnected);
    }
    public interface ConnectionCallback {
        void onConnectionEstablished();
    }
    public void initializeBluetooth() {
        // Check if the app has Bluetooth permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }
    }
    public void connectToSensor() {
        // Check for Bluetooth permission
        latestSensorData = null;
        if (context != null &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
                        == PackageManager.PERMISSION_GRANTED) {

            // Check if Bluetooth is enabled
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                // Request to enable Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(enableBtIntent, MY_BLUETOOTH_PERMISSION_REQUEST);
                }

            } else {

                sensorAddress = getSensorAddress();
                if (sensorAddress != null && !"null".equals(sensorAddress)) {
                    // Check for Bluetooth connect permission
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED) {

                        // Initiate the connection task
                        new ConnectTask(new ConnectionCallback() {
                            @Override
                            public void onConnectionEstablished() {
                                // Connection established callback
                                // You can perform actions here after the connection is established
                                // For example, you can initiate data retrieval
                                Log.e(TAG, "Connected Successfully");
                                retrieveSensorData();
                                // Reset the connecting flag when the connection is established
                                isConnecting = false;
                            }
                        }).execute();

                    } else {
                        // Request Bluetooth connect permission
                        if (context instanceof Activity) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                    MY_BLUETOOTH_PERMISSION_REQUEST);
                        }
                    }
                } else {
                    Log.e(TAG, "Sensor address is null or \"null\". Cannot connect.");
                }
            }
        } else {
            // Request Bluetooth permission
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.BLUETOOTH},
                        MY_BLUETOOTH_PERMISSION_REQUEST);
            }
        }
    }
    private String getSensorAddress() {

        if (!isConnected && !isConnecting) {
            Toast.makeText(context, "Connecting to AmbiUnit...", Toast.LENGTH_SHORT).show();
            isConnecting = true; // Set the connecting flag to true
        }

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
    private void retrieveSensorData() {
        if (isConnected) {
            Log.e(TAG, "Retrieving data");
            // Fetch sensor data and update UI
            String sensorData = getLatestSensorData();
            float temperature = parseSensorData(sensorData, 6);
            float humidity = parseSensorData(sensorData, 5);
            float co = parseSensorData(sensorData, 2);
            float voc = parseSensorData(sensorData, 12);
            float pm10 = parseSensorData(sensorData, 11);
            float pm25 = parseSensorData(sensorData, 10);
            float battery = parseSensorData(sensorData, 13);

            if (temperature == 0 && humidity == 0 && co == 0 && voc == 0 && pm10 == 0 && pm25 == 0) {
                Toast.makeText(context, "All sensor data values are zero. Error in reading", Toast.LENGTH_SHORT).show();
                latestSensorData= null;
                Log.e(TAG, "Not connected. Cannot retrieve sensor data.");
            } else {

                if (battery == 0) {
                    Toast.makeText(context, "The sensor is completely discharged, please charge", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(context, "Sensor battery: " + battery + "%", Toast.LENGTH_SHORT).show();
                }

                // Insert a new entry in the database
                databaseHelper.insertNewEntry(temperature, humidity, co, voc, pm10, pm25);
                latestSensorData= null;


                // Notify MainActivity to update UI elementsa
                if (connectionListener != null) {
                    connectionListener.onConnectionResult(true);
                }
            }
        }
    }
    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {
        private ConnectionCallback callback;
        public ConnectTask(ConnectionCallback callback) {
            this.callback = callback;
        }
        private boolean checkConnectionStatus() {
            return bluetoothSocket != null && bluetoothSocket.isConnected();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    // Check for Bluetooth connect permission
                    if (context != null &&
                            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH)
                                    == PackageManager.PERMISSION_GRANTED) {
                        // Check if Bluetooth is on
                        if (isBluetoothOn) {
                            isConnected = checkConnectionStatus();
                        } else {
                            if (context instanceof Activity) {
                                ActivityCompat.requestPermissions((Activity) context,
                                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                        MY_BLUETOOTH_PERMISSION_REQUEST);
                                isConnected = checkConnectionStatus();
                            }

                        }
                        if (!isConnected) {
                            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(sensorAddress);
                            // Create an insecure RFCOMM socket
                            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                            bluetoothSocket.connect();
                        }
                        Log.d(TAG, "Connected to AmbiUnit");


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    inputStream = bluetoothSocket.getInputStream();
                                    outputStream = bluetoothSocket.getOutputStream();
                                    latestSensorData = readSensorData();

                                } catch (IOException e) {
                                    Log.e(TAG, "Error reading sensor data", e);
                                }
                            }
                        }).start();

                        return true;
                    } else {
                        Log.e(TAG, "Bluetooth connect permission not granted");
                        if (context instanceof Activity) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                    MY_BLUETOOTH_PERMISSION_REQUEST);
                        }
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
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            while ((byteRead = inputStream.read()) != -1) {
                char character = (char) byteRead;
                data.append(character);
                if (character == '\n') {
                    break;
                }
            }
            } else {
                // Handle the situation where the socket is closed
                Log.e(TAG, "Bluetooth socket closed. Cannot read sensor data.");
            }


            if (!data.toString().isEmpty()) {
                latestSensorData = data.toString();
            }
            Log.d(TAG, "Received sensor data: " + data);

            return latestSensorData;
        }

        @Override
        protected void onPostExecute(Boolean connectionSuccessful) {
            if (connectionSuccessful) {
                isConnected = true; // Update connection status
                Log.d(TAG, "Connected to AmbiUnit");

                // Notify the callback when the connection is established
                if (callback != null) {
                    callback.onConnectionEstablished();
                }
            } else {
                Log.e(TAG, "Failed to connect after multiple attempts");
            }
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

    public float parseSensorData(String sensorData, int index) {
        try {
            // Split the sensor data using ';' as the delimiter
            String[] dataParts = sensorData.split(";");

            if (dataParts[dataParts.length - 1].contains("|")) {
                // If it does, split it using '|' and take the first part
                dataParts[dataParts.length - 1] = dataParts[dataParts.length - 1].split("\\|")[0];
            }

            // Extract the parameter at the specified index
            return Float.parseFloat(dataParts[index]);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;  // Default value if parsing fails
        }
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        // Bluetooth is turned on, update the isConnected variable accordingly
                        isBluetoothOn = true;
                        isConnected = false;
                        Log.d(TAG, "Bluetooth turned on");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        // Bluetooth is turned off, reset the isConnected variable
                        isBluetoothOn = false;
                        isConnected = false;
                        Log.d(TAG, "Bluetooth turned off");
                        break;
                }
            }
        }
    };
    /*public void insertNewEntry(float temperature, float humidity, float co, float voc, float pm10, float pm25) {

        databaseHelper.insertNewEntry(temperature, humidity, co, voc, pm10, pm25);
        Log.d(TAG, "Inserting new entry into the database");
    }*/
}

