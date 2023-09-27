package com.roger.myapplication.ui.app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.google.accompanist.adaptive.FoldAwareConfiguration
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.TwoPaneStrategy
import com.roger.myapplication.helper.StateBirds
import com.roger.myapplication.helper.getBirdCollection
import com.roger.myapplication.helper.userInteractionNotification


@Composable
fun BirdUIStart( windowSizeClass: WindowSizeClass, displayFeatures: List<DisplayFeature>) {
    val widthSizeClass by rememberUpdatedState(windowSizeClass.widthSizeClass)
    var selectedBirdIndex: Int? by rememberSaveable { mutableStateOf(null) }
    var isDetailOpen by rememberSaveable { mutableStateOf(false) }

    BirdUIChoice(isDetailOpen = isDetailOpen, setIsDetailOpen = {isDetailOpen = it},
        showListAndDetail =
            when(widthSizeClass) {
                WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> false
                WindowWidthSizeClass.Expanded -> true
                else -> true
            },
        detailKey = selectedBirdIndex,
        list = { isDetailVisible ->
            val currentSelectedBirdIndex = selectedBirdIndex
            BirdList(
                selectionState = if (isDetailVisible && currentSelectedBirdIndex != null) {
                    SelectionVisibilityState.ShowSelection(currentSelectedBirdIndex)
                } else {
                    SelectionVisibilityState.NoSelection
                },
                onIndexClick = {index -> selectedBirdIndex = index
                isDetailOpen = true
                },
                modifier = if(isDetailVisible) { Modifier.padding(end = 8.dp)
                } else {
                    Modifier
                }
            )
        },
        detail = {isListVisible ->
            val myList = getBirdCollection(LocalContext.current)
            val pickedBird = selectedBirdIndex?.let(myList::get)
            if (pickedBird != null) {
                BirdProfile(pickedBird)
            }
        },
        twoPaneStrategy = HorizontalTwoPaneStrategy(
            splitFraction = 1f / 3f,
        ),
        displayFeatures = displayFeatures,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

}

@Composable
fun BirdUIChoice(isDetailOpen: Boolean, setIsDetailOpen: (Boolean) -> Unit,
                 showListAndDetail: Boolean, detailKey: Any?,
                 list: @Composable (isDetailVisible: Boolean) -> Unit,
                 detail: @Composable (isListVisible: Boolean) -> Unit,
                 twoPaneStrategy: TwoPaneStrategy, displayFeatures: List<DisplayFeature>,
                 modifier: Modifier = Modifier)
{
    val currentIsDetailOpen by rememberUpdatedState(isDetailOpen)
    val currentShowListAndDetail by rememberUpdatedState(showListAndDetail)
    val currentDetailKey by rememberUpdatedState(detailKey)

    val showList by remember {
        derivedStateOf {
            currentShowListAndDetail || !currentIsDetailOpen
        }
    }
    val showDetail by remember {
        derivedStateOf {
            currentShowListAndDetail || currentIsDetailOpen
        }
    }
    check(showList || showDetail)
    val listSaveableStateHolder = rememberSaveableStateHolder()
    val detailSaveableStateHolder = rememberSaveableStateHolder()

    val start = remember {
        movableContentOf {
            listSaveableStateHolder.SaveableStateProvider(0) {
                Box(
                    modifier = Modifier
                        .userInteractionNotification {
                            // When interacting with the list, consider the detail to no longer be
                            // open in the case of resize.
                            setIsDetailOpen(false)
                        }
                ) {
                    list(showDetail)
                }
            }
        }
    }
    val end = remember {
        movableContentOf {
            detailSaveableStateHolder.SaveableStateProvider(currentDetailKey ?: "null") {
                Box(
                    modifier = Modifier
                        .userInteractionNotification {
                            setIsDetailOpen(true)
                        }
                ) {
                    detail(showList)
                }
            }
            if (!showList) {
                BackHandler {
                    setIsDetailOpen(false)
                }
            }
        }
    }
    Box(modifier = modifier) {
        if (showList && showDetail) {
            TwoPane(
                first = {
                    start()
                },
                second = {
                    end()
                },
                strategy = twoPaneStrategy,
                displayFeatures = displayFeatures,
                foldAwareConfiguration = FoldAwareConfiguration.VerticalFoldsOnly,
                modifier = Modifier.fillMaxSize(),
            )
        } else if (showList) {
            start()
        } else {
            end()
        }
    }

}