package com.example.connection

import com.github.jasync.sql.db.mysql.MySQLConnection
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder
import com.github.jasync.sql.db.pool.ConnectionPool
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import java.util.concurrent.TimeUnit


fun createConnectionPool(): ConnectionPool<MySQLConnection>? {

    val envVarsSecrets: HashMap<String, String?> = hashMapOf(
        "ITJ_DB_USER" to null,
        "ITJ_DB_HOST" to null,
        "ITJ_DB_PORT" to null,
        "ITJ_DB_PASSWORD" to null,
        "ITJ_DB_NAME" to null,
        "ITJ_DB_INSTANCENAME" to null
    )

    val projectId = "tokyo-epoch-296923"

    SecretManagerServiceClient.create().use { client ->
        for ((key, _) in envVarsSecrets) {
            val secretVersionName: SecretVersionName = SecretVersionName.of(projectId, key, "1")

            val response = client.accessSecretVersion(secretVersionName);

            val payload = response.payload.data.toStringUtf8()
            if (payload == null || payload == "") {
                return null
            }
            envVarsSecrets[key] = payload
        }
    }

    return MySQLConnectionBuilder.createConnectionPool {
        username = envVarsSecrets["ITJ_DB_USER"]!!
        host = envVarsSecrets["ITJ_DB_HOST"]!!
        password = envVarsSecrets["ITJ_DB_PASSWORD"]!!
        database = envVarsSecrets["ITJ_DB_NAME"]!!
        port = 3306
        maxActiveConnections = 100
        maxIdleTime = TimeUnit.MINUTES.toMillis(15)
        maxPendingQueries = 10_000
        connectionValidationInterval = TimeUnit.SECONDS.toMillis(30)
    }
}

