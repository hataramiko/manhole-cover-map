package com.mikohatara.manholecovermap.ui.entry

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikohatara.manholecovermap.R
import com.mikohatara.manholecovermap.util.ItemDetails

@Composable
fun EntryScreen(
    viewModel: EntryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.deleteUnusedImages(context) }

    EntryScreen(
        context = context,
        itemDetails = uiState.itemDetails,
        temporaryImageUri = uiState.temporaryImageUri,
        isLoading = uiState.isLoading,
        isNew = uiState.isNew,
        isEntryValid = uiState.isEntryValid,
        hasUnsavedChanges = uiState.hasUnsavedChanges,
        onBack = onBack,
        onSave = viewModel::saveEntry,
        onDelete = viewModel::deleteEntry,
        onValueChange = viewModel::updateUiState,
        onImagePicked = viewModel::handlePickedImage,
        onImageRemoved = viewModel::clearImagePath
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryScreen(
    context: Context,
    itemDetails: ItemDetails,
    temporaryImageUri: Uri?,
    isLoading: Boolean,
    isNew: Boolean,
    isEntryValid: Boolean,
    hasUnsavedChanges: Boolean,
    onBack: () -> Unit,
    onSave: (Context) -> Unit,
    onDelete: () -> Unit,
    onValueChange: (ItemDetails) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onImageRemoved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    /*var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val onDismissDiscardDialog = { showDiscardDialog = false }*/

    val topBarTitle = if (!isNew) {
        stringResource(R.string.edit)
    } else {
        stringResource(R.string.add_new)
    }

    val onBackBehavior = { onBack() } //{ if (hasUnsavedChanges) showDiscardDialog = true else onBack() }
    BackHandler { onBackBehavior() }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EntryTopAppBar(
                title = topBarTitle,
                onBack = onBackBehavior,
                onSave = {
                    onSave(context)
                    onBack()
                },
                onDelete = {
                    onDelete()
                    onBack()
                },
                isSaveEnabled = isEntryValid,
                isDeleteEnabled = !isNew,
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->
            if (isLoading) {
                Loading()
            } else {
                EntryScreenContent(
                    itemDetails = itemDetails,
                    temporaryImageUri = temporaryImageUri,
                    isEntryValid = isEntryValid,
                    onValueChange = onValueChange,
                    onImagePicked = onImagePicked,
                    onImageRemoved = onImageRemoved,
                    onSave = {
                        onSave(context)
                        onBack()
                    },
                    modifier = modifier.padding(innerPadding)
                )
            }
        }
    )
}

@Composable
private fun EntryScreenContent(
    itemDetails: ItemDetails,
    temporaryImageUri: Uri?,
    isEntryValid: Boolean,
    onValueChange: (ItemDetails) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onImageRemoved: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        Image(
            existingImagePath = itemDetails.imagePath,
            tempImageUri = temporaryImageUri,
            onPick = onImagePicked,
            onRemove = onImageRemoved
        )
        EntryField(
            label = stringResource(R.string.country),
            value = itemDetails.country ?: "",
            onValueChange = { onValueChange(itemDetails.copy(country = it)) },
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
        )
        EntryField(
            label = stringResource(R.string.region),
            value = itemDetails.region ?: "",
            onValueChange = { onValueChange(itemDetails.copy(region = it)) },
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
        )
        EntryField(
            label = stringResource(R.string.city),
            value = itemDetails.city ?: "",
            onValueChange = { onValueChange(itemDetails.copy(city = it)) },
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
        )
        Button(
            onClick = onSave,
            enabled = isEntryValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .height(40.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_save_24),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.save),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntryTopAppBar(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    isSaveEnabled: Boolean = true,
    isDeleteEnabled: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.outline_arrow_back_24),
                    contentDescription = null
                )
            }
        },
        actions = {
            FilledIconButton(
                onClick = onSave,
                enabled = isSaveEnabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_save_24),
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onDelete,
                enabled = isDeleteEnabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_delete_24),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun Image(
    existingImagePath: String?,
    tempImageUri: Uri?,
    onPick: (Uri?) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    /*pickItemImage( TODO
        existingImagePath = existingImagePath,
        tempImageUri = tempImageUri,
        onPick = onPick,
        onRemove = onRemove,
        modifier = modifier
    )*/
}

@Composable
private fun EntryField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun Loading() {
    Text(
        text = "${stringResource(R.string.loading)}...",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 128.dp)
    )
}
