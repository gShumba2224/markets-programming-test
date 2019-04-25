package service;

import source.Order;

import java.util.List;

public interface OrderService {

    public void registerOrders(List<Order> orders);

    public void cancelOrders(Integer ...orderNumbers);

    public void cancelOrders(List<Integer> orderNumbers);

    public void updateOrder(int orderNumber, Order order);

    public List<Order> getSummarizedInfo(Order.TYPE type);

    public List<Order> getOrderHistoryByType(Order.TYPE type);

    public List<Order> getAllOrderHistory();
}
