package util

import java.io.BufferedReader
import java.io.InputStreamReader

object SqlLoader {
    private val sqlCache = mutableMapOf<String, Map<String, String>>()

    fun loadSqlQueries(resourcePath: String): Map<String, String> {
        return sqlCache.getOrPut(resourcePath) {
            val queries = mutableMapOf<String, String>()
            val inputStream = javaClass.classLoader.getResourceAsStream(resourcePath)
                ?: throw IllegalArgumentException("Resource not found: $resourcePath")

            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var currentQueryName: String? = null
                val currentQueryBuilder = StringBuilder()

                reader.lineSequence().forEach { line ->
                    val trimmedLine = line.trim()
                    if (trimmedLine.startsWith("-- Query for ")) {
                        // If there's a previous query, store it
                        if (currentQueryName != null && currentQueryBuilder.isNotEmpty()) {
                            queries[currentQueryName!!] = currentQueryBuilder.toString().trim()
                        }
                        // Start a new query
                        currentQueryName = trimmedLine.substringAfter("-- Query for ").trim()
                        currentQueryBuilder.clear()
                    } else if (currentQueryName != null && trimmedLine.isNotEmpty()) {
                        currentQueryBuilder.append(trimmedLine).append(" ")
                    }
                }
                // Store the last query
                if (currentQueryName != null && currentQueryBuilder.isNotEmpty()) {
                    queries[currentQueryName!!] = currentQueryBuilder.toString().trim()
                }
            }
            queries
        }
    }
}