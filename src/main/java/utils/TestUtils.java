package utils;

import source.DataSource;
import source.DataSourceImpl;
import source.Order;
import source.Tables.TableData;
import source.Tables.TableDataImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUtils {

    public List<Order> generateRecords(int numOfRecords){

        List<Order> orders = generateRecords(numOfRecords/2, Order.TYPE.SELL);
        orders.addAll(generateRecords(numOfRecords - (numOfRecords)/2, Order.TYPE.BUY));
        return orders;
    }

    public List<Order> generateRecords(int numOfRecords, Order.TYPE type){
        return IntStream.range(0, numOfRecords)
                .mapToObj(index -> {
                    Order order = new Order();
                    order.setUserID("user" + index);
                    order.setQuantity(Math.random() * 100);
                    order.setPrice(Math.random() * 10);
                    order.setOrderType(type);
                    return order;
                }).collect(Collectors.toList());
    }

    public TableData<Order> generateTable(int numOfRecords){
        return new TableDataImpl<>(generateRecords(numOfRecords));
    }

    public DataSource generateDataSource(int numOfRecords){
        return new DataSourceImpl(generateTable(numOfRecords));
    }
}
