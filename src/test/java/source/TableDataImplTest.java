package source;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import source.Tables.TableData;
import utils.TestUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TableDataImplTest {

    private TableData<Order> testUnit;

    private TestUtils testUtils = new TestUtils();


    @Before
    public void initTestUnit(){
        testUnit = testUtils.generateTable(3);
    }

    @Test
    public void testNewRecordsAreCreated(){
        int originalNumOfRecords = testUnit.getRecords().size();
        List<Order> orders = IntStream.range(0, 2)
                .mapToObj(i -> new Order("user" + i, (double) i, (double) i, Order.TYPE.BUY))
                .collect(Collectors.toList());

        testUnit.addRecords(orders);
        Assert.assertEquals(originalNumOfRecords + orders.size(), testUnit.getRecords().size());
        IntStream.range(originalNumOfRecords, originalNumOfRecords + 2).forEach(i -> {
            Assert.assertEquals(orders.get(i - originalNumOfRecords), testUnit.getRecords().get(i));
        });
    }

    @Test
    public void testRecordsAreDeleted(){
        int originalNumOfRecords = testUnit.getRecords().size();
        Order recordToDelete = testUnit.getRecords().get(0);
        testUnit.deleteRecords(0);

        Assert.assertEquals(originalNumOfRecords - 1, testUnit.getRecords().size());
        Assert.assertFalse(testUnit.getRecords().contains(recordToDelete));
    }

    @Test
    public void testRecordsAreUpdated(){
        Order order = new Order("New User", (double)50, (double)50, Order.TYPE.SELL);
        testUnit.updateRecord(0, order);
        Assert.assertSame(testUnit.getRecords().get(0), order);
    }

}
