package source.Queries;

import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public interface QueryBuilder<T> {


    public List<T> view();

    public Map<Object, List<T>> viewGroups();

    public QueryBuilder<T> aggregateRecords(BinaryOperator<T> aggregator);

    public QueryBuilder<T> aggregateGroups(BinaryOperator<T> aggregator);

    public <V> QueryBuilder<T> groupRecords(String field, Class<V> fieldClass);

    public QueryBuilder<T> mergeGroups();

    public <V> QueryBuilder<T> sorted(String field, Class<V> fieldClass, boolean ascending);

    public <V> QueryBuilder<T> filtered (String field, Class<V> fieldClass, V value);
}
