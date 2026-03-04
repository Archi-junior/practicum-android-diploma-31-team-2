package ru.practicum.android.diploma.data.impl

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.repository.ShareDataRepository

class ShareDataRepositoryImpl(
    private val context: Context
) : ShareDataRepository {

    override suspend fun shareUrl(url: String, titleResId: Int) {
        val title = context.getString(titleResId)
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            val chooserIntent = Intent.createChooser(shareIntent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            Log.e("ShareDataRepository", "No app found to share", e)
            showToast(R.string.error_no_app_to_share)
        }
    }

    override suspend fun openEmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("ShareDataRepository", "No email app found", e)
            showToast(R.string.error_no_email_app)
        }
    }

    override suspend fun call(number: String) {
        try {
            val telNumber = if (number.startsWith("tel:")) number else "tel:$number"
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(telNumber)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Log.e("ShareDataRepository", "No dialer app found", e)
            showToast(R.string.error_no_dial_app)
        } catch (e: SecurityException) {
            Log.e("ShareDataRepository", "No permission to call", e)
            showToast(R.string.error_call_permission)
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }
}
