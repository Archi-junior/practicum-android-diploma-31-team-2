package ru.practicum.android.diploma.domain.models

fun Area.isCountry(): Boolean = parentId == null

fun Area.isRegion(): Boolean = parentId != null

fun List<Area>.getCountries(): List<Area> = filter { it.isCountry() }

fun List<Area>.getRegionsForCountry(countryId: Int): List<Area> =
    filter { it.parentId == countryId }

fun List<Area>.findCountryByRegion(regionId: Int): Area? {
    val region = find { it.id == regionId } ?: return null
    return find { it.id == region.parentId }
}
