package com.undef.gestionpedidos.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.undef.gestionpedidos.di.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Ejecutamos la sincronización de órdenes a Supabase
                val success = ServiceLocator.orderRepository.syncOrdersToCloud()
                if (success) {
                    Result.success()
                } else {
                    Result.retry()
                }
            } catch (e: Exception) {
                // En caso de cualquier error no controlado, pedimos reintento
                Result.retry()
            }
        }
    }
}
