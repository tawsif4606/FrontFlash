package com.example.frontflash

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

/** Finds the front camera that exposes a flash unit and toggles its torch. */
object FrontFlash {
    @Volatile var isOn = false
        private set

    fun findFrontFlashId(cm: CameraManager): String? =
        cm.cameraIdList.firstOrNull { id ->
            val c = cm.getCameraCharacteristics(id)
            c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT &&
                c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }

    /** Returns true on success, false if the ROM exposes no front torch. */
    fun set(context: Context, on: Boolean): Boolean {
        val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val id = try { findFrontFlashId(cm) } catch (e: Exception) { null } ?: return false
        return try {
            cm.setTorchMode(id, on)
            isOn = on
            true
        } catch (e: Exception) {
            false
        }
    }

    fun toggle(context: Context): Boolean = set(context, !isOn)
}
