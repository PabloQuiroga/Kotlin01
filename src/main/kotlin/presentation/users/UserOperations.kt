package presentation.users

import domain.model.User

interface UserOperations {
    fun getAllUsers(): List<User>
    fun getUserById(userId: Long): User?
    fun getUserByName(name: String): List<User>
    fun getUserByUsername(username: String): User?
    fun getUserByEmail(email: String): User?
    fun getUserByPhone(phone: String): User?
    fun getUserByWebsite(website: String): User?
    fun getUserByZipCode(zipCode: String): List<User>

    fun addNewUser(newUser: User): User
    fun updateExistingUser(user: User): User?
    fun deleteUser(userId: Long): Boolean
}
