package service;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import source.DataSourceImpl;
import source.Order;
import source.Queries.QueryBuilder;
import source.Queries.QueryBuilderImpl;
import source.Tables.TableData;
import source.Tables.TableDataImpl;
import utils.TestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrderServiceImplTest {

    private OrderService testUnit;

    private TestUtils testUtils = new TestUtils();

    private TableData<Order> ordersTable;

    private QueryBuilder<Order> queryBuilder;

    public void initTestUnitWithMockTable(){
        ordersTable = (TableDataImpl<Order>) Mockito.mock(TableDataImpl.class);
        queryBuilder = (QueryBuilder<Order>) Mockito.mock(QueryBuilderImpl.class);
        Mockito.when(ordersTable.queryBuilder()).thenReturn(queryBuilder);
        testUnit = new OrderServiceImpl(new DataSourceImpl(ordersTable));
    }

    @Test
    public void testNewOrdersCanBeMade(){
        initTestUnitWithMockTable();
        List<Order> newOrders = Arrays.asList(new Order(), new Order());
        testUnit.registerOrders(newOrders);
        Mockito.verify(ordersTable, Mockito.times(1)).addRecords(newOrders);
    }

    @Test
    public void testOrdersCanBeCanceled(){
        initTestUnitWithMockTable();
        testUnit.cancelOrders(1, 2);
        Mockito.verify(ordersTable, Mockito.times(1)).deleteRecords(1, 2);
    }

    @Test
    public void testOrdersCanBeUpdated(){
        initTestUnitWithMockTable();
        Order order = new Order();
        testUnit.updateOrder(1, order);
        Mockito.verify(ordersTable, Mockito.times(1)).updateRecord(1, order);
    }

    @Test
    public void testOrdersCanBeSummarizedThenSorted(){
        assertSummarizedInfoIsAggregatedAndSorted(Order.TYPE.SELL);
        assertSummarizedInfoIsAggregatedAndSorted(Order.TYPE.BUY);
    }

    @Test
    public void testOrderHistoryForOrderTypeCanBeReturned(){
        initTestUnitWithMockTable();
        Mockito.when(queryBuilder.filtered(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.view()).thenReturn(null);

        testUnit.getOrderHistoryByType(Order.TYPE.BUY);
        InOrder order = Mockito.inOrder(queryBuilder);
        order.verify(queryBuilder, Mockito.times(1)).filtered("getOrderType", Order.TYPE.class, Order.TYPE.BUY);
        order.verify(queryBuilder, Mockito.times(1)).view();
    }

    private void setUpTestUnitWithRecordsThatCanBeGrouped(double priceForGroups, double quantityForGroups, double valueForUngrouped){

        // create 6 records. 3 for BUY and 3 for SELL. Each group of 3 will have 2 with same price that will be grouped and 1 order with a unique price
        List<Order> records = testUtils.generateRecords(3, Order.TYPE.BUY);
        records.addAll(testUtils.generateRecords(3, Order.TYPE.SELL));

        queryBuilder = new QueryBuilderImpl<>(records);
        Map<Object, List<Order>> groupedByOrderType = queryBuilder.groupRecords("getOrderType", Order.TYPE.class).viewGroups();

        //set price and quantity for orders will be grouped by price
        groupedByOrderType.forEach((group, groupItems) -> {
            groupItems.forEach(record -> setPriceAndQuantityForAnOrder(record, priceForGroups, quantityForGroups));
        });

        // set price and quantity for orders that will not be grouped by price. Both Buy and Sell groups should have a single ungroupable order
        setPriceAndQuantityForAnOrder(groupedByOrderType.get(Order.TYPE.SELL).get(0), valueForUngrouped, valueForUngrouped);
        setPriceAndQuantityForAnOrder(groupedByOrderType.get(Order.TYPE.BUY).get(0), valueForUngrouped, valueForUngrouped);

        queryBuilder = new QueryBuilderImpl<>(groupedByOrderType);
        ordersTable = new TableDataImpl<>(queryBuilder.mergeGroups().view());
        testUnit = new OrderServiceImpl(new DataSourceImpl(ordersTable));
    }

    private Order setPriceAndQuantityForAnOrder(Order order, double price, double quantity){
        order.setPrice(price);
        order.setQuantity(quantity);
        return order;
    }

    private void assertPriceAndQuantityInOrder(Order order, Double expectedPrice, Double expectedQuantity){
        Assert.assertEquals(expectedPrice, order.getPrice());
        Assert.assertEquals(expectedQuantity, order.getQuantity());
    }

    private void assertSummarizedInfoIsAggregatedAndSorted(Order.TYPE type){

        setUpTestUnitWithRecordsThatCanBeGrouped(20, 5, 3);
        List<Order> summarizedRecords = testUnit.getSummarizedInfo(type);

        Order aggregatedRecord;
        Order unAggregatedRecord;

        // in Buy orders the first element
        if (Order.TYPE.BUY == type){
            aggregatedRecord = summarizedRecords.get(0);
            unAggregatedRecord = summarizedRecords.get(1);
        } else {
            aggregatedRecord = summarizedRecords.get(1);
            unAggregatedRecord = summarizedRecords.get(0);
        }

        Assert.assertEquals(2, summarizedRecords.size());
        assertPriceAndQuantityInOrder(aggregatedRecord, (double)20, (double)10);
        assertPriceAndQuantityInOrder(unAggregatedRecord, (double)3, (double)3);
    }
}
