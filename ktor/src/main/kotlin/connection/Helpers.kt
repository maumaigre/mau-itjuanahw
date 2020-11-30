package com.example.connection

import com.github.jasync.sql.db.Connection
import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.mysql.MySQLConnection
import com.github.jasync.sql.db.pool.ConnectionPool
import kotlinx.coroutines.future.await


suspend fun handleMysqlRequest(connectionPool: ConnectionPool<MySQLConnection>, query: String, values: List<Any> = emptyList()): QueryResult {
    return connectionPool!!.sendPreparedStatementAwait(query = query, values = values)
}


suspend fun Connection.sendPreparedStatementAwait(query: String, values: List<Any> = emptyList()): QueryResult =
        this.sendPreparedStatement(query, values).await()