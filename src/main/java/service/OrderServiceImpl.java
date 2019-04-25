package service;

import source.DataSource;
import source.Order;

import java.util.List;

public class OrderServiceImpl implements OrderService {

    private DataSource dataSource;


    public OrderServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void registerOrders(List<Order> orders) {
        dataSource.ordersTable().addRecords(orders);
    }

    @Override
    public void cancelOrders(Integer... orderNumbers) {
        dataSource.ordersTable().deleteRecords(orderNumbers);
    }

    @Override
    public void cancelOrders(List<Integer> orderNumbers) {
        dataSource.ordersTable().deleteRecords(orderNumbers);
    }

    @Override
    public void updateOrder(int orderNumber, Order newOrder) {
        dataSource.ordersTable().updateRecord(orderNumber, newOrder);
    }

    @Override
    public List<Order> getSummarizedInfo(Order.TYPE type) {
        return dataSource.ordersTable().queryBuilder()
                .filtered("getOrderType", Order.TYPE.class, type)
                .groupRecords("getPrice", Double.class)
                .aggregateGroups((record1, record2) -> {
                    Order order = new Order();
                    order.setUserID(record1.getUserID() + " & " + record2.getUserID());
                    order.setPrice(record1.getPrice());
                    order.setOrderType(record1.getOrderType());
                    order.setQuantity(record1.getQuantity() + record2.getQuantity());
                    return order;
                }).mergeGroups()
                .sorted("getPrice", Double.class, type == Order.TYPE.SELL)
                .view();
    }

    @Override
    public List<Order> getOrderHistoryByType(Order.TYPE type) {
        return dataSource.ordersTable().queryBuilder().filtered("getOrderType", Order.TYPE.class, type).view();
    }

    @Override
    public List<Order> getAllOrderHistory() {
        return dataSource.ordersTable().getRecords();
    }

}
