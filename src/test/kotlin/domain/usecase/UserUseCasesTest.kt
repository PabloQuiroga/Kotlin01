package domain.usecase

import domain.model.UserFactory
import domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserUseCasesTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userUseCases: UserUseCases

    @BeforeEach
    fun setUp() {
        userRepository = mockk() // Crea un mock de UserRepository
        userUseCases = UserUseCases(userRepository) // Inyecta el mock en UserUseCases
    }

    @Test
    fun `getAllUsers should return a list of users from the repository`() {
        // Given
        val expectedUsers = UserFactory.createList(2)
        every { userRepository.getAllUsers() } returns expectedUsers

        // When
        val actualUsers = userUseCases.getAllUsers()

        // Then
        assertEquals(expectedUsers, actualUsers)
        verify(exactly = 1) { userRepository.getAllUsers() } // Verifica que el método fue llamado
    }

    @Test
    fun `getUserById should return a user from the repository when found`() {
        // Given
        val userId = 1L
        val expectedUser = UserFactory.create(id = userId)
        every { userRepository.getUserById(userId) } returns expectedUser

        // When
        val actualUser = userUseCases.getUserById(userId)

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 1) { userRepository.getUserById(userId) }
    }

    @Test
    fun `getUserById should return null from the repository when not found`() {
        // Given
        val userId = 99L
        every { userRepository.getUserById(userId) } returns null

        // When
        val actualUser = userUseCases.getUserById(userId)

        // Then
        assertEquals(null, actualUser)
        verify(exactly = 1) { userRepository.getUserById(userId) }
    }

    @Test
    fun `createUser should add a user to the repository and return the added user`() {
        // Given
        val userToCreate = UserFactory.create(
            name = "Charlie",
            username = "charlie",
            email = "c@c.com",
            phone = "3",
            website = "c.com"
        )
        val expectedUser = UserFactory.create(id = 3)
        every { userRepository.addUser(userToCreate) } returns expectedUser

        // When
        val actualUser = userUseCases.createUser(userToCreate)

        // Then
        assertEquals(expectedUser, actualUser)
        verify(exactly = 1) { userRepository.addUser(userToCreate) }
    }

    @Test
    fun `updateUser should update a user in the repository and return the updated user`() {
        // Given
        val userToUpdate = UserFactory.create()
        every { userRepository.updateUser(userToUpdate) } returns userToUpdate

        // When
        val actualUser = userUseCases.updateUser(userToUpdate)

        // Then
        assertEquals(userToUpdate, actualUser)
        verify(exactly = 1) { userRepository.updateUser(userToUpdate) }
    }

    @Test
    fun `updateUser should return null if the user does not exist in the repository`() {
        // Given
        val userToUpdate = UserFactory.create()
        every { userRepository.updateUser(userToUpdate) } returns null

        // When
        val actualUser = userUseCases.updateUser(userToUpdate)

        // Then
        assertEquals(null, actualUser)
        verify(exactly = 1) { userRepository.updateUser(userToUpdate) }
    }

    @Test
    fun `deleteUser should delete a user from the repository and return true`() {
        // Given
        val userId = 1L
        every { userRepository.deleteUser(userId) } returns true

        // When
        val result = userUseCases.deleteUser(userId)

        // Then
        assertEquals(true, result)
        verify(exactly = 1) { userRepository.deleteUser(userId) }
    }

    @Test
    fun `deleteUser should return false if the user does not exist in the repository`() {
        // Given
        val userId = 99L
        every { userRepository.deleteUser(userId) } returns false

        // When
        val result = userUseCases.deleteUser(userId)

        // Then
        assertEquals(false, result)
        verify(exactly = 1) { userRepository.deleteUser(userId) }
    }

}
