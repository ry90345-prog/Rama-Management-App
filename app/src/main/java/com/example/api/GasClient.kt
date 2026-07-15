package com.example.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object GasClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    suspend fun syncTable(url: String, sheetName: String, rowsArray: JSONArray): String = withContext(Dispatchers.IO) {
        if (url.isEmpty()) {
            return@withContext "Error: Google Apps Script Web App URL is not set in Settings."
        }

        try {
            val payload = JSONObject().apply {
                put("action", "sync")
                put("sheet", sheetName)
                put("rows", rowsArray)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = payload.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Sync Failed: HTTP ${response.code}"
                }
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                if (json.optString("status") == "success") {
                    "Success: ${json.optString("message")}"
                } else {
                    "Failed: ${json.optString("message")}"
                }
            }
        } catch (e: Exception) {
            "Error syncing $sheetName: ${e.localizedMessage ?: e.message}"
        }
    }

    suspend fun uploadFile(url: String, folderName: String, fileName: String, mimeType: String, base64Data: String): Pair<String, String>? = withContext(Dispatchers.IO) {
        if (url.isEmpty()) return@withContext null

        try {
            val payload = JSONObject().apply {
                put("action", "upload")
                put("folderName", folderName)
                put("fileName", fileName)
                put("mimeType", mimeType)
                put("base64Data", base64Data)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = payload.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: ""
                val json = JSONObject(body)
                if (json.optString("status") == "success") {
                    val fileId = json.optString("fileId")
                    val fileUrl = json.optString("fileUrl")
                    Pair(fileId, fileUrl)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}
