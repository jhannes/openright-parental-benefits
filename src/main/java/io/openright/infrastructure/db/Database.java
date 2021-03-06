package io.openright.infrastructure.db;

import io.openright.infrastructure.util.ExceptionUtil;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private final DataSource dataSource;
    private final static ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

    public Database(String jndiName) {
        try {
            this.dataSource = (DataSource) new InitialContext().lookup(jndiName);
        } catch (NamingException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    public class Row {

        private final ResultSet rs;
        private final Map<String, Integer> columnMap = new HashMap<>();

        public Row(ResultSet rs) throws SQLException {
            this.rs = rs;
            for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
                String tableName = rs.getMetaData().getTableName(i);
                String columnName = rs.getMetaData().getColumnName(i);

                this.columnMap.put(tableName + "." + columnName, i);
            }
        }

        public String getString(String string) throws SQLException {
            return rs.getString(string);
        }

        public int getInt(String columnName) throws SQLException {
            return rs.getInt(columnName);
        }

        public long getLong(String tableName, String columnName) throws SQLException {
            return rs.getLong(getColumnIndex(tableName, columnName));
        }

        public long getLong(String columnName) throws SQLException {
            return rs.getLong(columnName);
        }

        public String getString(String tableName, String columnName) throws SQLException {
            return rs.getString(getColumnIndex(tableName, columnName));
        }

        public boolean getBoolean(String tableName, String columnName) throws SQLException {
            return rs.getBoolean(getColumnIndex(tableName, columnName));
        }

        public double getDouble(String tableName, String columnName) throws SQLException {
            return rs.getDouble(getColumnIndex(tableName, columnName));
        }

        private int getColumnIndex(String tableName, String columnName) {
            return columnMap.get(tableName + "." + columnName);
        }

        public JSONObject getJSON(String columnName) throws SQLException {
            return Database.this.getJSON(rs, columnName);
        }

        public Instant getInstant(String columnName) throws SQLException {
            return rs.getTimestamp(columnName).toInstant();
        }
    }

    public interface ConnectionCallback<T> {
        T run(Connection conn);
    }

    public interface StatementCallback<T> {
        T run(PreparedStatement stmt) throws SQLException;
    }

    public interface RowMapper<T> {
        T run(Row row) throws SQLException;
    }

    public <T> List<T> queryForList(String query, String parameter, RowMapper<T> mapper) {
        return queryForList(query, Collections.singletonList(parameter), mapper);
    }

    public <T> List<T> queryForList(String query, Long parameter, RowMapper<T> mapper) {
        return queryForList(query, Collections.singletonList(parameter), mapper);
    }

    public <T> List<T> queryForList(String query, List<Object> parameters, RowMapper<T> mapper) {
        return executeDbOperation(query, parameters, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                Row row = new Row(rs);
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.run(row));
                }
                return result;
            }
        });
    }

    public <T> Optional<T> queryForSingle(String query, List<Object> parameters, RowMapper<T> mapper) {
        return executeDbOperation(query, parameters, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                return mapSingleRow(rs, mapper);
            }
        });
    }

    private <T> Optional<T> mapSingleRow(ResultSet rs, RowMapper<T> mapper) throws SQLException {
        if (!rs.next()) {
            return Optional.empty();
        }
        T result = mapper.run(new Row(rs));
        if (rs.next()) {
            throw new RuntimeException("Duplicate");
        }
        return Optional.of(result);
    }


    public <T> T executeDbOperation(String query, List<Object> parameters, StatementCallback<T> statementCallback) {
        return doWithConnection(conn -> {
            long startTime = System.currentTimeMillis();
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                setParameters(statement, parameters);
                return statementCallback.run(statement);
            } catch (SQLException e) {
                log.info("Error while executing {}", query);
                throw ExceptionUtil.soften(e);
            } finally {
                long duration = System.currentTimeMillis() - startTime;
                log.info("Executing in {}ms: {} with params {}", duration, query, parameters);
            }
        });
    }

    private void setParameters(PreparedStatement statement, List<Object> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            setParameter(statement, parameters.get(i), i + 1);
        }
    }


    protected void setParameter(PreparedStatement statement, Object object, int parameterIndex) throws SQLException {
        if (object instanceof JSONObject) {
            PGobject o = new PGobject();
            o.setType("json");
            o.setValue(object.toString());
            statement.setObject(parameterIndex, o);
        } else if (object instanceof Instant) {
            statement.setTimestamp(parameterIndex, new Timestamp(((Instant) object).toEpochMilli()));
        } else {
            statement.setObject(parameterIndex, object);
        }
    }

    protected JSONObject getJSON(ResultSet rs, String columnName) throws SQLException {
        PGobject object = (PGobject) rs.getObject(columnName);
        return new JSONObject(new JSONTokener(object.getValue()));

    }

    public <T> T transactional(Supplier<T> supplier) {
        if (threadConnection.get() != null) {
            return supplier.get();
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            T result = supplier.get();
            conn.commit();
            return result;
        } catch (SQLException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    public void transactional(Runnable runnable) {
        if (threadConnection.get() != null) {
            runnable.run();
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            runnable.run();
            conn.commit();
        } catch (SQLException e) {
            throw ExceptionUtil.soften(e);
        }
    }

    public <T> T doWithConnection(ConnectionCallback<T> object) {
        if (threadConnection.get() != null) {
            return object.run(threadConnection.get());
        }

        try (Connection conn = dataSource.getConnection()) {
            return object.run(conn);
        } catch (SQLException e) {
            throw ExceptionUtil.soften(e);
        }
    }
}
