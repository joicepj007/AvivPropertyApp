package com.aviv.model

enum class OfferType {
    SALE,
    RENT,
    UNKNOWN;

    companion object {
        fun fromInt(value: Int?): OfferType = when (value) {
            1 -> RENT
            2 -> SALE
            else -> UNKNOWN
        }
    }
}