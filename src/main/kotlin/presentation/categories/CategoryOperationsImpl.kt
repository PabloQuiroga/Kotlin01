package presentation.categories

import data.repository.CategoryRepositoryImpl
import domain.model.Category
import domain.usecase.CategoryUseCases

class CategoryOperationsImpl : CategoryOperations {
    private val categoryUseCases: CategoryUseCases

    init {
        val categoryRepository = CategoryRepositoryImpl()
        categoryUseCases = CategoryUseCases(categoryRepository)
    }

    override fun getAllCategories(): List<Category> {
        return categoryUseCases.getAllCategories()
    }

    override fun getCategoryById(categoryId: Long): Category? {
        return categoryUseCases.getCategoryById(categoryId)
    }

    override fun getCategoryByName(categoryName: String): Category? {
        val categories = getAllCategories()
        return categories.find { it.name == categoryName }
    }

    override fun getCategoryByDescription(categoryDescription: String): Category? {
        val categories = getAllCategories()
        return categories.find { it.description.contains(categoryDescription) }
    }

    override fun addNewCategory(newCategory: Category): Category {
        return categoryUseCases.createCategory(newCategory)
    }

    override fun updateExistingCategory(category: Category): Category? {
        return categoryUseCases.updateCategory(category)
    }

    override fun deleteCategory(categoryId: Long): Boolean {
        return categoryUseCases.deleteCategory(categoryId)
    }

}