package de.rw22.butterflyiii

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.rw22.butterflyiii.ui.MainLayout
import de.rw22.butterflyiii.persistence.Config
import java.io.IOException
import java.io.OutputStream
import java.security.AccessController.getContext


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout()
        }

        if(Config(this).checkFile(true)) {
            Toast.makeText(this, "Found existing config file.", Toast.LENGTH_LONG).show()
        }
    }
}