package de.rw22.butterflyiii.persistence

import android.content.Context
import de.rw22.butterflyiii.MainActivity
import de.rw22.butterflyiii.R
import java.io.File
import java.io.FileWriter

public class Config(private val context: Context) {

    companion object {
        const val FILE_NAME = "config.json"
    }

    public fun checkFile(tryCreateFromTemplate: Boolean): Boolean {
        val file = File(context.filesDir, FILE_NAME)
        if(tryCreateFromTemplate && !file.exists()) {
            val txt = loadTemplate()
            write(txt, false)
        }

        return file.exists()
    }

    public fun write(content: String, append: Boolean = false): Boolean {
        var success: Boolean
        val file = File(context.filesDir, FILE_NAME)
        FileWriter(file, append).use {
            try {
                it.write(content)
                success = true
            } catch (e: Exception) {
                success = false
            }
        }
        return success
    }

    public fun resetToTemplate(): Boolean {
        return write(loadTemplate())
    }

    public fun loadTemplate(): String = context.resources.openRawResource(R.raw.config_template).bufferedReader().readText()
}