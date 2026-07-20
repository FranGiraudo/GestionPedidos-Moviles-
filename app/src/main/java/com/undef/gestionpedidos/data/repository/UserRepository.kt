package com.undef.gestionpedidos.data.repository

import com.undef.gestionpedidos.data.local.dao.UserDao
import com.undef.gestionpedidos.data.local.entity.UserEntity
import com.undef.gestionpedidos.domain.model.Usuario
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun register(email: String, password: String, fullName: String, phone: String? = null): Boolean {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return false
        }
        val entity = UserEntity(
            email = email,
            passwordHash = hashPassword(password),
            fullName = fullName,
            phone = phone
        )
        return try {
            userDao.insertUser(entity)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): Usuario? {
        val entity = userDao.getUserByEmail(email) ?: return null
        val hashedInput = hashPassword(password)
        if (entity.passwordHash != hashedInput || !entity.isActive) {
            return null
        }
        return Usuario(entity.id, entity.email, entity.fullName, entity.phone, entity.role, entity.isActive)
    }

    suspend fun getUserById(id: Int): Usuario? {
        val entity = userDao.getUserById(id) ?: return null
        return Usuario(entity.id, entity.email, entity.fullName, entity.phone, entity.role, entity.isActive)
    }
}
