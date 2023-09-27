package com.roger.myapplication.ui.app

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.roger.myapplication.helper.StateBirds
import com.roger.myapplication.helper.getBirdCollection
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.roger.myapplication.helper.getDrawableFromName


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BirdList(context: Context = LocalContext.current,
             selectionState: SelectionVisibilityState,
             onIndexClick: (index: Int) -> Unit,
             modifier: Modifier = Modifier) {
    val birdList = remember { getBirdCollection(context)}
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .then(
                when (selectionState) {
                    SelectionVisibilityState.NoSelection -> Modifier
                    is SelectionVisibilityState.ShowSelection -> Modifier.selectableGroup()
                }
            )
    ) {
        itemsIndexed(birdList) {
            index, bird ->
            val interactionSource = remember { MutableInteractionSource() }

            val interactionModifier = when (selectionState) {
                SelectionVisibilityState.NoSelection -> {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = rememberRipple(),
                        onClick = { onIndexClick(index) }
                    )
                }
                is SelectionVisibilityState.ShowSelection -> {
                    Modifier.selectable(
                        selected = index == selectionState.selectedBirdIndex,
                        interactionSource = interactionSource,
                        indication = rememberRipple(),
                        onClick = { onIndexClick(index) }
                    )
                }
            }
            BirdItem(bird, interactionModifier)
        }
    }
}

@Composable
fun BirdItem(bird: StateBirds, modifier: Modifier) {
    Card(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(),
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Row() {
            StateImage(bird)
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = bird.name, style = typography.titleMedium)
                Text(text = bird.nomenclature, style = typography.bodySmall)
                Text(text = bird.state, style = typography.bodySmall)
            }
        }
    }
}

@Composable
private fun StateImage(bird: StateBirds) {
    Image(
        painter = painterResource(id = getDrawableFromName(bird.thumb,LocalContext.current )),
        contentDescription = bird.state,
        modifier = Modifier
            .padding(8.dp)
            .size(64.dp)
    )

}

sealed interface SelectionVisibilityState {
    object NoSelection : SelectionVisibilityState
    data class ShowSelection(
        val selectedBirdIndex: Int
    ) : SelectionVisibilityState
}