package data.repository

import data.source.DatabaseManager
import domain.model.Address
import domain.model.Geo
import domain.model.User
import domain.repository.UserRepository
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

class UserRepositoryImpl : UserRepository {

    private fun <T> withConnection(block: (Connection) -> T): T {
        DatabaseManager.getConnection().use { conn ->
            return block(conn)
        }
    }

    override fun getAllUsers(): List<User> = withConnection { conn ->
        val users = mutableListOf<User>()
        val sql = """
            SELECT
                u.id AS user_id, u.name AS user_name, u.username, u.email, u.phone, u.website,
                a.id AS address_id, a.street, a.suite, a.city, a.zipcode,
                g.id AS geo_id, g.lat, g.lng
            FROM users u
            JOIN addresses a ON u.address_id = a.id
            JOIN geos g ON a.geo_id = g.id
        """.trimIndent()
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            while (rs.next()) {
                users.add(mapResultSetToUser(rs))
            }
        }
        users
    }

    override fun getUserById(id: Long): User? = withConnection { conn ->
        val sql = """
            SELECT
                u.id AS user_id, u.name AS user_name, u.username, u.email, u.phone, u.website,
                a.id AS address_id, a.street, a.suite, a.city, a.zipcode,
                g.id AS geo_id, g.lat, g.lng
            FROM users u
            JOIN addresses a ON u.address_id = a.id
            JOIN geos g ON a.geo_id = g.id
            WHERE u.id = ?
        """.trimIndent()
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
            val userSql = "INSERT INTO users(name, username, email, phone, website, address_id) VALUES(?, ?, ?, ?, ?, ?)"
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
        val geoSql = "INSERT INTO geos(lat, lng) VALUES(?, ?)"
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
        val addressSql = "INSERT INTO addresses(street, suite, city, zipcode, geo_id) VALUES(?, ?, ?, ?, ?)"
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
            val userSql = "UPDATE users SET name = ?, username = ?, email = ?, phone = ?, website = ? WHERE id = ?"
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
        val geoSql = "UPDATE geos SET lat = ?, lng = ? WHERE id = ?"
        conn.prepareStatement(geoSql).use { pstmt ->
            pstmt.setString(1, geo.lat)
            pstmt.setString(2, geo.lng)
            pstmt.setLong(3, geo.id ?: throw SQLException("Geo ID cannot be null for update."))
            pstmt.executeUpdate()
        }
    }

    private fun updateAddress(conn: Connection, address: Address) {
        val addressSql = "UPDATE addresses SET street = ?, suite = ?, city = ?, zipcode = ? WHERE id = ?"
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
            val selectIdsSql = "SELECT address_id FROM users WHERE id = ?"
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

            val selectGeoIdSql = "SELECT geo_id FROM addresses WHERE id = ?"
            var geoId: Long? = null
            conn.prepareStatement(selectGeoIdSql).use { pstmt ->
                pstmt.setLong(1, addressId!!)
                val rs = pstmt.executeQuery()
                if (rs.next()) {
                    geoId = rs.getLong("geo_id")
                }
            }

            // Delete User
            val deleteUserSql = "DELETE FROM users WHERE id = ?"
            val userRowsAffected = conn.prepareStatement(deleteUserSql).use { pstmt ->
                pstmt.setLong(1, id)
                pstmt.executeUpdate()
            }

            // Delete Address (if cascade is not fully handled by DB or for explicit control)
            val deleteAddressSql = "DELETE FROM addresses WHERE id = ?"
            conn.prepareStatement(deleteAddressSql).use { pstmt ->
                pstmt.setLong(1, addressId!!)
                pstmt.executeUpdate()
            }

            // Delete Geo (if cascade is not fully handled by DB or for explicit control)
            if (geoId != null) {
                val deleteGeoSql = "DELETE FROM geos WHERE id = ?"
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
