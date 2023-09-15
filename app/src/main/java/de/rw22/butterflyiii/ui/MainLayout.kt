package de.rw22.butterflyiii.ui

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.isDigitsOnly
import de.rw22.butterflyiii.R
import de.rw22.butterflyiii.ext.clickableWithoutRipple
import de.rw22.butterflyiii.persistence.Config
import de.rw22.butterflyiii.ui.theme.AppTheme
import java.io.File
import java.io.FileWriter
import java.lang.StringBuilder
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Preview(showBackground = true, showSystemUi = true, name = "Light")
@Preview(showBackground = true, showSystemUi = true, name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MainLayout() {
    val ctx: Context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val surfaceInteractionSource = remember { MutableInteractionSource() }

     AppTheme {
        Surface(
                tonalElevation = 2.5.dp,
                modifier = Modifier.clickableWithoutRipple(
                    interactionSource = surfaceInteractionSource,
                    onClick = {
                        focusManager.clearFocus()
                    }
                )
            ) {
            ConstraintLayout (
                modifier = Modifier.fillMaxSize()
            ) {
                val (heading, toggle, input, portInput, tokenInput, cfgLabel, expBtn,div1, impBtn, div2, resetBtn) = createRefs()

                Text(
                    text = "Importer Config",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.constrainAs(heading) {
                        top.linkTo(parent.top, margin = 24.dp)
                        start.linkTo(parent.start, margin = 24.dp)
                    }
                )

                var useHttps by rememberSaveable { mutableStateOf(
                    if(ctx is Activity) {
                        ctx.getPreferences(Context.MODE_PRIVATE).getBoolean(ctx.getString(R.string.preference_https), false)
                    } else false
                ) }
                val lineColor = MaterialTheme.colorScheme.onSurfaceVariant
                IconButton(
                    onClick = { useHttps = !useHttps },
                    modifier = Modifier
                        .constrainAs(toggle) {
                            start.linkTo(parent.start, margin = 24.dp)
                            top.linkTo(input.top)
                            bottom.linkTo(input.bottom)
                            height = Dimension.fillToConstraints
                        }
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 3.dp, bottomStart = 3.dp))
                        .drawBehind {
                            val strokeWidth = 3f
                            val y = size.height - strokeWidth / 2

                            drawLine(
                                lineColor,
                                Offset(0f, y),
                                Offset(size.width, y),
                                strokeWidth
                            )
                        }
                ) {
                    if(useHttps)
                        Icon(Icons.Filled.Lock, contentDescription = "Protocol")
                    else
                        Icon(Icons.Outlined.Lock, contentDescription = "Protocol")
                }

                var ip by rememberSaveable { mutableStateOf(
                    if(ctx is Activity) {
                        ctx.getPreferences(Context.MODE_PRIVATE).getString(ctx.getString(R.string.preference_ip), null) ?: ""
                    } else ""
                ) }
                TextField(
                    value = ip,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, autoCorrect = true),
                    shape = RoundedCornerShape(0.dp),
                    onValueChange = {
                        if(validateIPv4(it))
                            ip = it
                        else if(it.isEmpty())
                            ip = it
                    },
                    label = { Text("Firefly Importer IP") },
                    singleLine = true,
                    modifier = Modifier.constrainAs(input) {
                        top.linkTo(heading.bottom, margin = 32.dp)
                        start.linkTo(toggle.end)
                        end.linkTo(portInput.start)
                        width = Dimension.fillToConstraints
                    }
                )


                var port by rememberSaveable { mutableStateOf(
                    if(ctx is Activity) {
                        ctx.getPreferences(Context.MODE_PRIVATE).getInt(ctx.getString(R.string.preference_port), -1).let {
                            if(it == -1) "" else "$it"
                        }
                    } else ""
                ) }
                TextField(
                    value = port,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, autoCorrect = false),
                    shape = RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp),
                    onValueChange = {
                        if(it == "" || (it.isDigitsOnly() && Integer.parseInt(it) in 1..<65535))
                            port = it
                    },
                    label = { Text("Port") },
                    singleLine = true,
                    modifier = Modifier
                        .constrainAs(portInput) {
                            end.linkTo(parent.end, margin = 24.dp)
                            top.linkTo(toggle.top)
                            bottom.linkTo(toggle.bottom)
                        }
                        .clip(RoundedCornerShape(bottomEnd = 3.dp))
                        .width(90.dp)
                )

                var accessToken by rememberSaveable { mutableStateOf(
                    if(ctx is Activity) {
                        ctx.getPreferences(Context.MODE_PRIVATE).getString(ctx.getString(R.string.preference_token), null) ?: ""
                    } else ""
                ) }
                OutlinedTextField(
                    singleLine = true,
                    label = { Text("Personal Access Token") },
                    value = accessToken,
                    onValueChange = { accessToken = it.trim() },
                    modifier = Modifier.constrainAs(tokenInput) {
                        start.linkTo(toggle.start)
                        end.linkTo(portInput.end)
                        width = Dimension.fillToConstraints

                        top.linkTo(input.bottom, margin = 32.dp)
                    }
                )

                Text(
                    "config.json",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.constrainAs(cfgLabel) {
                        top.linkTo(tokenInput.bottom, margin = 48.dp)
                        start.linkTo(toggle.start)
                    }
                )

                val exportFile = rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("application/json"), onResult = {
                    it?.let { uri ->
                        val stream = ctx.contentResolver.openOutputStream(uri)
                        stream?.use { outputStream ->
                            outputStream.write(File(ctx.filesDir, Config.FILE_NAME).readBytes())
                            outputStream.flush()
                        }
                    }
                })
                ListItem(
                    headlineContent = { Text("Export") },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.file_export_black_24dp),
                            contentDescription = "Export"
                        )
                    },
                    modifier = Modifier
                        .constrainAs(expBtn) {
                            top.linkTo(cfgLabel.bottom, margin = 4.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .clip(RoundedCornerShape(5.dp))
                        .clickable(onClick = {
                            exportFile.launch("config.json")
                        })
                        .padding(horizontal = 8.dp)
                )

                HorizontalDivider(modifier = Modifier.constrainAs(div1) {
                    top.linkTo(expBtn.bottom)
                    start.linkTo(toggle.start)
                    end.linkTo(portInput.end)
                    width = Dimension.fillToConstraints
                })

                val importFile = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(), onResult = {
                    it?.let { uri ->
                        val stream = ctx.contentResolver.openInputStream(uri)
                        stream?.use { inputStream ->
                            val txt = inputStream.bufferedReader().readText()
                            Config(ctx).write(txt)
                        }
                    }
                })
                ListItem(
                    headlineContent = { Text("Import") },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.file_import_black_24dp),
                            contentDescription = "Export"
                        )
                    },
                    modifier = Modifier
                        .constrainAs(impBtn) {
                            top.linkTo(div1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .clip(RoundedCornerShape(5.dp))
                        .clickable(onClick = {
                            importFile.launch("application/json")
                        })
                        .padding(horizontal = 8.dp)
                )

                HorizontalDivider(modifier = Modifier.constrainAs(div2) {
                    top.linkTo(impBtn.bottom)
                    start.linkTo(toggle.start)
                    end.linkTo(portInput.end)
                    width = Dimension.fillToConstraints
                })

                ListItem(
                    headlineContent = { Text("Reset") },
                    leadingContent = {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = "Export"
                        )
                    },
                    modifier = Modifier
                        .constrainAs(resetBtn) {
                            top.linkTo(div2.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .clip(RoundedCornerShape(5.dp))
                        .clickable(onClick = {
                            Config(ctx).resetToTemplate()
                            Toast
                                .makeText(
                                    ctx,
                                    "Successfully reset to default.",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        })
                        .padding(horizontal = 8.dp)
                )

                var showSheet by rememberSaveable { mutableStateOf(false) }

                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = {
                        // Save all current user input values, then open the upload dialog
                        val sharedPref = (ctx as Activity)?.getPreferences(Context.MODE_PRIVATE) ?: return@ExtendedFloatingActionButton
                        with (sharedPref.edit()) {
                            putBoolean(ctx.getString(R.string.preference_https), useHttps)
                            putString(ctx.getString(R.string.preference_ip), ip)
                            putInt(ctx.getString(R.string.preference_port), if(port.isNotEmpty()) Integer.parseInt(port) else 0)
                            putString(ctx.getString(R.string.preference_token), accessToken)
                            apply()
                        }

                        showSheet = true
                    },
                    modifier = Modifier.constrainAs(createRef()) {
                        end.linkTo(parent.end, margin = 24.dp)
                        bottom.linkTo(parent.bottom, margin = 24.dp)
                    }
                ) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "Add"
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text("Add File")
                }

                if(showSheet) {
                    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                    ModalBottomSheet(
                        onDismissRequest = { showSheet = false },
                        sheetState = state,
                        windowInsets = BottomSheetDefaults.windowInsets
                    ) {
                        // Custom layout for the dialog
                        Surface(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            ConstraintLayout(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                val (title, chooseBtn, uploadBtn, div, previewTxt) = createRefs()
                                var csvUri: Uri by remember { mutableStateOf(Uri.EMPTY) }

                                Text(
                                    text = "Upload transactions",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.constrainAs(title) {
                                        start.linkTo(parent.start, margin = 24.dp)
                                        top.linkTo(parent.top)
                                    }
                                )

                                var selectedFileName by rememberSaveable { mutableStateOf("Click to select transaction csv") }
                                val lineCount by rememberSaveable { mutableStateOf(0) }
                                val chooseFile = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(), onResult = {
                                    it?.let { uri ->
                                        queryName(ctx.contentResolver, uri)?.let { name ->
                                            selectedFileName = name
                                        }
                                        val stream = ctx.contentResolver.openInputStream(uri)
                                        stream?.use { inputStream ->
                                            val lineCount = inputStream.bufferedReader().readLines().size
                                        }
                                        csvUri = uri
                                    }
                                })

                                ListItem(
                                    headlineContent = { Text("Selected File") },
                                    supportingContent = { Text(selectedFileName, fontStyle = FontStyle.Italic, overflow = TextOverflow.Ellipsis) },
                                    leadingContent = { Icon(
                                        painterResource(id = R.drawable.insert_drive_file_24),
                                        contentDescription = "Export"
                                    ) },
                                    modifier = Modifier
                                        .constrainAs(chooseBtn) {
                                            top.linkTo(title.bottom, margin = 32.dp)
                                            start.linkTo(parent.start)
                                            end.linkTo(parent.end)

                                            width = Dimension.fillToConstraints
                                        }
                                        .height(64.dp)
                                        .clickable(
                                            role = Role.Button,
                                            onClick = {
                                                chooseFile.launch("text/comma-separated-values")
                                            }
                                        )
                                        .padding(horizontal = 12.dp)
                                )

                                HorizontalDivider(modifier = Modifier.constrainAs(div) {
                                    top.linkTo(chooseBtn.bottom, margin = 16.dp)
                                })

                                if(selectedFileName.endsWith(".csv")) {
                                    var allLines: List<String> by remember { mutableStateOf(listOf()) }
                                    ctx.contentResolver.openInputStream(csvUri)?.use {
                                        allLines = it.bufferedReader().readLines()
                                    }

                                    Surface(
                                        tonalElevation = 10.dp,
                                        modifier = Modifier.constrainAs(createRef()) {
                                            top.linkTo(div.bottom, margin = 16.dp)
                                            bottom.linkTo(uploadBtn.top, margin = 16.dp)
                                            start.linkTo(parent.start, margin = 24.dp)
                                            end.linkTo(parent.end, margin = 24.dp)
                                            width = Dimension.fillToConstraints
                                            height = Dimension.fillToConstraints
                                        },
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Box(modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp), contentAlignment = Alignment.Center) {
                                            val (dt, ct) = filterTransactionFile(allLines)

                                            Text(
                                                text = dt.joinToString(separator = "\n") + "\n" + ct.toMutableList().also { it.removeAt(0) }.joinToString(separator = "\n"),
                                                color = MaterialTheme.colorScheme.onSurface, ,
                                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 25.sp),
                                                modifier = Modifier
                                                    .verticalScroll(rememberScrollState())
                                                    .horizontalScroll(rememberScrollState())
                                            )
                                        }
                                    }
                                    FilledTonalButton(
                                        onClick = {
                                            val (debitTable, creditTable) = filterTransactionFile(allLines)

                                            var toastTxt: String = if(uploadToFirefly(ctx, debitTable, creditTable.toMutableList().also { it.removeAt(0) })) "Yay!" else "Nay :("
                                            Toast.makeText(ctx, toastTxt, Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier.constrainAs(uploadBtn) {
                                            start.linkTo(parent.start, margin = 24.dp)
                                            end.linkTo(parent.end, margin = 24.dp)
                                            bottom.linkTo(parent.bottom, margin = 24.dp)

                                            width = Dimension.fillToConstraints
                                        }
                                    ) {
                                        Text("Upload")
                                    }
                                } else {
                                    Surface(
                                        tonalElevation = 10.dp,
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier
                                            .constrainAs(createRef()) {
                                                start.linkTo(parent.start, margin = 48.dp)
                                                end.linkTo(parent.end, margin = 48.dp)
                                                top.linkTo(div.bottom)
                                                bottom.linkTo(parent.bottom)

                                                width = Dimension.fillToConstraints
                                            }
                                            .height(200.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Choose file to continue.",
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

val pattern = Regex("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")
private fun validateIPv4(ip : String) : Boolean {
    return pattern.matches(ip)
}

private fun queryName(resolver: ContentResolver, uri: Uri): String? {
    val returnCursor = resolver.query(uri, null, null, null, null)!!
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}

private fun filterTransactionFile(allFileLines: List<String>): Pair<List<String>, List<String>> {
    val debitLines: MutableList<String> = mutableListOf()
    val creditLines: MutableList<String> = mutableListOf()
    for(line in allFileLines) {
        val segments = line.split(";").toMutableList()
        if (segments.size == 6)
            debitLines.add(line)
        else if(segments.size == 7) {
            segments.removeAt(3)
            var tmp: String = ""
            for(seg in segments)
                if (seg != "") tmp += "$seg;"
            creditLines.add(tmp)
        }
    }

    return Pair(debitLines, creditLines)
}

private fun uploadToFirefly(ctx: Context, debitTableLines: List<String>, creditTableLines: List<String>): Boolean {
    val builder: StringBuilder = StringBuilder()
    builder.append(debitTableLines.joinToString(separator = "\n"))
    builder.append("\n")
    builder.append(creditTableLines.joinToString(separator = "\n"))
    val transactionTable = builder.toString()
    val tableFile = File(ctx.filesDir, "csv.csv")
    FileWriter(tableFile, false).write(transactionTable)

    val configFile = File(ctx.filesDir, Config.FILE_NAME)

    val sharedPref = (ctx as Activity)?.getPreferences(Context.MODE_PRIVATE) ?: return false
    val proto: String = if(sharedPref.getBoolean("de.rw22.butterflyiii.useHttps", false)) "https" else "http"
    val ip: String = sharedPref.getString("de.rw22.butterflyiii.fireflyIp", null) ?: return false
    val port: Int = sharedPref.getInt("de.rw22.butterflyiii.importerPort", -1); if (port == -1) return false

    val url: String = "$proto://$ip:$port/autoupload?secret=YOURSECRETHERE"



    return true
}