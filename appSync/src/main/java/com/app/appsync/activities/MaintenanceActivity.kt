package com.app.appsync.activities

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
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
                val maintenanceLayout = findViewById<LinearLayout>(R.id.ll_root)
                val maintenanceLayout1 = findViewById<LinearLayout>(R.id.lls_root)
                maintenanceLayout.visibility = View.GONE
                maintenanceLayout1.visibility = View.GONE
                val maintenanceData = jsonObject.getJSONObject("maintenanceData")
                if (maintenanceData.toString() != "{}") {
                    val title = maintenanceData.getString("title")
                    val description = maintenanceData.getString("description")
                    val image = maintenanceData.getString("image")
                    val textColorCode = maintenanceData.getString("textColorCode")
                    val backgroundColorCode = maintenanceData.getString("backgroundColorCode")

                    if (title != "" && description != "") {
                        maintenanceLayout.visibility = View.VISIBLE

                        if (backgroundColorCode != "") {
                            maintenanceLayout.setBackgroundColor(
                                Color.parseColor(
                                    backgroundColorCode
                                )
                            )
                        }
                        val imgIcon = findViewById<ImageView>(R.id.img_icon)
                        val txtTitleMaintain = findViewById<TextView>(R.id.txt_title_maintain)
                        val txtDesMaintain = findViewById<TextView>(R.id.txt_des_maintain)
                        val txtAppName = findViewById<TextView>(R.id.txt_app_name)
                        @Suppress("SENSELESS_COMPARISON")
                        if (image != "" && image != "null" && image != null) {
                            DownloadImageTask(imgIcon)
                                .execute(image)
                        } else {
                            imgIcon.setImageResource(R.drawable.maintenance_icon)
                        }
                        txtTitleMaintain.text = title
                        txtDesMaintain.text = description

                        val ai = packageManager.getApplicationInfo(
                            packageName,
                            PackageManager.GET_META_DATA
                        )
                        val bundle1 = ai.metaData
                        val appName = bundle1.getString("com.appsonair.name")
                        txtAppName.text = appName
                        if (textColorCode !== "") {
                            txtTitleMaintain.setTextColor(Color.parseColor(textColorCode))
                            txtDesMaintain.setTextColor(Color.parseColor(textColorCode))
                            txtAppName.setTextColor(Color.parseColor(textColorCode))
                        }
                    } else {
                        maintenanceLayout1.visibility = View.VISIBLE
                        val imgIcon = findViewById<ImageView>(R.id.img2_icon)
                        val txtTitle2Maintain = findViewById<TextView>(R.id.txt_title2_maintain)

                        val ai = packageManager.getApplicationInfo(
                            packageName,
                            PackageManager.GET_META_DATA
                        )
                        val bundle1 = ai.metaData
                        val appName = bundle1.getString("com.appsonair.name")
                        txtTitle2Maintain.text = buildString {
                            append(appName)
                            append(" ")
                            append(getString(R.string.maintenance))
                        }

                        if (maintenanceData.toString() != "{}") {
                            @Suppress("SENSELESS_COMPARISON")
                            if (image != "" && image != "null" && image != null) {
                                DownloadImageTask(imgIcon)
                                    .execute(image)
                            } else {
                                imgIcon.setImageResource(R.drawable.maintenance_icon)
                            }
                        }
                    }
                } else {
                    maintenanceLayout1.visibility = View.VISIBLE
                    val imgIcon = findViewById<ImageView>(R.id.img2_icon)
                    val txtTitle2Maintain = findViewById<TextView>(R.id.txt_title2_maintain)

                    val ai =
                        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    val bundle1 = ai.metaData
                    val appName = bundle1.getString("com.appsonair.name")
                    txtTitle2Maintain.text = buildString {
                        append(appName)
                        append(" ")
                        append(getString(R.string.maintenance))
                    }

                    if (maintenanceData.toString() != "{}") {
                        val image = maintenanceData.getString("image")
                        @Suppress("SENSELESS_COMPARISON")
                        if (image != "" && image != "null" && image != null) {
                            DownloadImageTask(imgIcon)
                                .execute(image)
                        } else {
                            imgIcon.setImageResource(R.drawable.maintenance_icon)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {}
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }
}
