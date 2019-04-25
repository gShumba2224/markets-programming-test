package source;

import source.Tables.TableData;

public interface DataSource {

    public TableData<Order> ordersTable();
}
