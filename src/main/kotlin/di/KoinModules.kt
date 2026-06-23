package di

import data.repository.CategoryRepositoryImpl
import data.repository.ProductRepositoryImpl
import data.repository.UserRepositoryImpl
import data.source.DatabaseManager
import domain.repository.CategoryRepository
import domain.repository.ProductRepository
import domain.repository.UserRepository
import domain.usecase.CategoryUseCases
import domain.usecase.ProductUseCases
import domain.usecase.UserUseCases
import org.koin.dsl.module
import presentation.categories.CategoryOperations
import presentation.categories.CategoryOperationsImpl
import presentation.products.ProductOperations
import presentation.products.ProductOperationsImpl
import presentation.users.UserOperations
import presentation.users.UserOperationsImpl

val categoryModule = module {
    single { DatabaseManager }
    // Registrar la implementación concreta
    single { CategoryRepositoryImpl() }
    // Registrar la interfaz, obteniendo la implementación concreta
    single<CategoryRepository> { get<CategoryRepositoryImpl>() }
    factory { CategoryUseCases(get<CategoryRepository>()) }
    factory<CategoryOperations> { CategoryOperationsImpl(get<CategoryUseCases>()) }
}

val productModule = module {
    single { DatabaseManager }
    // Registrar la implementación concreta
    single { ProductRepositoryImpl() }
    // Registrar la interfaz, obteniendo la implementación concreta
    single<ProductRepository> { get<ProductRepositoryImpl>() }
    factory { ProductUseCases(get<ProductRepository>()) }
    factory<ProductOperations> { ProductOperationsImpl(get<ProductUseCases>()) }
}

val userModule = module {
    single { DatabaseManager }
    // Registrar la implementación concreta
    single { UserRepositoryImpl() }
    // Registrar la interfaz, obteniendo la implementación concreta
    single<UserRepository> { get<UserRepositoryImpl>() }
    factory { UserUseCases(get<UserRepository>()) }
    factory<UserOperations> { UserOperationsImpl(get<UserUseCases>()) }
}

val allModules = listOf(
    categoryModule,
    productModule,
    userModule
)