package data.repository

import data.source.DatabaseManager
import di.allModules
import domain.model.Address
import domain.model.Geo
import domain.model.User
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.sql.Connection
import java.sql.DriverManager

class UserRepositoryImplTest : KoinTest { // Extender de KoinTest

    private lateinit var connection: Connection
    private val userRepository: UserRepositoryImpl by inject() // Inyectar con Koin

    @BeforeEach
    fun setUp() {
        // Iniciar Koin con los módulos de la aplicación
        startKoin {
            modules(allModules)
        }

        // Usar una base de datos SQLite en memoria para cada test
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        DatabaseManager.setTestConnection(connection) // Sobrescribir la función getConnection de DatabaseManager para usar nuestra conexión en memoria
        DatabaseManager.initDatabase() // Inicializar el esquema en la DB en memoria
    }

    @AfterEach
    fun tearDown() {
        connection.close() // Cerrar la conexión después de cada test
        DatabaseManager.clearTestConnection() // Limpiar la conexión de test
        stopKoin() // Detener Koin
    }

    @Test
    fun `addUser should insert a user and return the user with generated ID`() {
        val newUser = createTestMockUser()
        val addedUser = userRepository.addUser(newUser)

        assertNotNull(addedUser.id)
        assertNotNull(addedUser.address.id)
        assertNotNull(addedUser.address.geo.id)
        assertEquals(newUser.name, addedUser.name)
        assertEquals(newUser.username, addedUser.username)

        val retrievedUser = userRepository.getUserById(addedUser.id!!)
        assertEquals(addedUser, retrievedUser)
    }

    @Test
    fun `getAllUsers should return all users in the database`() {
        val geo1 = Geo(lat = "10.0", lng = "20.0")
        val address1 = Address(street = "St1", suite = "S1", city = "C1", zipcode = "Z1", geo = geo1)
        val user1 = User(
            name = "User1",
            username = "u1",
            email = "u1@e.com",
            phone = "p1",
            website = "w1",
            address = address1
        )
        userRepository.addUser(user1)

        val geo2 = Geo(lat = "30.0", lng = "40.0")
        val address2 = Address(street = "St2", suite = "S2", city = "C2", zipcode = "Z2", geo = geo2)
        val user2 = User(
            name = "User2",
            username = "u2",
            email = "u2@e.com",
            phone = "p2",
            website = "w2",
            address = address2
        )
        userRepository.addUser(user2)

        val users = userRepository.getAllUsers()
        assertEquals(2, users.size)
        assertTrue(users.any { it.username == "u1" })
        assertTrue(users.any { it.username == "u2" })
    }

    @Test
    fun `getUserById should return the correct user when found`() {
        val addedUser = userRepository.addUser(createTestMockUser())

        val foundUser = userRepository.getUserById(addedUser.id!!)
        assertEquals(addedUser, foundUser)
    }

    @Test
    fun `getUserById should return null when user not found`() {
        val foundUser = userRepository.getUserById(999L)
        assertNull(foundUser)
    }

    @Test
    fun `updateUser should update an existing user and return the updated user`() {
        val addedUser = userRepository.addUser(createTestMockUser())

        val updatedGeo = addedUser.address.geo.copy(lat = "30.0", lng = "40.0")
        val updatedAddress = addedUser.address.copy(street = "Updated St", geo = updatedGeo)
        val userToUpdate = addedUser.copy(
            name = "Updated Name",
            email = "updated@example.com",
            address = updatedAddress
        )

        val result = userRepository.updateUser(userToUpdate)

        assertNotNull(result)
        assertEquals("Updated Name", result?.name)
        assertEquals("updated@example.com", result?.email)
        assertEquals("Updated St", result?.address?.street)
        assertEquals("30.0", result?.address?.geo?.lat)

        val retrievedUser = userRepository.getUserById(addedUser.id!!)
        assertEquals(result, retrievedUser)
    }

    @Test
    fun `updateUser should return null if user to update does not exist`() {
        // Modificado para incluir un ID para Geo, ya que la lógica de updateUser lo requiere.
        val geo = Geo(id = 999L, lat = "10.0", lng = "20.0")
        val address = Address(id = 999, street = "St", suite = "S", city = "C", zipcode = "Z", geo = geo)
        val nonExistentUser = User(
            id = 999L,
            name = "Non Existent",
            username = "none",
            email = "none@e.com",
            phone = "p",
            website = "w",
            address = address
        )

        val result = userRepository.updateUser(nonExistentUser)
        assertNull(result)
    }

    @Test
    fun `deleteUser should delete an existing user and return true`() {
        val addedUser = userRepository.addUser(createTestMockUser())

        val isDeleted = userRepository.deleteUser(addedUser.id!!)
        assertTrue(isDeleted)

        val foundUser = userRepository.getUserById(addedUser.id!!)
        assertNull(foundUser)
    }

    @Test
    fun `deleteUser should return false if user to delete does not exist`() {
        val isDeleted = userRepository.deleteUser(999L)
        assertFalse(isDeleted)
    }

    private fun createTestMockUser(): User {
        val geo = Geo(lat = "10.0", lng = "20.0")
        val address = Address(street = "Test St", suite = "Apt 1", city = "Test City", zipcode = "12345", geo = geo)
        return User(
            name = "Test User",
            username = "testuser",
            email = "test@example.com",
            phone = "123",
            website = "test.com",
            address = address
        )
    }
}