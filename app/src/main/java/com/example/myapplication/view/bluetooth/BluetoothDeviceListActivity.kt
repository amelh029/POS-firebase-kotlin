package com.example.myapplication.view.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ActivityMessage
import com.example.myapplication.R
import com.example.myapplication.adapters.recyclerView.bluetooth.BluetoothDeviceAdapter
import com.example.myapplication.data.source.local.entity.helper.OrderWithProduct
import com.example.myapplication.data.source.local.entity.room.master.Store
import com.example.myapplication.data.source.preference.SettingPref
import com.example.myapplication.databinding.ActivityBluetoothDeviceListBinding
import com.example.myapplication.utils.printer.PrintBill

class BluetoothDeviceListActivity : ActivityMessage() {
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private lateinit var binding: ActivityBluetoothDeviceListBinding
    private lateinit var adapterBluetooth: BluetoothDeviceAdapter
    private lateinit var setting: SettingPref
    private lateinit var printBill: PrintBill

    private var order: OrderWithProduct? = null
    private var printType: Int = 0
    private var store: Store? = null


    companion object {
        const val REQUEST_ENABLE_BT = 0
        const val EXTRA_ORDER = "extra_order"
        const val EXTRA_PRINT = "extra_print"
        const val EXTRA_STORE = "extra_store"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        printBill = PrintBill(this)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        setting = SettingPref(this)
        adapterBluetooth = BluetoothDeviceAdapter(setting.printerDevice) { onChooseDevice(it) }

        binding.rvBtDvList.layoutManager = LinearLayoutManager(this)
        binding.rvBtDvList.adapter = adapterBluetooth

        binding.btnBtBack.setOnClickListener { onBackPressed() }

        order = intent.getSerializableExtra(EXTRA_ORDER) as OrderWithProduct?
        store = intent.getSerializableExtra(EXTRA_STORE) as Store?
        printType = intent.getIntExtra(EXTRA_PRINT, 0)

        checkBluetooth()
    }

    private fun checkBluetooth() {
        if (isBluetoothAvailable()) {
            if (mBluetoothAdapter!!.isEnabled) {
                getBoundDevice(mBluetoothAdapter!!)
            } else {
                forceToEnableBluetooth()
            }
        } else {
            showToast("Perangkat tidak mendukung bluetooth")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (!setting.printerDevice.isNullOrEmpty()) {
                printBill()
            } else {
                getBoundDevice(mBluetoothAdapter!!)
            }
        }
    }

    private fun getBoundDevice(bluetoothAdapter: BluetoothAdapter) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val boundedDevice = bluetoothAdapter.bondedDevices
            if (!boundedDevice.isNullOrEmpty()) {
                adapterBluetooth.setDevices(ArrayList(boundedDevice))
            }
        }
    }

    private fun forceToEnableBluetooth() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(reqCode, resultCode, intent)
        if (reqCode == REQUEST_ENABLE_BT) {
            if (mBluetoothAdapter!!.isEnabled) {
                getBoundDevice(mBluetoothAdapter!!)
            } else {
                showToast("You must enable bluetooth to use the print feature")
            }
        } else {
            showToast("You must enable bluetooth to use the print feature")
        }
    }

    private fun isBluetoothAvailable(): Boolean {
        return mBluetoothAdapter != null
    }

    private fun onChooseDevice(device: BluetoothDevice) {
        setting.printerDevice = device.address
        if (order == null) {
            finish()
            return
        }
        printBill()
    }

    private fun printBill() {
        showLoading(true)
        when (printType) {
            PrintBill.BILL -> {
                if (store != null) {
                    printBill.doPrintBill(order!!, store!!) {
                        if (it) {
                            finish()
                        } else {
                            showLoading(false)
                            showToast("An error occurred, try again")
                        }
                    }
                } else {
                    showLoading(false)
                    showToast("An error occurred, try again")
                }
            }
            PrintBill.QUEUE -> {
                printBill.doPrintQueue(order!!) {
                    if (it) {
                        finish()
                    } else {
                        showLoading(false)
                        showToast("An error occurred, try again")
                    }
                }
            }
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.contBtProgress.visibility = View.VISIBLE
            binding.rvBtDvList.visibility = View.INVISIBLE
            binding.btnBtBack.isEnabled = false
        } else {
            binding.contBtProgress.visibility = View.GONE
            binding.rvBtDvList.visibility = View.VISIBLE
            binding.btnBtBack.isEnabled = true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDiscovering()
        printBill.onDestroy()
    }

    private fun stopDiscovering() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mBluetoothAdapter?.cancelDiscovery()
        }
    }
}