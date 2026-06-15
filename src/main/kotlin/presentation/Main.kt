package presentation

import data.repository.CategoryRepositoryImpl
import data.repository.ProductRepositoryImpl
import data.repository.UserRepositoryImpl
import data.source.DatabaseManager
import domain.model.Address
import domain.model.Category
import domain.model.Geo
import domain.model.Product
import domain.model.User
import domain.usecase.CategoryUseCases
import domain.usecase.ProductUseCases
import domain.usecase.UserUseCases
import java.sql.SQLException

fun main() {
    val appRunner = AppRunner()
    appRunner.start()
}

class AppRunner {
    private lateinit var userUseCases: UserUseCases
    private lateinit var categoryUseCases: CategoryUseCases
    private lateinit var productUseCases: ProductUseCases

    fun start() {
        println("--- Clean Architecture Example with SQLite ---")

        initializeDatabase()
        setupDependencies()
        loadInitialData() // Cargar datos iniciales después de configurar dependencias

        runUserOperations()
        runCategoryOperations()
        runProductOperations()
    }

    private fun initializeDatabase() {
        println("Initializing database...")
        try {
            DatabaseManager.initDatabase()
            println("Database initialized successfully.")
        } catch (e: SQLException) {
            println("Error initializing database: ${e.message}")
            e.printStackTrace()
            // Si la base de datos no se puede inicializar, las operaciones posteriores fallarán.
            // Considerar una estrategia de salida o manejo de errores más robusta en una app real.
        }
    }

    private fun setupDependencies() {
        val userRepository = UserRepositoryImpl()
        userUseCases = UserUseCases(userRepository)

        val categoryRepository = CategoryRepositoryImpl()
        categoryUseCases = CategoryUseCases(categoryRepository)

        val productRepository = ProductRepositoryImpl()
        productUseCases = ProductUseCases(productRepository)
    }

