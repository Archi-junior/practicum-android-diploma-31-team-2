package ru.practicum.android.diploma.data.mapper

import android.os.Bundle
import com.google.gson.Gson
import ru.practicum.android.diploma.data.db.FavoriteVacancyEntity
import ru.practicum.android.diploma.data.dto.EmployerDto
import ru.practicum.android.diploma.data.dto.VacancyDto
import ru.practicum.android.diploma.domain.models.Address
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Contacts
import ru.practicum.android.diploma.domain.models.Employer
import ru.practicum.android.diploma.domain.models.Employment
import ru.practicum.android.diploma.domain.models.Experience
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.Phone
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.Schedule
import ru.practicum.android.diploma.domain.models.Vacancy

fun VacancyDto.toDomain(): Vacancy =
    Vacancy(
        id = id,
        name = cleanJobTitle(name, employer),
        description = description,
        salary = salary?.toDomain(),
        address = address?.toDomain(),
        experience = experience?.toDomain(),
        schedule = schedule?.toDomain(),
        employment = employment?.toDomain(),
        contacts = contacts?.toDomain(),
        employer = employer.toDomain(),
        area = area.toDomain(),
        skills = skills.map { it },
        url = url,
        industry = industry.toDomain(),
    )

private fun cleanJobTitle(rawName: String, employer: EmployerDto?): String {
    var result = rawName
    val employerName = employer?.name?.trim() ?: ""
    if (employerName.isNotEmpty()) {
        val suffix = " в $employerName"
        if (result.endsWith(suffix, ignoreCase = true)) {
            result = result.substringBeforeLast(suffix).trim()
        }
    }
    if (result.endsWith(" в ")) {
        result = result.substring(0, result.length - 3) + ", "
    }
    return result.trim()
}

fun Vacancy.toEntity(gson: Gson): FavoriteVacancyEntity =
    FavoriteVacancyEntity(
        id = id,
        name = name,
        description = description,

        salaryId = salary?.id,
        salaryFrom = salary?.from,
        salaryTo = salary?.to,
        salaryCurrency = salary?.currency,

        addressCity = address?.city,
        addressStreet = address?.street,
        addressBuilding = address?.building,
        addressFullAddress = address?.fullAddress,

        experienceId = experience?.id,
        experienceName = experience?.name,

        scheduleId = schedule?.id,
        scheduleName = schedule?.name,

        employmentId = employment?.id,
        employmentName = employment?.name,

        contactsId = contacts?.id,
        contactsName = contacts?.name,
        contactsEmail = contacts?.email,
        contactsPhones = contacts?.let {
            gson.toJson(it.phones)
        },

        employerId = employer.id,
        employerName = employer.name,
        employerLogo = employer.logo,

        areaId = area.id,
        areaName = area.name,
        areaParentId = area.parentId,
        areas = gson.toJson(area.areas),

        skills = gson.toJson(skills),

        url = url,

        industryId = industry.id,
        industryName = industry.name,
    )

fun FavoriteVacancyEntity.toDomain(gson: Gson): Vacancy =
    Vacancy(
        id = id,
        name = name,
        description = description,
        salary = salaryId?.let {
            Salary(
                id = it,
                from = salaryFrom,
                to = salaryTo,
                currency = salaryCurrency,
            )
        },
        address = addressCity?.let {
            Address(
                city = it,
                street = addressStreet.toString(),
                building = addressBuilding.toString(),
                fullAddress = addressFullAddress.toString(),
            )
        },
        experience = experienceId?.let {
            Experience(id = it, name= experienceName.toString())
        },
        schedule = scheduleId?.let {
            Schedule(id = it, name = scheduleName.toString())
        },
        employment = employmentId?.let {
            Employment(id = it, name = employmentName.toString())
        },
        contacts = contactsId?.let {
            Contacts(
                id = it,
                name = contactsName.toString(),
                email = contactsEmail.toString(),
                phones = contactsPhones?.let { child ->
                    gson.fromJson(child, Array<Phone>::class.java).toList()
                } ?: emptyList()
            )
        },
        employer = Employer(
            id = employerId,
            name = employerName,
            logo = employerLogo,
        ),
        area = Area(
            id = areaId,
            name = areaName,
            parentId = areaParentId,
            areas = gson.fromJson(areas, Array<Area>::class.java).toList(),
        ),
        skills = gson.fromJson(skills, Array<String>::class.java).toList(),
        url = url,
        industry = Industry(id = industryId, name = industryName),
    )

private fun cleanJobTitle(rawName: String, employer: EmployerDto?): String {
    var result = rawName
    val employerName = employer?.name?.trim() ?: ""
    if (employerName.isNotEmpty()) {
        val suffix = " в $employerName"
        if (result.endsWith(suffix, ignoreCase = true)) {
            result = result.substringBeforeLast(suffix).trim()
        }
    }
    if (result.endsWith(" в ")) {
        result = result.dropLast(DELETE_NAME_COMPANY) + ", "
    }
    return result.trim()

}
private const val DELETE_NAME_COMPANY = 3

