package domain.usecase

import domain.model.Category
import domain.repository.CategoryRepository

class CategoryUseCases(private val categoryRepository: CategoryRepository) {

    fun getAllCategories(): List<Category> {
        return categoryRepository.getAllCategories()
    }

    fun getCategoryById(id: Long): Category? {
        return categoryRepository.getCategoryById(id)
    }

    fun createCategory(category: Category): Category {
        // Aquí podrías añadir lógica de negocio adicional antes de guardar
        return categoryRepository.addCategory(category)
    }

    fun updateCategory(category: Category): Category? {
        // Aquí podrías añadir lógica de negocio adicional antes de actualizar
        return categoryRepository.updateCategory(category)
    }

    fun deleteCategory(id: Long): Boolean {
        // Aquí podrías añadir lógica de negocio adicional antes de eliminar
        return categoryRepository.deleteCategory(id)
    }
}
