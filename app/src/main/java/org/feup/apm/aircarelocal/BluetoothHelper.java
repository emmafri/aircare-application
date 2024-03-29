package org.feup.apm.aircarelocal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

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
    private boolean isBluetoothOn = false;

    // This class is used to manage all the processes that relate to the bluetooth connection between the mobile device and the sensor
    public BluetoothHelper(Context context, ConnectionListener listener) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectionListener = listener;
        databaseHelper = new DatabaseHelper(context);
        registerBluetoothStateReceiver(context);

    }
    // Continuously records the state of bluetooth
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
    // Used at the start to ask the user for permission to see and access nearby devices
    public boolean initializeBluetooth() {
        // Check if the app has Bluetooth permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            // Bluetooth connect permission is already granted
            return true;
        } else {
            // Request Bluetooth connect permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                // Return false because the permission is not yet granted
                return false;
            }
        }
        // Return false if permission is not granted and not requested
        return false;
    }

    // Main process to connect to the sensor and receive the data
    public void connectToSensor() {
        // Make sure no entries are reused
        latestSensorData = null;

        // Only happens if the used has given permission to the app to see and interact with nearby devices
        if (initializeBluetooth()) {
            // Make sure the is bluetooth permission
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
                                    Log.d(TAG, "Connected Successfully");
                                    retrieveSensorData();
                                    // Reset the connecting flag when the connection is established
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
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                        return;
                    }
                }
            }
        }
        else {
            Toast.makeText(context, "Bluetooth connection denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Gets the sensor address from the nearby devices in order to ark for the data
    private String getSensorAddress() {

            Toast.makeText(context, "Connecting to AmbiUnit...", Toast.LENGTH_SHORT).show();

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
    // Used to store the data retrieved from the sensor into the database, with the appropriate format
    private void retrieveSensorData() {
        if (isConnected) {
            Log.d(TAG, "Retrieving data");
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
                Log.e(TAG, "Error in connection. Cannot retrieve sensor data.");
            } else {

                if (battery == 0) {
                    Toast.makeText(context, "The sensor is completely discharged, please charge", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(context, "Sensor battery: " + battery + "%", Toast.LENGTH_SHORT).show();
                }

                // Insert a new entry in the database
                databaseHelper.insertNewEntry(temperature, humidity, co, voc, pm10, pm25);

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
        // Used to check if the socket is used
        private boolean checkConnectionStatus() {
            return bluetoothSocket != null && bluetoothSocket.isConnected();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            int maxRetries = 3;
            int retryCount = 0;

            // Limits the amount of tries the system will do to retrieve data from the sensor
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
                        // If the sensor is not yet connected to the phone
                        if (!isConnected) {
                            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(sensorAddress);
                            // Create an insecure RFCOMM socket
                            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                            bluetoothSocket.connect();
                        }
                        Log.d(TAG, "Connected to AmbiUnit");

                        // Requests the data from the sensor
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
            return false;
        }
        // Reads the data incoming from the sensor and stores it into a convenient String variable
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

            // Only passes it to the latestSensorData variable if some data is actually received
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
                if (context != null) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Failed to connect after multiple attempts", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            if (connectionListener != null) {
                connectionListener.onConnectionResult(connectionSuccessful);
            }
        }
    }

    // Permits access to the latestSensorData variable
    public String getLatestSensorData() {
        return latestSensorData;
    }

    // Used for closing the connection
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

            isConnected = false;
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth connection", e);
        }
    }

    // Used for processing the typical data output from the sensor into the format needed
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

    // Keeps track of whether the bluetooth is turned off or on
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
                        closeBluetoothConnection();
                        break;
                }
            }
        }
    };
}

