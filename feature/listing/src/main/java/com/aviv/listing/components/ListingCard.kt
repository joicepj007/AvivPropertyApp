package com.aviv.listing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aviv.common.extensions.formatPrice
import com.aviv.designsystem.R // ← Add this import
import com.aviv.model.Listing
import com.aviv.model.OfferType

@Composable
fun ListingCard(
    listing: Listing,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Image Section with Price, Location & Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                // Property Image with Placeholder
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(listing.imageUrl.ifEmpty { null })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Property image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_no_image),
                    error = painterResource(R.drawable.ic_no_image),
                    fallback = painterResource(R.drawable.ic_no_image)
                )

                // Dark gradient overlay (only if there's an image)
                if (listing.imageUrl.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.75f)
                                    ),
                                    startY = 80f
                                )
                            )
                    )
                }

                // Rent/Sale Badge - Top Right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    OfferTypeBadge(offerType = listing.offerType)
                }

                // Price and Location - Bottom Left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    // Price
                    Text(
                        text = listing.price.formatPrice(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (listing.imageUrl.isNotEmpty()) Color.White
                        else MaterialTheme.colorScheme.onSurface,
                        fontSize = 30.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Location
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = if (listing.imageUrl.isNotEmpty()) Color.White
                            else MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = listing.city,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (listing.imageUrl.isNotEmpty()) Color.White
                            else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Property Type/Name
                Text(
                    text = listing.propertyType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Features Row - Original Design
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    PropertyFeature(
                        icon = Icons.Default.Bed,
                        value = "${listing.bedrooms} beds"
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    PropertyFeature(
                        icon = Icons.Default.Home,
                        value = "${listing.rooms} rooms"
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    PropertyFeature(
                        icon = Icons.Default.SquareFoot,
                        value = "${listing.area.toInt()} m²"
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyFeature(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

//Badge Design for Rent/Sale
@Composable
private fun OfferTypeBadge(
    offerType: OfferType,
    modifier: Modifier = Modifier
) {
    val (text, backgroundColor, textColor) = when (offerType) {
        OfferType.SALE -> Triple(
            "For Sale",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        OfferType.RENT -> Triple(
            "For Rent",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        OfferType.UNKNOWN -> Triple(
            "N/A",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}