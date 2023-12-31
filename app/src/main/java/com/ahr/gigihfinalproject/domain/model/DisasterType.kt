package com.ahr.gigihfinalproject.domain.model

import com.ahr.gigihfinalproject.util.emptyString

data class DisasterType(
    val name: String = emptyString(),
    val code: String = emptyString(),
)