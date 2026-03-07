package ru.practicum.android.diploma.data.network

fun ApiRequest.VacanciesFilter.toQueryMap(): Map<String, String> =
    buildMap {
        areaId?.let { put("area", it.toString()) }
        industryId?.let { put("industry", it.toString()) }
        text?.let { put("text", it) }
        salaryVal?.let { put("salary", it.toString()) }
        put("page", page.toString())
        onlyWithSalary?.let { put("only_with_salary", it.toString()) }
    }
