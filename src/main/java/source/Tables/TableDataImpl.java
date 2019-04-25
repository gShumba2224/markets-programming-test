package source.Tables;

import source.Queries.QueryBuilder;
import source.Queries.QueryBuilderImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableDataImpl<T> implements TableData<T> {

    private List<T> records;

    public TableDataImpl() {
        records = new ArrayList<>();
    }

    public TableDataImpl(List<T> records){
        this.records = records;
    }

    @Override
    public List<T> getRecords(){
        return records;
    }

    @Override
    public void deleteRecords(Integer ... recordNums) {
        deleteRecords(Arrays.asList(recordNums));
    }

    @Override
    public void deleteRecords(List<Integer> recordNums) {
        recordNums.forEach(num -> records.remove((int)num));
    }

    @Override
    public void updateRecord(int recordNum, T newRecord) {
        records.set(recordNum, newRecord);
    }

    @Override
    public void addRecords(T... newRecords) {
        addRecords(Arrays.asList(newRecords));
    }

    @Override
    public void addRecords(List<T> newRecords) {
        records.addAll(newRecords);
    }

    @Override
    public QueryBuilder<T> queryBuilder() {
        return new QueryBuilderImpl<>(records);
    }
}
