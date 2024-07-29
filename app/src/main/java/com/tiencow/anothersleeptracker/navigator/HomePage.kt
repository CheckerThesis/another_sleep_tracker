package com.tiencow.anothersleeptracker.navigator

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.tiencow.anothersleeptracker.StopwatchViewModel
import java.time.Duration
import kotlin.math.ceil
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun HomePage(
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    val time by viewModel.time.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()

    val timeEntries by viewModel.timeEntries.collectAsState(initial = emptyList())
    val averageDuration: Duration = timeEntries
        .map { it.duration }
        .let { durations ->
            if (durations.isEmpty()) Duration.ZERO
            else Duration.ofNanos(durations.map { it.toNanos() }.average().toLong())
        }
    val averageHours = averageDuration.toHours()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
//        Text(
//            "Average duration of sleep: $averageHours"
//        )
//        Graph(
//            viewModel = viewModel
//        )
        Spacer(modifier = Modifier.height(7.dp))
        Box {
            Graph(
                viewModel = viewModel
            )
            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = "Average duration of sleep: $averageHours",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (isRunning) time else "00:00:00",
            fontSize = 50.sp
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun Graph(viewModel: StopwatchViewModel = hiltViewModel()) {
    val pointsData = mutableListOf<Point>()
    val timeEntries by viewModel.timeEntries.collectAsState(initial = emptyList())
    println("Home: $timeEntries")

    // Add standalone points
    val standalonePoints = listOf(Point(0f, 10f), Point(0f, 0f))
    pointsData.addAll(standalonePoints)

    // Add connected points
    for (i in timeEntries.indices) {
        pointsData.add(
            Point(
                x = i.toFloat(),
                y = round(timeEntries[i].duration.toHours().toDouble()).toFloat()
            )
        )
    }

    if (pointsData.isEmpty()) {
        pointsData.add(Point(0f, 0f))
    }

    val steps = 5
    val maxYValue = pointsData.maxOfOrNull { it.y } ?: 0f

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(MaterialTheme.colorScheme.background)
        .axisLabelColor(MaterialTheme.colorScheme.secondary)
        .steps(pointsData.size - 1)
        .labelData { i -> (pointsData.size - 2 - i).toString() }
        .labelAndAxisLinePadding(15.dp)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .axisLabelColor(MaterialTheme.colorScheme.secondary)
        .backgroundColor(MaterialTheme.colorScheme.background)
        .labelData { i ->
            val yScale = maxYValue / steps
            (i * yScale).toInt().toString()
        }.build()

    val lines = mutableListOf<Line>()

    // Only add the connected points line if there are points to connect
    if (pointsData.size > 2) {
        lines.add(
            Line(
                dataPoints = pointsData.drop(2), // Exclude the first two standalone points
                lineStyle = LineStyle(color = MaterialTheme.colorScheme.primary),
                intersectionPoint = IntersectionPoint(color = MaterialTheme.colorScheme.primary),
                selectionHighlightPoint = SelectionHighlightPoint(),
                shadowUnderLine = ShadowUnderLine(),
                selectionHighlightPopUp = SelectionHighlightPopUp()
            )
        )
    }

    // Add standalone points as individual points
    standalonePoints.forEach { point ->
        lines.add(
            Line(
                dataPoints = listOf(point),
                lineStyle = LineStyle(color = Color.Transparent), // Invisible line
                intersectionPoint = IntersectionPoint(
                    color = Color.Transparent,
                    radius = 8.dp
                )
            )
        )
    }

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(lines = lines),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(),
        backgroundColor = MaterialTheme.colorScheme.background
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(285.dp),
        lineChartData = lineChartData
    )
}