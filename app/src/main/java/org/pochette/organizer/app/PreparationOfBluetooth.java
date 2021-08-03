package org.pochette.organizer.app;


import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.view.InputDevice;

import org.pochette.utils_lib.logg.Logg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class PreparationOfBluetooth {
    private final static String TAG = "FEHA (PreparationOfBluetooth)";
//
//    final static BluetoothProfile.ServiceListener listener = new BluetoothProfile.ServiceListener() {
//        @Override
//        public void onServiceConnected(int profile, BluetoothProfile proxy) {
//            final int[] states = new int[]{BluetoothProfile.STATE_CONNECTED, BluetoothProfile.STATE_CONNECTING};
//            switch (profile) {
//                case BluetoothProfile.A2DP:
//                    Logg.i(TAG, "profile A2DP");
//                    break;
//                case BluetoothProfile.GATT: // NOTE ! Requires SDK 18 !
//                    Logg.i(TAG, "profile GATT");
//                    break;
//                case BluetoothProfile.GATT_SERVER: // NOTE ! Requires SDK 18 !
//                    Logg.i(TAG, "profile GATT_SERVER");
//                    break;
//                case BluetoothProfile.HEADSET:
//                    Logg.i(TAG, "profile HEADSET");
//                    break;
//                case BluetoothProfile.HEALTH: // NOTE ! Requires SDK 14 !
//                    Logg.i(TAG, "profile HEALTH");
//                    break;
//                case BluetoothProfile.SAP: // NOTE ! Requires SDK 23 !
//                    Logg.i(TAG, "profile SAP");
//                    break;
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(int profile) {
//        }
//    };


//    static void loggDevices(Activity iActivity) {
//
//        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (btAdapter == null || !btAdapter.isEnabled()) {
//            Logg.w(TAG, "bluetooth off");
//            return;
//        }
//        btAdapter.getProfileProxy(iActivity, listener, BluetoothProfile.A2DP);
//        btAdapter.getProfileProxy(iActivity, listener, BluetoothProfile.GATT); // NOTE ! Requires SDK 18 !
//        btAdapter.getProfileProxy(iActivity, listener, BluetoothProfile.GATT_SERVER); // NOTE ! Requires SDK 18 !
//        btAdapter.getProfileProxy(iActivity, listener, BluetoothProfile.HEADSET);
//        btAdapter.getProfileProxy(iActivity, listener, BluetoothProfile.HEALTH); // NOTE ! Requires SDK 14 !
//        btAdapter.getProfileProxy(iActivity, listener, BluetoothProfile.SAP); // NOTE ! Requires SDK 23 !
//
////        BluetoothAdapter tBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
////        Set<BluetoothDevice> tPairedDevices = tBluetoothAdapter.getBondedDevices();
////
////
////        for(BluetoothDevice bt : tPairedDevices) {
////            Logg.i(TAG, bt.toString());
////            Logg.i(TAG, bt.getAddress());
////            Logg.i(TAG, bt.getName());
////            Logg.i(TAG, bt.getBluetoothClass().toString());
////            Logg.i(TAG,"device"+ bt.getBluetoothClass().getDeviceClass());
////            Logg.i(TAG,"maj device"+ bt.getBluetoothClass().getMajorDeviceClass());
////            Logg.i(TAG, "BondState: "+bt.getBondState());
////            Logg.i(TAG, "Type: "+bt.getType());
////
////        }
//    }

//    static BluetoothDevice getDevice() {
//        final BluetoothAdapter tBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//
//        Set<BluetoothDevice> pairedDevices = tBluetoothAdapter.getBondedDevices();
//
//        List<String> s = new ArrayList<String>();
//        for (BluetoothDevice lBluetoothDevice: pairedDevices) {
//
//            s.add(lBluetoothDevice.getName());
//            Logg.i(TAG, "fond"+lBluetoothDevice.toString());
//
//        }
//
//
//        BluetoothDevice tDevice = tBluetoothAdapter.getRemoteDevice(getMac());
//        if (tDevice != null) {
//            Logg.i(TAG, "got " + tDevice.toString());
//        }
//        return tDevice;
//    }
//
////    static void runTutland(Activity iActivity) {
////        // Get paired devices.
////        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
////        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
////        if (pairedDevices.size() > 0) {
////            // There are paired devices. Get the name and address of each paired device.
////            for (BluetoothDevice device : pairedDevices) {
////                String deviceName = device.getName();
////                String deviceHardwareAddress = device.getAddress(); // MAC address
////                Logg.i(TAG, deviceName + " " + deviceHardwareAddress);
////            }
////        }
////    }
//
//    static String getMac() {
//        return "E0:F8:48:51:03:2D";
//    }
//
//    public static ArrayList<Integer> getGameControllerIds() {
//        ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
//        int[] deviceIds = InputDevice.getDeviceIds();
//        for (int deviceId : deviceIds) {
//            InputDevice dev = InputDevice.getDevice(deviceId);
//            int sources = dev.getSources();
//
//            // Verify that the device has gamepad buttons, control sticks, or both.
//            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
//                    || ((sources & InputDevice.SOURCE_JOYSTICK)
//                    == InputDevice.SOURCE_JOYSTICK)) {
//                // This device is a game controller. Store its device ID.
//                if (!gameControllerDeviceIds.contains(deviceId)) {
//                    gameControllerDeviceIds.add(deviceId);
//                }
//            }
//        }
//        return gameControllerDeviceIds;
//    }
//
//    public static void execute(Activity iActivity) {
//        ArrayList<Integer> tAR = getGameControllerIds();
//        if (tAR != null && tAR.size() > 0) {
//            for (Integer lId : tAR) {
//                Logg.i(TAG, "Game:" + lId);
//            }
//        } else {
//            Logg.i(TAG, "Got nothing");
//        }
//    }
//
//    public static void execute(Application iApplication) {
//        ArrayList<Integer> tAR = getGameControllerIds();
//        if (tAR != null && tAR.size() > 0) {
//            for (Integer lId : tAR) {
//                Logg.i(TAG, "Game:" + lId);
//            }
//        } else {
//            Logg.i(TAG, "Got nothing");
//        }
//    }
//
//
//    public static void exxxecute(Activity iActivity) {
//        Logg.i(TAG, "start PreparationOfBluetooth");
//        // runTutland(iActivity);
//
//        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//        BluetoothDevice tBluetoothDevice = getDevice();
//
//
//        UUID MY_UUID = UUID.fromString("00001108-0000-1000-8000-00805f9b34fb");
//
//        Logg.i(TAG, "BondState" + tBluetoothDevice.getBondState());
////        if (tBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
////            Method m = null;
////            try {
////                m = tBluetoothDevice.getClass()
////                        .getMethod("removeBond", (Class[]) null);
////                m.invoke(tBluetoothDevice, (Object[]) null);
////                Logg.i(TAG, "removce Bond");
////                Logg.i(TAG, "BondState" + tBluetoothDevice.getBondState());
////            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {   Logg.w(TAG, e.toString());
////            }
////
////        }
//
//        Logg.i(TAG, "HEALTH: " + btAdapter.getProfileConnectionState(BluetoothProfile.HEALTH));
//        Logg.i(TAG, "HEADSET: " + btAdapter.getProfileConnectionState(BluetoothProfile.HEADSET));
//        Logg.i(TAG, "A2DP: " + btAdapter.getProfileConnectionState(BluetoothProfile.A2DP));
//
//
//        BluetoothServerSocket mmServerSocket = null;
//        BluetoothSocket socket = null;
//        try {
//
//            mmServerSocket = btAdapter.listenUsingInsecureRfcommWithServiceRecord("MyService", MY_UUID);
//            Logg.i(TAG, "153: " + mmServerSocket.toString());
//
//            try {
//                socket = mmServerSocket.accept(2000);
//            } catch(IOException e) {
//                Logg.w(TAG, "timeout");
//            }
//            Logg.i(TAG, "156:" + mmServerSocket.toString());
//
//            //    mmServerSocket.c
//        } catch(IOException e) {
//            Logg.w(TAG, e.toString());
//        }
//        if (socket != null) {
//            Logg.i(TAG, socket.toString());
//
//            byte[] buffer = new byte[256];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//            try {
//                Logg.d(TAG, "Closing Server Socket.....");
//                mmServerSocket.close();
//
//                InputStream tmpIn = null;
//                OutputStream tmpOut = null;
//
//                // Get the BluetoothSocket input and output streams
//
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//
//                DataInputStream mmInStream = new DataInputStream(tmpIn);
//
//                DataOutputStream mmOutStream = new DataOutputStream(tmpOut);
//                // here you can use the Input Stream to take the string from the client  whoever is connecting
//                //similarly use the output stream to send the data to the client
//
//                // Read from the InputStream
//                bytes = mmInStream.read(buffer);
//                String readMessage = new String(buffer, 0, bytes);
//                // Send the obtained bytes to the UI Activity
//
//
//                Logg.i(TAG, readMessage);
//            } catch(Exception e) {
//
//                Logg.w(TAG, e.toString());
//            }
//        }
//
//
//        //mmServerSocket =  mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID); // you can also try  using In Secure connection...
//
//        // This is a blocking call and will only return on a
//        // successful connection or an exception
//
//
//        if (1 == 1) {
//            return;
//
//        }
//        BluetoothSocket tSocket = null;
//        try {
//            tSocket = tBluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//            Logg.i(TAG, tSocket.toString());
//        } catch(IOException e) {
//            Logg.w(TAG, e.toString());
//        }
//        if (tSocket != null) {
//            try {
//                Logg.i(TAG, "call connect");
//                tSocket.connect();
//            } catch(IOException e) {
//
//                Logg.w(TAG, e.toString());
//            }
//        }
//
//
//        // loggDevices(iActivity);
//    }
}
