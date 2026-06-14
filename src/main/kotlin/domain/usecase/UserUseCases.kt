package domain.usecase

import domain.model.User
import domain.repository.UserRepository

class UserUseCases(private val userRepository: UserRepository) {

    fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }

    fun getUserById(id: Long): User? {
        return userRepository.getUserById(id)
    }

    fun createUser(user: User): User {
        // Aquí podrías añadir lógica de negocio adicional antes de guardar,
        // como validaciones, etc.
        return userRepository.addUser(user)
    }

    fun updateUser(user: User): User? {
        // Aquí podrías añadir lógica de negocio adicional antes de actualizar
        return userRepository.updateUser(user)
    }

    fun deleteUser(id: Long): Boolean {
        // Aquí podrías añadir lógica de negocio adicional antes de eliminar
        return userRepository.deleteUser(id)
    }
}
