package presentation.categories

import domain.model.Category

interface CategoryOperations {
    fun getAllCategories(): List<Category>
    fun getCategoryById(categoryId: Long): Category?
    fun getCategoryByName(categoryName: String): Category?
    fun getCategoryByDescription(categoryDescription: String): Category?

    fun addNewCategory(newCategory: Category): Category
    fun updateExistingCategory(category: Category): Category?
    fun deleteCategory(categoryId: Long): Boolean
}


