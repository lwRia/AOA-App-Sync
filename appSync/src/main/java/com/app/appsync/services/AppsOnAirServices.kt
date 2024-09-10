package com.app.appsync.services

import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.aoacore.services.CoreService
import com.app.aoacore.services.NetworkService
import com.app.appsync.BuildConfig
import com.app.appsync.R
import com.app.appsync.activities.AppUpdateActivity
import com.app.appsync.activities.MaintenanceActivity
import com.app.appsync.interfaces.UpdateCallBack
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AppsOnAirServices {
    companion object {
        private var appId: String = ""
        private var showNativeUI: Boolean = true
        private var isResponseReceived: Boolean = false
        private const val TAG = "AppsOnAirServices"

        private fun getResponse(
            response: Response,
            context: Context,
            callBack: UpdateCallBack? = null,
            isFromCDN: Boolean
        ) {
            try {
                if (response.code == 200) {
                    val myResponse = response.body!!.string()
                    val jsonObject = JSONObject(myResponse)
                    val updateData = jsonObject.getJSONObject("updateData")
                    val isAndroidUpdate = updateData.getBoolean("isAndroidUpdate")
                    val isMaintenance = jsonObject.getBoolean("isMaintenance")
                    if (isAndroidUpdate) {
                        val isAndroidForcedUpdate = updateData.getBoolean("isAndroidForcedUpdate")
                        val androidBuildNumber = updateData.getString("androidBuildNumber")
                        val info = context.packageManager.getPackageInfo(context.packageName, 0)
                        @Suppress("DEPRECATION") val versionCode = info.versionCode
                        var buildNum = 0

                        @Suppress("SENSELESS_COMPARISON")
                        if (androidBuildNumber != null) {
                            buildNum = androidBuildNumber.toInt()
                        }
                        val isUpdate = versionCode < buildNum
                        if (showNativeUI && isUpdate && (isAndroidForcedUpdate || isAndroidUpdate)) {
                            val intent = Intent(context, AppUpdateActivity::class.java)
                            intent.putExtra("res", myResponse)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        }
                    } else if (isMaintenance && showNativeUI) {
                        val intent = Intent(context, MaintenanceActivity::class.java)
                        intent.putExtra("res", myResponse)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                    callBack?.onSuccess(myResponse)
                    isResponseReceived = true
                } else if (isFromCDN) {
                    callServiceApi(context, callBack)
                }
            } catch (e: Exception) {
                callBack?.onFailure(e.message)
                isResponseReceived = true
                Log.d(TAG, "getResponse: " + e.message)
            }
        }

        private fun callCDNServiceApi(context: Context, callBack: UpdateCallBack? = null) {
            val baseUrl = BuildConfig.CDN_BASE_URL

            val pathSegment = buildString {
                append(appId)
                append(".json")
            }

            val urlBuilder: HttpUrl.Builder = baseUrl.toHttpUrlOrNull()!!.newBuilder()
            val unixTime = System.currentTimeMillis() / 1000L
            // Add the path segment
            urlBuilder.addPathSegment(pathSegment)
            // Add query parameters
            urlBuilder.addQueryParameter("now", unixTime.toString())
            // Build the URL with query parameters
            val url: String = urlBuilder.build().toString()

            val client = OkHttpClient().newBuilder().build()
            val request: Request = Request.Builder().url(url).method("GET", null).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "onFailure: AppsOnAirCDNApi" + e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    getResponse(response, context, callBack, true)
                }
            })
        }

        private fun callServiceApi(context: Context, callBack: UpdateCallBack? = null) {
            val url: String = buildString {
                append(BuildConfig.BASE_URL)
                append(appId)
            }
            val client = OkHttpClient().newBuilder().build()
            val request: Request = Request.Builder().url(url).method("GET", null).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "onFailure: AppsOnAirServiceApi" + e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    getResponse(response, context, callBack, false)
                }
            })
        }

        fun sync(
            context: Context,
            callBack: UpdateCallBack? = null,
            options: Map<String, Any> = emptyMap()
        ) {
            val applicationId: String = CoreService.getAppId(context)
            appId = applicationId

            if (appId.isEmpty()) {
                Log.d(TAG, "ShakeBug AppId: " + context.getString(R.string.error_something_wrong))
            } else {
                if (options.isNotEmpty()
                    && options.containsKey(key = "showNativeUI")
                    && options["showNativeUI"] is Boolean
                ) {
                    showNativeUI = options["showNativeUI"] as Boolean
                }

                NetworkService.checkConnectivity(
                    context
                ) { isAvailable: Boolean ->
                    run {
                        if (isAvailable) {
                            if (!isResponseReceived) {
                                callCDNServiceApi(context, callBack)
                            }
                        } else {
                            Log.d(TAG, "sync: Please check your internet connection!")
                        }
                    }
                }
            }
        }
    }
}
