package com.example.frontflash

import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

class FrontFlashTileService : TileService() {

    private val callback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            val cm = getSystemService(CAMERA_SERVICE) as CameraManager
            if (cameraId == FrontFlash.findFrontFlashId(cm)) {
                FrontFlash.set(this@FrontFlashTileService, enabled)
                render(enabled)
            }
        }
    }

    override fun onStartListening() {
        val cm = getSystemService(CAMERA_SERVICE) as CameraManager
        cm.registerTorchCallback(callback, Handler(Looper.getMainLooper()))
        render(FrontFlash.isOn)
    }

    override fun onStopListening() {
        (getSystemService(CAMERA_SERVICE) as CameraManager).unregisterTorchCallback(callback)
    }

    override fun onClick() {
        if (!FrontFlash.toggle(this)) {
            Toast.makeText(this, R.string.no_front_flash, Toast.LENGTH_LONG).show()
        }
        render(FrontFlash.isOn)
    }

    private fun render(on: Boolean) {
        qsTile?.apply {
            state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }
}
