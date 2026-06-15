package data.repository

import data.source.DatabaseManager
import domain.model.Address
import domain.model.Geo
import domain.model.User
import domain.repository.UserRepository
import util.SqlLoader
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class UserRepositoryImpl : UserRepository {

    private val userSqlQueries: Map<String, String> =
        SqlLoader.loadSqlQueries("sql/user.sql")

    private fun <T> withConnection(block: (Connection) -> T): T {
        return DatabaseManager.executeWithConnection(block)
    }

    override fun getAllUsers(): List<User> = withConnection { conn ->
        val users = mutableListOf<User>()
        val sql = userSqlQueries["getAllUsers"]
            ?: throw IllegalStateException("SQL query 'getAllUsers' not found.")
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            while (rs.next()) {
                users.add(mapResultSetToUser(rs))
            }
        }
        users
    }

    override fun getUserById(id: Long): User? = withConnection { conn ->
        val sql = userSqlQueries["getUserById"]
            ?: throw IllegalStateException("SQL query 'getUserById' not found.")
        conn.prepareStatement(sql).use { pstmt ->
            pstmt.setLong(1, id)
            val rs = pstmt.executeQuery()
            if (rs.next()) {
                mapResultSetToUser(rs)
            } else {
                null
            }
        }
    }

    override fun addUser(user: User): User = withConnection { conn ->
        conn.autoCommit = false // Start transaction

        try {
            // 1. Insert Geo
            val geoId = insertGeo(conn, user.address.geo)

            // 2. Insert Address
            val addressId = insertAddress(conn, user.address.copy(geo = user.address.geo.copy(id = geoId)), geoId)

            // 3. Insert User
            val userSql = userSqlQueries["addUser"]
                ?: throw IllegalStateException("SQL query 'addUser' not found.")
            conn.prepareStatement(userSql, java.sql.Statement.RETURN_GENERATED_KEYS).use { pstmt ->
                pstmt.setString(1, user.name)
                pstmt.setString(2, user.username)
                pstmt.setString(3, user.email)
                pstmt.setString(4, user.phone)
                pstmt.setString(5, user.website)
                pstmt.setLong(6, addressId)
                pstmt.executeUpdate()

                val generatedKeys = pstmt.generatedKeys
                if (generatedKeys.next()) {
                    val newUserId = generatedKeys.getLong(1)
                    conn.commit() // Commit transaction
                    return@withConnection user.copy(id = newUserId, address = user.address.copy(id = addressId, geo = user.address.geo.copy(id = geoId)))
                } else {
                    throw SQLException("Failed to retrieve auto-generated user ID.")
                }
            }
        } catch (e: SQLException) {
            conn.rollback() // Rollback transaction on error
            throw e
        } finally {
            conn.autoCommit = true // Restore auto-commit
        }
    }

    private fun insertGeo(conn: Connection, geo: Geo): Long {
        val geoSql = userSqlQueries["insertGeo"]
            ?: throw IllegalStateException("SQL query 'insertGeo' not found.")
        conn.prepareStatement(geoSql, java.sql.Statement.RETURN_GENERATED_KEYS).use { pstmt ->
            pstmt.setString(1, geo.lat)
            pstmt.setString(2, geo.lng)
            pstmt.executeUpdate()

            val generatedKeys = pstmt.generatedKeys
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1)
            } else {
                throw SQLException("Failed to retrieve auto-generated geo ID.")
            }
        }
    }

    private fun insertAddress(conn: Connection, address: Address, geoId: Long): Long {
        val addressSql = userSqlQueries["insertAddress"]
            ?: throw IllegalStateException("SQL query 'insertAddress' not found.")
        conn.prepareStatement(addressSql, java.sql.Statement.RETURN_GENERATED_KEYS).use { pstmt ->
            pstmt.setString(1, address.street)
            pstmt.setString(2, address.suite)
            pstmt.setString(3, address.city)
            pstmt.setString(4, address.zipcode)
            pstmt.setLong(5, geoId)
            pstmt.executeUpdate()

            val generatedKeys = pstmt.generatedKeys
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1)
            } else {
                throw SQLException("Failed to retrieve auto-generated address ID.")
            }
        }
    }

    override fun updateUser(user: User): User? = withConnection { conn ->
        conn.autoCommit = false // Start transaction

        try {
            // 1. Update Geo
            user.address.geo.id?.let { geoId ->
                updateGeo(conn, user.address.geo.copy(id = geoId))
            } ?: throw SQLException("Geo ID is required for update.")

            // 2. Update Address
            user.address.id?.let { addressId ->
                updateAddress(conn, user.address.copy(id = addressId))
            } ?: throw SQLException("Address ID is required for update.")

            // 3. Update User
            val userSql = userSqlQueries["updateUser"]
                ?: throw IllegalStateException("SQL query 'updateUser' not found.")
            conn.prepareStatement(userSql).use { pstmt ->
                pstmt.setString(1, user.name)
                pstmt.setString(2, user.username)
                pstmt.setString(3, user.email)
                pstmt.setString(4, user.phone)
                pstmt.setString(5, user.website)
                pstmt.setLong(6, user.id ?: throw SQLException("User ID cannot be null for update."))
                val rowsAffected = pstmt.executeUpdate()

                if (rowsAffected > 0) {
                    conn.commit() // Commit transaction
                    return@withConnection user
                } else {
                    conn.rollback()
                    return@withConnection null
                }
            }
        } catch (e: SQLException) {
            conn.rollback() // Rollback transaction on error
            throw e
        } finally {
            conn.autoCommit = true // Restore auto-commit
        }
    }

    private fun updateGeo(conn: Connection, geo: Geo) {
        val geoSql = userSqlQueries["updateGeo"]
            ?: throw IllegalStateException("SQL query 'updateGeo' not found.")
        conn.prepareStatement(geoSql).use { pstmt ->
            pstmt.setString(1, geo.lat)
            pstmt.setString(2, geo.lng)
            pstmt.setLong(3, geo.id ?: throw SQLException("Geo ID cannot be null for update."))
            pstmt.executeUpdate()
        }
    }

    private fun updateAddress(conn: Connection, address: Address) {
        val addressSql = userSqlQueries["updateAddress"]
            ?: throw IllegalStateException("SQL query 'updateAddress' not found.")
        conn.prepareStatement(addressSql).use { pstmt ->
            pstmt.setString(1, address.street)
            pstmt.setString(2, address.suite)
            pstmt.setString(3, address.city)
            pstmt.setString(4, address.zipcode)
            pstmt.setLong(5, address.id ?: throw SQLException("Address ID cannot be null for update."))
            pstmt.executeUpdate()
        }
    }

    override fun deleteUser(id: Long): Boolean = withConnection { conn ->
        conn.autoCommit = false // Start transaction

        try {
            // Get address_id and geo_id first to delete them
            val selectIdsSql = userSqlQueries["selectAddressIdForDelete"]
                ?: throw IllegalStateException("SQL query 'selectAddressIdForDelete' not found.")
            var addressId: Long? = null
            conn.prepareStatement(selectIdsSql).use { pstmt ->
                pstmt.setLong(1, id)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    addressId = rs.getLong("address_id")
                }
            }

            if (addressId == null) {
                conn.rollback()
                return@withConnection false
            }

            val selectGeoIdSql = userSqlQueries["selectGeoIdForDelete"]
                ?: throw IllegalStateException("SQL query 'selectGeoIdForDelete' not found.")
            var geoId: Long? = null
            conn.prepareStatement(selectGeoIdSql).use { pstmt ->
                pstmt.setLong(1, addressId!!)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    geoId = rs.getLong("geo_id")
                }
            }

            // Delete User
            val deleteUserSql = userSqlQueries["deleteUser"]
                ?: throw IllegalStateException("SQL query 'deleteUser' not found.")
            val userRowsAffected = conn.prepareStatement(deleteUserSql).use { pstmt ->
                pstmt.setLong(1, id)
                pstmt.executeUpdate()
            }

            // Delete Address (if cascade is not fully handled by DB or for explicit control)
            val deleteAddressSql = userSqlQueries["deleteAddress"]
                ?: throw IllegalStateException("SQL query 'deleteAddress' not found.")
            conn.prepareStatement(deleteAddressSql).use { pstmt ->
                pstmt.setLong(1, addressId!!)
                pstmt.executeUpdate()
            }

            // Delete Geo (if cascade is not fully handled by DB or for explicit control)
            if (geoId != null) {
                val deleteGeoSql = userSqlQueries["deleteGeo"]
                    ?: throw IllegalStateException("SQL query 'deleteGeo' not found.")
                conn.prepareStatement(deleteGeoSql).use { pstmt ->
                    pstmt.setLong(1, geoId!!)
                    pstmt.executeUpdate()
                }
            }

            if (userRowsAffected > 0) {
                conn.commit() // Commit transaction
                return@withConnection true
            } else {
                conn.rollback()
                return@withConnection false
            }
        } catch (e: SQLException) {
            conn.rollback() // Rollback transaction on error
            throw e
        } finally {
            conn.autoCommit = true // Restore auto-commit
        }
    }

    private fun mapResultSetToUser(rs: ResultSet): User {
        val geo = Geo(
            id = rs.getLong("geo_id"),
            lat = rs.getString("lat"),
            lng = rs.getString("lng")
        )
        val address = Address(
            id = rs.getLong("address_id"),
            street = rs.getString("street"),
            suite = rs.getString("suite"),
            city = rs.getString("city"),
            zipcode = rs.getString("zipcode"),
            geo = geo
        )
        return User(
            id = rs.getLong("user_id"),
            name = rs.getString("user_name"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            phone = rs.getString("phone"),
            website = rs.getString("website"),
            address = address
        )
    }
}