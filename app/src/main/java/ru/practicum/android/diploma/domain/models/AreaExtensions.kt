package ru.practicum.android.diploma.domain.models

fun Area.isCountry(): Boolean = parentId == null

fun Area.isRegion(): Boolean = parentId != null

fun List<Area>.getCountries(): List<Area> = filter { it.isCountry() }

fun List<Area>.getRegionsForCountry(countryId: Int): List<Area> {
    val country = find { it.id == countryId }
    return country?.areas ?: emptyList()
}

fun List<Area>.findCountryByRegion(regionId: Int): Area? {
    forEach { country ->
        if (country.areas.any { it.id == regionId }) {
            return country
        }
    }
    return null
}
