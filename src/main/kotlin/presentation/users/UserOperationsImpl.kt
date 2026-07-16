package presentation.users

import domain.model.User
import domain.usecase.UserUseCases

class UserOperationsImpl(private val userUseCases: UserUseCases) : UserOperations {

    override fun getAllUsers(): List<User> {
        return userUseCases.getAllUsers()
    }

    override fun getUserById(userId: Long): User? {
        return userUseCases.getUserById(userId)
    }

    override fun getUserByName(name: String): List<User> {
        val users = getAllUsers()
        return users.filter { it.name == name }
    }

    override fun getUserByUsername(username: String): User? {
        val users = getAllUsers()
        return users.find { it.username == username }
    }

    override fun getUserByEmail(email: String): User? {
        val users = getAllUsers()
        return users.find { it.email == email }
    }

    override fun getUserByPhone(phone: String): User? {
        val users = getAllUsers()
        return users.find { it.phone == phone }
    }

    override fun getUserByWebsite(website: String): User? {
        val users = getAllUsers()
        return users.find { it.website == website }
    }

    override fun getUserByZipCode(zipCode: String): List<User> {
        val users = getAllUsers()
        return users.filter { it.address.zipcode == zipCode }
    }

    override fun addNewUser(newUser: User): User {
        return userUseCases.createUser(newUser)
    }

    override fun updateExistingUser(user: User): User? {
        return userUseCases.updateUser(user)
    }

    override fun deleteUser(userId: Long): Boolean {
        return userUseCases.deleteUser(userId)
    }
}
