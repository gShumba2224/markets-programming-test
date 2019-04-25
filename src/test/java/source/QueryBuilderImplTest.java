package source;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import source.Queries.QueryBuilder;
import source.Queries.QueryBuilderImpl;
import utils.TestUtils;

import java.util.List;
import java.util.Map;

public class QueryBuilderImplTest {

    private QueryBuilder<Order> testUnit;

    private List<Order> records;

    private TestUtils testUtils = new TestUtils();

    @Before
    public void initTestUnit(){
        records = testUtils.generateRecords(5);
        testUnit = new QueryBuilderImpl<>(records);
    }

    @Test
    public void testResultsAreSortedInAscending(){
        assertRecordsAreSorted(true);
    }

    @Test
    public void testResultsAreSortedInDescending(){
        assertRecordsAreSorted(false);
    }

    private void assertRecordsAreSorted(boolean ascending){
        List<Order> sortedRecords = testUnit.sorted("getPrice", Double.class, ascending).view();
        for (int index = 0; index < sortedRecords.size() - 1; index ++){
            Double price1 = sortedRecords.get(index).getPrice();
            Double price2 = sortedRecords.get(index + 1).getPrice();
            boolean isSorted = ascending ? (price1 < price2) : (price1 > price2);
            Assert.assertTrue(isSorted);
        }
    }

    @Test
    public void testQueryResultsAreFiltered(){
        List<Order> filteredResults = testUnit.filtered("getOrderType", Order.TYPE.class, Order.TYPE.BUY).view();

        filteredResults.forEach(record -> Assert.assertEquals(Order.TYPE.BUY, record.getOrderType()));

        long amountOfBuyOrders = records.stream().filter(record -> record.getOrderType() == Order.TYPE.BUY).count();
        Assert.assertEquals(amountOfBuyOrders, filteredResults.size());
    }


    @Test
    public void testRecordsCanBeGrouped(){
        Map<Object, List<Order>> groupedRecords = testUnit.groupRecords("getOrderType", Order.TYPE.class).viewGroups();
        List<Order> buyGroup = groupedRecords.get(Order.TYPE.BUY);
        List<Order> sellGroup = groupedRecords.get(Order.TYPE.SELL);

        buyGroup.forEach(record -> Assert.assertEquals(Order.TYPE.BUY, record.getOrderType()));
        sellGroup.forEach(record -> Assert.assertEquals(Order.TYPE.SELL, record.getOrderType()));

        // check that no records where lost in the grouping
        Assert.assertEquals(records.size(), sellGroup.size() + buyGroup.size());
    }


    @Test
    public void testUngroupedRecordsCanBeAggregated(){
       assertAggregation(testUnit.aggregateRecords(this::addPricesFromTwoRecords).view(), records);
    }

    @Test
    public void testGroupedRecordsCanBeAggregated(){
        Map<Object, List<Order>> groupsBeforeAggregation = testUnit.groupRecords("getOrderType", Order.TYPE.class).viewGroups();
        Map<Object, List<Order>> groupsWithAggregation = testUnit.aggregateGroups(this::addPricesFromTwoRecords).viewGroups();

        groupsWithAggregation.forEach((group, aggregatedItems) -> assertAggregation(groupsBeforeAggregation.get(group), aggregatedItems));

        // check only two groups where made. One for BUY and one for SELL
        Assert.assertEquals(2, groupsWithAggregation.size());
    }

    private void assertAggregation(List<Order> aggregatedRecords, List<Order> initialRecords){
        // check all records have been merged into 1
        Assert.assertEquals(1, aggregatedRecords.size());
        Order expectedAggregatedPrice = initialRecords.stream().reduce(this::addPricesFromTwoRecords).get();
        Assert.assertTrue(expectedAggregatedPrice.equals(aggregatedRecords.get(0)));
    }

    private Order addPricesFromTwoRecords (Order order1, Order order2){
        Order order = new Order();
        order.setPrice(order1.getPrice() + order2.getPrice());
        return order;
    }

    @Test
    public void testGroupsCanBeMerged() {
        Map<Object, List<Order>> groups = testUnit.groupRecords("getOrderType", Order.TYPE.class).viewGroups();
        List<Order> mergedRecords = testUnit.mergeGroups().view();

        int totalItemsInAllGroups = 0;

        for (List<Order> groupItems : groups.values()){
            totalItemsInAllGroups = totalItemsInAllGroups + groupItems.size();
            groupItems.forEach(record ->  Assert.assertTrue(mergedRecords.contains(record)));
        }
        Assert.assertEquals(totalItemsInAllGroups, mergedRecords.size());
    }


}
