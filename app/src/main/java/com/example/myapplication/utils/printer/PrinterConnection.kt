package com.example.myapplication.utils.printer

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.data.source.preference.SettingPref
import java.io.IOException


class PrinterConnection(private val activity: FragmentActivity) {

    private var setting: SettingPref = SettingPref(activity)

    companion object {
        var mbtSocket: BluetoothSocket? = null

        fun setSocketFromDevice(context: Context, device: BluetoothDevice) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val uuid = device.uuids[0].uuid
                try {
                    mbtSocket = device.createRfcommSocketToServiceRecord(uuid)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getDevice(callback: (BluetoothSocket?) -> Unit) {
        val address = setting.printerDevice
        if (!address.isNullOrEmpty()) {
            val device = getDeviceFromAddress(address)
            if (device != null) {
                setSocketFromDevice(activity, device)
                if (mbtSocket != null) {
                    connectSocket(mbtSocket!!, callback)
                } else {
                    callback.invoke(null)
                }
            } else {
                callback.invoke(null)
            }
        } else {
            callback.invoke(null)
        }
    }

    private fun getDeviceFromAddress(address: String): BluetoothDevice? {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return if (adapter != null) {
            adapter.getRemoteDevice(address)
        } else {
            showToast("Bluetooth tidak aktif")
            null
        }
    }

    private fun connectSocket(socket: BluetoothSocket, callback: (BluetoothSocket?) -> Unit) {
        Thread {
            try {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    socket.connect()
                    activity.runOnUiThread {
                        showToast("Printing")
                    }

                    callback.invoke(mbtSocket)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                try {
                    socket.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                Log.e("DeviceConnection", "getDevice connect ${ex.message}")
                mbtSocket = null
                activity.runOnUiThread {
                    showToast("Error print, periksa perangkat perinter lalu coba kembali")
                }
                callback.invoke(null)
            }
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
