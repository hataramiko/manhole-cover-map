package com.mikohatara.manholecovermap.ui.prefab

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mikohatara.manholecovermap.R
import java.io.File

@Composable
fun pickImage(
    context: Context,
    existingImagePath: String?,
    onPick: (Uri?) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    tempImageUri: Uri? = null
): String? {
    var isRemoveIntent by remember { mutableStateOf(false) }
    val toggleRemoveIntent = { isRemoveIntent = !isRemoveIntent }
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        onPick(it)
    }
    val togglePicker = {
        photoPicker.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    if (isRemoveIntent) {
        onRemove()
        toggleRemoveIntent()
    }

    if (tempImageUri != null) {
        ImagePickerFrame(
            onPick = togglePicker,
            onRemove = toggleRemoveIntent,
            modifier = modifier,
            hasExistingImage = true
        ) {
            EntryScreenImage(context = context, imageUri = tempImageUri)
        }
        return null

    } else if (existingImagePath != null) {
        ImagePickerFrame(
            onPick = togglePicker,
            onRemove = toggleRemoveIntent,
            modifier = modifier,
            hasExistingImage = true
        ) {
            EntryScreenImage(context = context, imagePath = existingImagePath)
        }
        return existingImagePath

    } else {
        ImagePickerFrame(
            onPick = togglePicker,
            onRemove = toggleRemoveIntent,
            modifier = modifier,
            hasExistingImage = false
        ) {
            EntryScreenImage(context = context)
        }
        return null
    }
}

@Composable
fun EntryScreenImage(
    context: Context,
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    imagePath: String? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        modifier = modifier.fillMaxWidth()
    ) {
        if (imageUri != null || imagePath != null) {
            AsyncImage(
                model = ImageRequest
                    .Builder(context)
                    .data(imageUri ?: imagePath)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_photo_alternate_24),
                    contentDescription = null,
                    tint = colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.add_image),
                    color = colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun HomeScreenImage(
    context: Context,
    imagePath: String?,
    modifier: Modifier = Modifier,
    size: Int = 72
) {
    Card(
        modifier = modifier.size(size.dp)
    ) {
        if (imagePath != null) {
            val imageUri = remember { Uri.fromFile(File(imagePath)) }
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(imageUri)
                    .size(256)
                    .crossfade(true)
                    .build()
            )
            Image(painter)
        } else {
            NoImage()
        }
    }
}

@Composable
private fun Image(
    painter: AsyncImagePainter,
    modifier: Modifier = Modifier
) {
    when (painter.state) {
        is AsyncImagePainter.State.Empty, is AsyncImagePainter.State.Loading -> {
            LoadingImage()
        }
        is AsyncImagePainter.State.Success -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
        is AsyncImagePainter.State.Error -> {
            ImageError()
        }
    }
}

@Composable
private fun NoImage(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.surface)
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_hide_image_24),
            contentDescription = null,
            tint = colorScheme.outlineVariant
        )
    }
}

@Composable
private fun LoadingImage(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.surfaceContainerLow)
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_hourglass_24),
            contentDescription = null,
            tint = colorScheme.outlineVariant
        )
    }
}

@Composable
private fun ImageError(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.surfaceContainerLowest)
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_broken_image_24),
            contentDescription = null,
            tint = colorScheme.outlineVariant
        )
    }
}

@Composable
private fun ImagePickerFrame(
    onPick: () -> Unit,
    onRemove: () -> Unit,
    hasExistingImage: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.padding(top = 8.dp)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(Color.Transparent),
            border = BorderStroke(width = 1.dp, color = colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                content()
            }
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onPick)
        )
        RemovalButton(onClick = onRemove, hasExistingImage = hasExistingImage)
    }
}

@Composable
private fun RemovalButton(
    onClick: () -> Unit,
    hasExistingImage: Boolean
) {
    if (hasExistingImage) {
        Box(
            contentAlignment = Alignment.TopEnd,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 4.dp, y = (-8).dp)
        ) {
            FilledTonalIconButton(
                onClick = onClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_close_24),
                    contentDescription = null
                )
            }
        }
    }
}
