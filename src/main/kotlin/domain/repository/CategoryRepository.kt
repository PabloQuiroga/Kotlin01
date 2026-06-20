package domain.repository

import domain.model.Category

interface CategoryRepository {
    fun getAllCategories(): List<Category>
    fun getCategoryById(id: Long): Category?

    fun addCategory(category: Category): Category
    fun updateCategory(category: Category): Category?
    fun deleteCategory(id: Long): Boolean
}
