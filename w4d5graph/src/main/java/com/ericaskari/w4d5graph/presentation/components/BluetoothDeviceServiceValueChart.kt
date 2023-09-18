package com.ericaskari.w4d5graph.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ericaskari.w4d5graph.application.data.AppViewModelProvider
import com.ericaskari.w4d5graph.bluetoothdeviceservicevalue.BluetoothDeviceServiceValueViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.flow.map


@Composable
fun BluetoothDeviceServiceValueChart(
    viewModel: BluetoothDeviceServiceValueViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val values = viewModel.getAllItemsStream().map {
        return@map entryModelOf(*it.map { it.integerValue.toFloat() }.asReversed().toTypedArray())
    }.collectAsState(initial = entryModelOf(0F))

    Chart(
        chart = lineChart(),
        model = values.value,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
    )
}
