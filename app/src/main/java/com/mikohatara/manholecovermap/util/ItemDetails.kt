package com.mikohatara.manholecovermap.util

import com.mikohatara.manholecovermap.data.ManholeCover

data class ItemDetails(
    val id: Int? = null,
    val imagePath: String? = null,
    val country: String? = null,
    val region: String? = null,
    val city: String? = null,
)

fun ManholeCover.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    imagePath = imagePath,
    country = country,
    region = region,
    city = city
)

fun ItemDetails.toManholeCover(): ManholeCover = ManholeCover(
    id = id ?: 0,
    imagePath = imagePath?.takeIf { it.isNotBlank() },
    country = country?.takeIf { it.isNotBlank() },
    region = region?.takeIf { it.isNotBlank() },
    city = city?.takeIf { it.isNotBlank() }
)
