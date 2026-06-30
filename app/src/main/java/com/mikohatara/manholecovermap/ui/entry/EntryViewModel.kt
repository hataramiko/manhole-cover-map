package com.mikohatara.manholecovermap.ui.entry

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikohatara.manholecovermap.data.ManholeCover
import com.mikohatara.manholecovermap.data.ManholeCoverRepository
import com.mikohatara.manholecovermap.ui.navigation.ManholeCoverMapDestinationArgs.ITEM_ID
import com.mikohatara.manholecovermap.util.ItemDetails
import com.mikohatara.manholecovermap.util.toItemDetails
import com.mikohatara.manholecovermap.util.toManholeCover
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Immutable
data class EntryUiState(
    val item: ManholeCover? = null,
    val itemDetails: ItemDetails = ItemDetails(),
    val temporaryImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isEntryValid: Boolean = false,
    val isNew: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)

@HiltViewModel
class EntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val manholeCoverRepository: ManholeCoverRepository
) : ViewModel() {

    private val itemId: Int? = savedStateHandle.get<Int>(ITEM_ID)

    private val _uiState = MutableStateFlow(EntryUiState())
    val uiState: StateFlow<EntryUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                if (itemId != null) {
                    withContext(Dispatchers.IO) {
                        loadItem(itemId)
                    }
                } else {
                    _uiState.update {
                        it.copy(isEntryValid = false, isNew = true)
                    }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateUiState(newItemDetails: ItemDetails) {
        _uiState.update {
            val initialDetails = if (it.isNew) {
                ItemDetails()
            } else {
                val item = it.item
                item?.toItemDetails() ?: ItemDetails()
            }
            val hasUnsavedChanges = newItemDetails != initialDetails ||
                    it.temporaryImageUri != null || it.hasUnsavedChanges
            val isEntryValid = !(newItemDetails.country.isNullOrBlank())

            it.copy(
                itemDetails = newItemDetails,
                isEntryValid = isEntryValid,
                hasUnsavedChanges = hasUnsavedChanges
            )
        }
    }

    fun saveEntry(context: Context) = viewModelScope.launch {
        if (uiState.value.temporaryImageUri != null) saveImageToInternalStorage(context)
        if (uiState.value.itemDetails.imagePath == null && !uiState.value.isNew) clearImagePath()
        if (uiState.value.isNew) addNewItem() else updateItem()
        _uiState.update { it.copy(hasUnsavedChanges = false) }
    }

    fun deleteEntry() = viewModelScope.launch {
        deleteItem()
    }

    fun handlePickedImage(uri: Uri?) {
        _uiState.update { it.copy(temporaryImageUri = uri, hasUnsavedChanges = true) }
    }

    fun clearImagePath() {
        _uiState.update {
            val newItemDetails = it.itemDetails.copy(imagePath = null)
            it.copy(
                itemDetails = newItemDetails,
                temporaryImageUri = null,
                hasUnsavedChanges = true
            )
        }
    }

    fun deleteUnusedImages(context: Context) = viewModelScope.launch {
        val directory = context.filesDir
        val files = directory.listFiles() ?: return@launch
        val imagesInUse = getImagesInUse()
        files.forEach {
            if (!imagesInUse.contains(it.absolutePath)) it.delete()
        }
    }

    private fun saveImageToInternalStorage(context: Context) { //TODO
        /*uiState.value.temporaryImageUri?.let { uri ->
            val newImagePath = filePathFromUri(uri, context)
            val newItemDetails = uiState.value.itemDetails.copy(imagePath = newImagePath)
            updateUiState(newItemDetails)
        }*/
    }

    private fun addNewItem() = viewModelScope.launch {
        manholeCoverRepository.addManholeCover(uiState.value.itemDetails.toManholeCover())
    }

    private fun updateItem() = viewModelScope.launch {
        manholeCoverRepository.updateManholeCover(uiState.value.itemDetails.toManholeCover())
    }

    private suspend fun loadItem(itemId: Int) {
        manholeCoverRepository.getManholeCoverStream(itemId).firstOrNull()?.let { item ->
            _uiState.update {
                it.copy(
                    item = item,
                    itemDetails = item.toItemDetails(),
                    isEntryValid = true,
                    isNew = false
                )
            }
        }
    }

    private suspend fun deleteItem() {
        val item = uiState.value.item ?: return
        manholeCoverRepository.deleteManholeCover(item)
    }

    private suspend fun getImagesInUse(): List<String> {
        val itemsAsync = viewModelScope.async {
            manholeCoverRepository.getAllManholeCoversStream().firstOrNull() ?: emptyList()
        }
        val items = itemsAsync.await()

        return items.mapNotNull { it.imagePath }
    }
}
