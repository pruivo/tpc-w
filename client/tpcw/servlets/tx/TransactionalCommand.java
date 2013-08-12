package tx;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionalCommand<T> {
    public T doIt(Connection con) throws SQLException;
}