package org.feup.apm.aircarelocal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothHelper {

    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    // UUID for Serial Port Profile (SPP)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String deviceAddress;

    public BluetoothHelper() {

    }

    public void initializeBluetooth() {
        // Check if the device supports Bluetooth
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, you can prompt the user to enable it
            return;
        }

        // Get the list of paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // Example: Choose the first paired device, you may want to implement a selection mechanism
        if (!pairedDevices.isEmpty()) {
            BluetoothDevice selectedDevice = pairedDevices.iterator().next();
            deviceAddress = selectedDevice.getAddress();
        }
    }

    public void connectBluetoothDevice() {
        // Check if the Bluetooth socket is already connected
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            return; // Already connected, no need to reconnect
        }

        // Check if the Bluetooth socket is null (not created in initializeBluetooth)
        if (bluetoothSocket == null) {
            return; // Socket not created, cannot proceed with connection
        }

        // Connect to the Bluetooth device in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Attempt to connect to the Bluetooth device
                    bluetoothSocket.connect();

                    // If the connection is successful, obtain input and output streams
                    inputStream = bluetoothSocket.getInputStream();
                    outputStream = bluetoothSocket.getOutputStream();

                    // Now you can use inputStream and outputStream for communication
                } catch (IOException e) {
                    // Handle connection error (e.g., device is not available, or connection fails)
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeBluetoothConnection() {
        // Close the Bluetooth connection

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

