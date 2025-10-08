package com.randos.mealmap.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.randos.mealmap.utils.format
import java.time.LocalDate

@Composable
fun DateView(
    modifier: Modifier = Modifier,
    dateFrom: LocalDate, dateTo: LocalDate
) {
    AnimatedContent(
        modifier = modifier,
        targetState = dateFrom,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { dateFrom ->
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${dateFrom.format()} - ${dateTo.format()}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W600
        )
    }
}

@Preview
@Composable
private fun PreviewDateView() {
    DateView(dateFrom = LocalDate.now(), dateTo = LocalDate.now().plusWeeks(1))
}