    private fun loadInitialData() {
        println("\nChecking for initial data...")
        // Usamos los casos de uso para verificar si hay datos, manteniendo la capa de datos aislada
        if (userUseCases.getAllUsers().isEmpty() || categoryUseCases.getAllCategories().isEmpty() || productUseCases.getAllProducts().isEmpty()) {
            println("No users, categories or products found. Loading initial data from 'initial_data.sql'...")

            try {
                DatabaseManager.loadSqlFile("initial_data.sql")
                println("Initial data loaded successfully.")
            } catch (e: Exception) {
                println("Error loading initial data: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("Users, categories and products already exist. Skipping initial data load.")
        }
    }

    private fun runUserOperations() {
        println("\n--- Running User Operations ---")

        // 1. Get all users (including initial ones if loaded)
        displayAllUsers()

        // Pick a user to demonstrate update/delete
        val users = userUseCases.getAllUsers()
        val userForDemo = if (users.isNotEmpty()) {
            // Usar un usuario existente para la demo, e.g., el primero o uno específico
            users.firstOrNull { it.username == "alices" } ?: users.first()
        } else {
            // Si no hay usuarios iniciales, añadir uno nuevo para la demo
            println("\nNo initial users found for demo, adding a new one...")
            addNewUser()
        }

        userForDemo?.let { user ->
            println("\nSelected user for demo: $user")

            // 2. Get user by ID
            user.id?.let { userId ->
                displayUserById(userId)
            }

            // 3. Update user
            val updatedUser = updateExistingUser(user)
            println("\nUpdated User: $updatedUser")

            // Verify update
            updatedUser?.id?.let { userId ->
                displayUserById(userId)
            }

            // 4. Delete user
            updatedUser?.id?.let { userId ->
                deleteUser(userId)
            }

            // Verify deletion
            displayAllUsers()
        } ?: println("\nCould not find or create a user for demonstration.")
    }

    private fun runCategoryOperations() {
        println("\n--- Running Category Operations ---")

        displayAllCategories()

        // Add a new category
        val newCategory = Category(name = "Sports", description = "Equipment and apparel for sports")
        val addedCategory = categoryUseCases.createCategory(newCategory)
        println("\nAdded Category: $addedCategory")

        displayAllCategories()

        // Get category by ID
        addedCategory.id?.let { categoryId ->
            displayCategoryById(categoryId)
        }

        // Update category
        val categoryToUpdate = addedCategory.copy(name = "Outdoor & Sports", description = "Gear for outdoor activities and sports")
        val updatedCategory = categoryUseCases.updateCategory(categoryToUpdate)
        println("\nUpdated Category: $updatedCategory")

        displayCategoryById(updatedCategory?.id ?: -1)

        // Delete category
        updatedCategory?.id?.let { categoryId ->
            deleteCategory(categoryId)
        }

        displayAllCategories()
    }

    private fun runProductOperations() {
        println("\n--- Running Product Operations ---")

        displayAllProducts()

        // Get an existing category for new product
        val electronicsCategory = categoryUseCases.getAllCategories().firstOrNull { it.name == "Electronics" }

        if (electronicsCategory != null) {
            // Add a new product
            val newProduct = Product(name = "Wireless Earbuds", price = 99.99, category = electronicsCategory)
            val addedProduct = productUseCases.createProduct(newProduct)
            println("\nAdded Product: $addedProduct")

            displayAllProducts()

            // Get product by ID
            addedProduct.id?.let { productId ->
                displayProductById(productId)
            }

            // Update product
            val updatedProduct = addedProduct.copy(price = 89.99)
            val resultUpdate = productUseCases.updateProduct(updatedProduct)
            println("\nUpdated Product: $resultUpdate")

            displayProductById(resultUpdate?.id ?: -1)

            // Delete product
            resultUpdate?.id?.let { productId ->
                deleteProduct(productId)
            }

            displayAllProducts()
        } else {
            println("\nElectronics category not found, skipping product operations demo.")
        }
    }

    private fun addNewUser(): User {
        val newGeo = Geo(lat = "40.7128", lng = "-74.0060")
        val newAddress = Address(street = "123 Main St", suite = "Apt 101", city = "New York", zipcode = "10001", geo = newGeo)
        val newUser = User(
            name = "John Doe",
            username = "johndoe_new", // Changed username to avoid UNIQUE constraint violation if run multiple times
            email = "john.doe.new@example.com",
            phone = "123-456-7890",
            website = "johndoe_new.com",
            address = newAddress
        )
        return userUseCases.createUser(newUser)
    }

    private fun displayAllUsers() {
        val allUsers = userUseCases.getAllUsers()
        println("\nAll Users:")
        if (allUsers.isEmpty()) {
            println("No users found.")
        } else {
            allUsers.forEach { println(it) }
        }
    }

    private fun displayUserById(userId: Long) {
        val userById = userUseCases.getUserById(userId)
        println("\nUser by ID ($userId): $userById")
    }

    private fun updateExistingUser(user: User): User? {
        val updatedGeo = user.address.geo.copy(lat = "34.0522", lng = "-118.2437") // Los Angeles
        val updatedAddress = user.address.copy(street = "456 Oak Ave", city = "Los Angeles", zipcode = "90012", geo = updatedGeo)
        val userToUpdate = user.copy(
            name = "Jonathan Doe Updated", // Changed name for clarity
            email = "jonathan.doe.updated@example.com",
            address = updatedAddress
        )
        return userUseCases.updateUser(userToUpdate)
    }

    private fun deleteUser(userId: Long) {
        val isDeleted = userUseCases.deleteUser(userId)
        println("\nUser with ID $userId deleted: $isDeleted")
    }

    private fun displayAllCategories() {
        val allCategories = categoryUseCases.getAllCategories()
        println("\nAll Categories:")
        if (allCategories.isEmpty()) {
            println("No categories found.")
        } else {
            allCategories.forEach { println(it) }
        }
    }

    private fun displayCategoryById(categoryId: Long) {
        val categoryById = categoryUseCases.getCategoryById(categoryId)
        println("\nCategory by ID ($categoryId): $categoryById")
    }

    private fun deleteCategory(categoryId: Long) {
        val isDeleted = categoryUseCases.deleteCategory(categoryId)
        println("\nCategory with ID $categoryId deleted: $isDeleted")
    }

    private fun displayAllProducts() {
        val allProducts = productUseCases.getAllProducts()
        println("\nAll Products:")
        if (allProducts.isEmpty()) {
            println("No products found.")
        } else {
            allProducts.forEach { println(it) }
        }
    }

    private fun displayProductById(productId: Long) {
        val productById = productUseCases.getProductById(productId)
        println("\nProduct by ID ($productId): $productById")
    }

    private fun deleteProduct(productId: Long) {
        val isDeleted = productUseCases.deleteProduct(productId)
        println("\nProduct with ID $productId deleted: $isDeleted")
    }
}
