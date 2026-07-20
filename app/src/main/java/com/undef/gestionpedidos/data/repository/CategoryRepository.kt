package com.undef.gestionpedidos.data.repository

import android.util.Log
import com.undef.gestionpedidos.data.local.dao.CategoryDao
import com.undef.gestionpedidos.data.local.entity.CategoryEntity
import com.undef.gestionpedidos.data.remote.ApiService
import com.undef.gestionpedidos.di.ServiceLocator
import com.undef.gestionpedidos.domain.model.Categoria

class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val apiService: ApiService
) {
    suspend fun getAllCategories(): List<Categoria> {
        var localData = categoryDao.getAllCategories()

        if (localData.isEmpty()) {
            try {
                val remoteData = apiService.getCategories(
                    apiKey = ServiceLocator.SUPABASE_ANON_KEY,
                    authorization = "Bearer ${ServiceLocator.SUPABASE_ANON_KEY}"
                )
                remoteData.forEach { dto ->
                    categoryDao.insertCategory(CategoryEntity(nombre = dto.nombre))
                }
                localData = categoryDao.getAllCategories()
            } catch (e: Exception) {
                Log.e("CategoryRepository", "Error fetching categories from remote", e)
                // Si falla la red, se devuelve la lista local (vacía en este caso)
            }
        }

        return localData.map { entity ->
            Categoria(entity.id, entity.nombre)
        }
    }
}
