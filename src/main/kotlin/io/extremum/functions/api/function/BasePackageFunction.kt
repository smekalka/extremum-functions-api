package io.extremum.functions.api.function

import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters

interface BasePackageFunction {

    suspend fun onStorageTrigger(parameters : StorageTriggerParameters)

    suspend fun onSignal(parameters : SignalParameters)
}