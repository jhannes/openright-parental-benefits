package io.openright.lib.db;

import io.openright.infrastructure.db.Database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JdbcTable implements Selectable {

    public interface Inserter {
        void values(Map<String, Object> row);
    }

    private final Database database;
    private final String tableName;
    private final List<String> orderBy;
    private final List<String> innerJoins;
    private final List<String> whereClauses;
    private final List<Object> parameters;

    private JdbcTable(JdbcTable jdbcTable) {
        this.database = jdbcTable.database;
        this.tableName = jdbcTable.tableName;

        this.orderBy = new ArrayList<>(jdbcTable.orderBy);
        this.whereClauses = new ArrayList<>(jdbcTable.whereClauses);
        this.innerJoins = new ArrayList<>(jdbcTable.innerJoins);
        this.parameters = new ArrayList<>(jdbcTable.parameters);
    }

    public JdbcTable(Database database, String tableName) {
        this.database = database;
        this.tableName = tableName;

        this.orderBy = new ArrayList<>();
        this.whereClauses = new ArrayList<>();
        this.innerJoins = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    public long insertValues(Inserter inserter) {
        HashMap<String, Object> row = new HashMap<>();
        inserter.values(row);
        return database.executeDbOperation(insertQuery(row.keySet()), new ArrayList<>(row.values()), stmt -> {
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getLong("id");
        });
    }

    private String insertQuery(Collection<String> columnNames) {
        return "insert into " + tableName + " ("
                + String.join(", ", columnNames) + ") values ("
                + String.join(", ", repeat("?", columnNames.size()))
                + ") returning id";
    }

    public void updateValues(Inserter inserter) {
        HashMap<String, Object> row = new HashMap<>();
        inserter.values(row);

        List<Object> values = new ArrayList<>();
        values.addAll(row.values());
        values.addAll(getParameters());
        database.executeDbOperation(updateQuery(row.keySet()), values, stmt -> {
            stmt.executeUpdate();
            return null;
        });
    }

    private String updateQuery(Collection<String> updatedColumns) {
        return "update " + tableName + " set "
                + updatedColumns.stream().map(col -> col + " = ?").collect(Collectors.joining(", "))
                + getWhereClause();
    }

    @Override
    public <T> List<T> list(Database.RowMapper<T> mapper) {
        return database.queryForList(getQuery(), getParameters(), mapper);
    }

    public <T> Optional<T> single(Database.RowMapper<T> mapper) {
        return database.queryForSingle(getQuery(), getParameters(), mapper);
    }

    public void delete() {
        database.executeDbOperation(deleteQuery(), getParameters(), stmt -> {
            stmt.executeUpdate();
            return null;
        });
    }

    private String deleteQuery() {
        return "delete from " + tableName + getWhereClause();
    }

    private String getQuery() {
        StringBuilder query = new StringBuilder("select * from ").append(tableName);
        if (!this.innerJoins.isEmpty()) {
            query.append(" ").append(String.join(" ", innerJoins));
        }
        query.append(getWhereClause());
        if (!orderBy.isEmpty()) {
            query.append(" order by ").append(String.join(",", orderBy));
        }
        return query.toString();
    }

    private String getWhereClause() {
        if (whereClauses.isEmpty()) {
            return "";
        } else {
            return " where " + String.join(" and ", whereClauses);
        }
    }

    /**
     * Create a new DatabaseTable object with query parameters for where class. Fluent interface allows for chaining query parameters and values.
     *
     * @param field is the name of the database column
     * @return new instance of self with added set of query parameter and value
     */
    @Override
    public JdbcTable where(String field, Object value) {
        JdbcTable newTable = new JdbcTable(this);
        newTable.whereClauses.add(field + " = ?");
        newTable.parameters.add(value);
        return newTable;
    }

    public JdbcTable whereCondition(String condition, Object value) {
        JdbcTable newTable = new JdbcTable(this);
        newTable.whereClauses.add(condition);
        newTable.parameters.add(value);
        return newTable;
    }

    @Override
    public Selectable orderBy(String string) {
        JdbcTable table = new JdbcTable(this);
        table.orderBy.add(string);
        return table;
    }

    public Selectable join(String tableName, String myReference, String id) {
        JdbcTable table = new JdbcTable(this);
        table.innerJoins.add("INNER JOIN " + tableName + " on " + tableName + "." + id
                + " = " + this.tableName + "." + myReference);
        return table;
    }

    private List<Object> getParameters() {
        return parameters;
    }

    private List<String> repeat(String string, int count) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(string);
        }
        return result;
    }
}
