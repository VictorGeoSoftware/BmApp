package com.briel.marnisos.brielapp.data.usecases

import com.briel.marnisos.brielapp.data.local.DeviceIdLocalDataSource
import com.briel.marnisos.brielapp.domain.usecases.GetDeviceIdUseCase

fun GetDeviceIdUseCase.Factory.create(
    deviceIdLocalDataSource: DeviceIdLocalDataSource
): GetDeviceIdUseCase = GetDeviceIdUseCase { deviceIdLocalDataSource.getOrCreate() }
