package domain.repository

import domain.model.User

interface UserRepository {
    fun getAllUsers(): List<User>
    fun getUserById(id: Long): User?
    fun addUser(user: User): User
    fun updateUser(user: User): User?
    fun deleteUser(id: Long): Boolean
}
