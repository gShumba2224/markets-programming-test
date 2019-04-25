package source;

import source.Tables.TableData;
import source.Tables.TableDataImpl;

public class DataSourceImpl implements DataSource {

    private TableData<Order> orders;

    public DataSourceImpl() {
        orders = new TableDataImpl<>();
    }

    public DataSourceImpl(TableData<Order> ordersTable) {
        this.orders = ordersTable;
    }

    public TableData<Order> ordersTable() {
        return orders;
    }
}
