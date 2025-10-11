package com.randos.mealmap.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.randos.domain.model.Recipe
import com.randos.mealmap.utils.Utils.recipeToShareableText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ContextUtils {
    fun shareRecipe(context: Context, recipe: Recipe) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, recipeToShareableText(recipe))
            putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe!")
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share via")
        context.startActivity(shareIntent)
    }

    suspend fun copyUriToAppStorage(context: Context, uri: Uri): Uri? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val inputStream =
                    context.contentResolver.openInputStream(uri) ?: return@withContext null
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "meal_map_${System.currentTimeMillis()}.jpg"
                )
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}

fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Permissions should be called in an Activity context")
}