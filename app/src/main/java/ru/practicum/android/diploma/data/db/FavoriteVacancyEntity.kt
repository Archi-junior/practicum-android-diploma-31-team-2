package ru.practicum.android.diploma.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_vacancies")
data class FavoriteVacancyEntity(

    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,

    val salaryId: String?,
    val salaryFrom: Int?,
    val salaryTo: Int?,
    val salaryCurrency: String?,

    val addressCity: String?,
    val addressStreet: String?,
    val addressBuilding: String?,
    val addressFullAddress: String?,

    val experienceId: String?,
    val experienceName: String?,

    val scheduleId: String?,
    val scheduleName: String?,

    val employmentId: String?,
    val employmentName: String?,

    val contactsId: String?,
    val contactsName: String?,
    val contactsEmail: String?,
    val contactsPhones: String?,

    val employerId: String,
    val employerName: String,
    val employerLogo: String,

    val areaId: Int,
    val areaName: String,
    val areaParentId: Int?,
    val areas: String,

    val skills: String,

    val url: String,

    val industryId: Int,
    val industryName: String,
)
