package com.randos.mealmap.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.mealmap.utils.Constants.listOfWeeksAvailable

@Composable
fun WeekSelector(
    isSelectingWeek: Boolean,
    selectedWeek: String,
    onIsSelectingWeekUpdate: (Boolean) -> Unit,
    onSelectedWeekUpdate: (Int, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onIsSelectingWeekUpdate(true) }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            this@Card.AnimatedVisibility(
                visible = !isSelectingWeek,
                enter = fadeIn(),
                exit = fadeOut(animationSpec = tween(durationMillis = 100)),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            Column {
                listOfWeeksAvailable.forEachIndexed { index, (week, text) ->
                    AnimatedVisibility(
                        visible = selectedWeek == text || isSelectingWeek,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        WeekText(
                            text = text,
                            onClick = {
                                if (isSelectingWeek) {
                                    onSelectedWeekUpdate(week, text)
                                } else {
                                    onIsSelectingWeekUpdate(true)
                                }
                            }
                        )
                    }
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxWidth(),
                        visible = index < listOfWeeksAvailable.size - 1 && isSelectingWeek,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        HorizontalDivider(Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekText(text: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.W600
        )
    }
}

@Preview
@Composable
private fun PreviewWeekSelector() {
    WeekSelector(isSelectingWeek = false, selectedWeek = "This Week", onIsSelectingWeekUpdate = {
    }, onSelectedWeekUpdate = { _, _ -> })
}

@Preview
@Composable
private fun PreviewWeekSelectorIsSelecting() {
    WeekSelector(isSelectingWeek = true, selectedWeek = "This Week", onIsSelectingWeekUpdate = {
    }, onSelectedWeekUpdate = { _, _ -> })
}
