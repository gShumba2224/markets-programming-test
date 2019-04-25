package source.Tables;

import source.Queries.QueryBuilder;

import java.util.List;

public interface TableData<T>  {

    public List<T> getRecords();

    void deleteRecords(Integer... recordNums);

    public void deleteRecords(List<Integer> recordNums);

    public void updateRecord(int recordNum, T newRecord);

    public void addRecords(T ...newRecords);

    public void addRecords(List<T> newRecords);

    public QueryBuilder<T> queryBuilder();

}
