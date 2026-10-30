package com.woovi.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.woovi.app.core.util.DateFormats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    value: String?,
    onValueChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            onClick = { showPicker = true },
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(if (value == null) "$label: elegir" else "$label: ${DateFormats.toDisplay(value)}")
        }
        if (value != null) {
            TextButton(onClick = { onValueChange(null) }) { Text("Quitar") }
        }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = value?.let { DateFormats.toMillis(it) }
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { onValueChange(DateFormats.fromMillis(it)) }
                    showPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}