package io.openright.lib.db;

import io.openright.infrastructure.db.Database;

import java.util.List;

public interface Selectable {

    <T> List<T> list(Database.RowMapper<T> mapper);

    JdbcTable where(String field, Object value);

    Selectable orderBy(String string);
}
