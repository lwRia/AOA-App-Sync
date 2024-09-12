package com.app.appsync.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.app.appsync.R
import com.app.appsync.services.DownloadImageTask
import org.json.JSONObject

class MaintenanceActivity : AppCompatActivity() {
    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)
        try {
            val bundle = this.intent.extras
            val data = bundle!!.getString("res") // NullPointerException.

            val jsonObject = JSONObject(data.toString())
            val isMaintenance = jsonObject.getBoolean("isMaintenance")
            if (isMaintenance) {
                val linearLayout = findViewById<LinearLayout>(R.id.ll_root)
                val imgIcon = findViewById<ImageView>(R.id.img_icon)
                val txtTitleMaintain = findViewById<TextView>(R.id.txt_title_maintain)
                val txtDesMaintain = findViewById<TextView>(R.id.txt_des_maintain)

                val maintenanceData = jsonObject.getJSONObject("maintenanceData")
                if (maintenanceData.toString() != "{}") {
                    val title = maintenanceData.getString("title") ?: ""
                    val description = maintenanceData.getString("description") ?: ""
                    val image = maintenanceData.getString("image") ?: ""
                    val textColorCode = maintenanceData.getString("textColorCode") ?: ""
                    val backgroundColorCode = maintenanceData.getString("backgroundColorCode") ?: ""

                    if (backgroundColorCode.isNotEmpty()) {
                        linearLayout.setBackgroundColor(
                            Color.parseColor(
                                backgroundColorCode
                            )
                        )
                    }
                    if (textColorCode.isNotEmpty()) {
                        txtTitleMaintain.setTextColor(Color.parseColor(textColorCode))
                        txtDesMaintain.setTextColor(Color.parseColor(textColorCode))
                    }
                    if (image.isNotEmpty()) {
                        DownloadImageTask(imgIcon)
                            .execute(image)
                    }
                    if (description.isNotEmpty()) {
                        txtDesMaintain.text = description
                    }
                    if (title.isNotEmpty()) {
                        txtTitleMaintain.text = title
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }
}
