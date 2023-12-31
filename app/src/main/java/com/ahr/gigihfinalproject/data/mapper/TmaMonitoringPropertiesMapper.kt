package com.ahr.gigihfinalproject.data.mapper

import com.ahr.gigihfinalproject.data.network.response.TmaMonitoringGeometriesItem
import com.ahr.gigihfinalproject.data.network.response.TmaMonitoringObservationItemResponse
import com.ahr.gigihfinalproject.data.network.response.TmaMonitoringPropertiesItemResponse
import com.ahr.gigihfinalproject.domain.model.TmaMonitoringGeometries
import com.ahr.gigihfinalproject.domain.model.TmaMonitoringObservationItem
import com.ahr.gigihfinalproject.domain.model.TmaMonitoringProperties
import com.ahr.gigihfinalproject.util.emptyString

fun TmaMonitoringGeometriesItem.toDomains(): TmaMonitoringGeometries =
    TmaMonitoringGeometries(
        type = type ?: emptyString(),
        properties = properties?.toDomains() ?: TmaMonitoringProperties()
    )

fun TmaMonitoringPropertiesItemResponse.toDomains(): TmaMonitoringProperties =
    TmaMonitoringProperties(
        gaugenameid = gaugenameid ?: emptyString(),
        gaugeid = gaugeid ?: emptyString(),
        observations = observations?.map { it.toDomains() } ?: emptyList()
    )

fun TmaMonitoringObservationItemResponse.toDomains(): TmaMonitoringObservationItem =
    TmaMonitoringObservationItem(
        f1 = f1 ?: emptyString(),
        f2 = f2 ?: 0,
        f3 = f3 ?: 0,
        f4 = f4 ?: emptyString(),
    )