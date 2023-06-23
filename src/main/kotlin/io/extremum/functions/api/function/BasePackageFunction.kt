package io.extremum.functions.api.function

import io.extremum.functions.api.function.model.Context
import io.extremum.functions.api.function.model.SignalParameters
import io.extremum.functions.api.function.model.StorageTriggerParameters

interface BasePackageFunction {

    suspend fun onStorageTrigger(context: Context, parameters : StorageTriggerParameters)

    suspend fun onSignal(context: Context, parameters : SignalParameters)
}