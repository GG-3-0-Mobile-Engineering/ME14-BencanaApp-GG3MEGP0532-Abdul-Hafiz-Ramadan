package com.ahr.gigihfinalproject.data

import android.content.Context
import androidx.room.withTransaction
import com.ahr.gigihfinalproject.R
import com.ahr.gigihfinalproject.data.local.GigihFinalProjectDatabase
import com.ahr.gigihfinalproject.data.mapper.toDomains
import com.ahr.gigihfinalproject.data.mapper.toEntities
import com.ahr.gigihfinalproject.data.network.service.PetaBencanaService
import com.ahr.gigihfinalproject.domain.model.DisasterFilterTimePeriod
import com.ahr.gigihfinalproject.domain.model.DisasterGeometry
import com.ahr.gigihfinalproject.domain.model.DisasterType
import com.ahr.gigihfinalproject.domain.model.Province
import com.ahr.gigihfinalproject.domain.model.Resource
import com.ahr.gigihfinalproject.domain.model.TmaMonitoringGeometries
import com.ahr.gigihfinalproject.domain.repository.DisasterRepository
import com.ahr.gigihfinalproject.util.getCurrentTimeSeconds
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisasterRepositoryImpl @Inject constructor(
    private val petaBencanaService: PetaBencanaService,
    private val gigihFinalProjectDatabase: GigihFinalProjectDatabase,
    @ApplicationContext private val context: Context,
) : DisasterRepository {

    private val disasterGeometryDao get() = gigihFinalProjectDatabase.disasterGeometryDao()

    override fun getDisasterReportWithFilter(
        timePeriod: DisasterFilterTimePeriod?,
        province: Province?,
        disasterType: DisasterType?,
    ): Flow<Resource<List<DisasterGeometry>>> = flow {

        emit(Resource.Loading)

        val defaultTimePeriodSecond = context.resources.getInteger(R.integer.default_disaster_timeperiod_second).toLong()
        val timePeriodSecond = timePeriod?.timeSecond ?: defaultTimePeriodSecond

        val disasterReportResult = when {
            province != null && disasterType != null -> petaBencanaService.getDisasterReportFilterByDisasterAndLocation(
                admin = province.code,
                disaster = disasterType.code,
                timePeriod = timePeriodSecond
            )

            province != null -> petaBencanaService.getDisasterReportFilterByLocation(
                admin = province.code,
                timePeriod = timePeriodSecond
            )

            disasterType != null -> petaBencanaService.getDisasterReportFilterByDisaster(
                disaster = disasterType.code,
                timePeriod = timePeriodSecond
            )

            else -> petaBencanaService.getLatestDisasterInformation(timePeriod = timePeriodSecond)
        }

        // Only save to database when user get disaster with no filter
        if (province == null && disasterType == null) {
            gigihFinalProjectDatabase.withTransaction {
                val disasterGeometryEntities = disasterReportResult.disasterResult?.disasterObjects?.disasterOutput?.geometries?.toEntities()
                disasterGeometryDao.clearDisasterGeometriesEntities()
                disasterGeometryDao.upsertDisasterGeometryEntities(disasterGeometryEntities ?: emptyList())
            }
            val disasterGeometryDomains = disasterGeometryDao.getDisasterGeometriesEntities().toDomains()
            emit(Resource.Success(disasterGeometryDomains))
        } else {
            val disasterGeometries = disasterReportResult.disasterResult?.disasterObjects?.disasterOutput?.geometries?.toDomains()
            emit(Resource.Success(disasterGeometries ?: emptyList()))
        }

    }.catch {
        val disasterGeometries = if (province == null && disasterType == null) {
            disasterGeometryDao.getDisasterGeometriesEntities().toDomains()
        } else emptyList()
        emit(Resource.Error(it, disasterGeometries))
    }

    override fun getProvinces(query: String): Flow<List<Province>> = flow {
        val provinceNames = context.resources.getStringArray(R.array.province_names)
        val provinceCodes = context.resources.getStringArray(R.array.province_codes)
        val provinces = provinceNames.mapIndexed { index, _ ->
            Province(
                name = provinceNames[index],
                code = provinceCodes[index]
            )
        }
        if (query.isNotEmpty()) {
            val filteredProvinces = provinces.filter { it.name.contains(query, true) || it.code.contains(query, true) }
            emit(filteredProvinces)
        } else {
            emit(provinces)
        }
    }

    override fun getDisasterFilter(): Flow<List<DisasterType>> = flow {
        val disasterNames = context.resources.getStringArray(R.array.disaster_names)
        val disasterCodes = context.resources.getStringArray(R.array.disaster_codes)
        val provinces = disasterNames.mapIndexed { index, _ ->
            DisasterType(
                name = disasterNames[index],
                code = disasterCodes[index]
            )
        }
        emit(provinces)
    }

    override fun getDisasterTimePeriodFilter(selectedDisasterTimePeriod: DisasterFilterTimePeriod?): Flow<List<DisasterFilterTimePeriod>> =
        flow {
            val currentTimeSeconds = getCurrentTimeSeconds()
            val disasterTimePeriodsNames = context.resources.getStringArray(R.array.disaster_time_period_names)
            val disasterTimePeriodsSeconds = context.resources.getIntArray(R.array.disaster_time_period_seconds)

            val disasterFilterTimePeriod =
                disasterTimePeriodsNames.mapIndexed { index, periodName ->
                    val isSelected = selectedDisasterTimePeriod?.name.equals(periodName, false)
                    DisasterFilterTimePeriod(
                        timeSecond = (if (index == 0) currentTimeSeconds else disasterTimePeriodsSeconds[index]).toLong(),
                        name = periodName,
                        selected = isSelected
                    )
                }
            emit(disasterFilterTimePeriod)
        }

    override fun getTmaMonitoring(): Flow<Resource<List<TmaMonitoringGeometries>>> = flow {
        emit(Resource.Loading)
        val tmaMonitoringProperties = petaBencanaService.getTmaMonitoring().result?.objects?.output?.geometries
            ?.map { it.toDomains() } ?: emptyList()
        emit(Resource.Success(tmaMonitoringProperties))
    }.catch {
        emit(Resource.Error(it, emptyList()))
    }
}