package domain.usecase

import domain.model.Address
import domain.model.Geo
import domain.model.User
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
        val geo = Geo(id = 1, lat = "0.0", lng = "0.0")
        val address = Address(id = 1, street = "Street", suite = "Suite", city = "City", zipcode = "12345", geo = geo)
        val expectedUsers = listOf(
            User(
                id = 1,
                name = "Alice",
                username = "alice",
                email = "a@a.com",
                phone = "1",
                website = "a.com",
                address = address
            ),
            User(
                id = 2,
                name = "Bob",
                username = "bob",
                email = "b@b.com",
                phone = "2",
                website = "b.com",
                address = address
            )
        )
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
        val geo = Geo(id = 1, lat = "0.0", lng = "0.0")
        val address = Address(id = 1, street = "Street", suite = "Suite", city = "City", zipcode = "12345", geo = geo)
        val expectedUser = User(
            id = userId,
            name = "Alice",
            username = "alice",
            email = "a@a.com",
            phone = "1",
            website = "a.com",
            address = address
        )
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
        val geo = Geo(lat = "0.0", lng = "0.0")
        val address = Address(street = "Street", suite = "Suite", city = "City", zipcode = "12345", geo = geo)
        val userToCreate = User(
            name = "Charlie",
            username = "charlie",
            email = "c@c.com",
            phone = "3",
            website = "c.com",
            address = address
        )
        val expectedUser = userToCreate.copy(id = 3) // Simulate ID being assigned by repository
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
        val geo = Geo(id = 1, lat = "0.0", lng = "0.0")
        val address = Address(id = 1, street = "Street", suite = "Suite", city = "City", zipcode = "12345", geo = geo)
        val userToUpdate = User(
            id = 1,
            name = "Alice Updated",
            username = "alice",
            email = "a@a.com",
            phone = "1",
            website = "a.com",
            address = address
        )
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
        val geo = Geo(id = 1, lat = "0.0", lng = "0.0")
        val address = Address(id = 1, street = "Street", suite = "Suite", city = "City", zipcode = "12345", geo = geo)
        val userToUpdate = User(
            id = 99,
            name = "NonExistent",
            username = "none",
            email = "n@n.com",
            phone = "9",
            website = "n.com",
            address = address
        )
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
