package source.Queries;

import exceptions.TableExceptions;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class QueryBuilderImpl<T> implements QueryBuilder<T> {

    private List<T> records;

    private Map<Object, List<T>> groupedRecords;

    public QueryBuilderImpl(List<T> records) {
        this.records = records;
    }

    public QueryBuilderImpl(Map<Object, List<T>> groupedRecords){
        this.groupedRecords = groupedRecords;
    }

    @Override
    public List<T> view() {
        return Optional.ofNullable(records)
                .orElseThrow(() -> new TableExceptions("Records have been grouped. Call (mergeGroups)"));
    }

    @Override
    public Map<Object, List<T>> viewGroups() {
        return Optional.ofNullable(groupedRecords)
                .orElseThrow(() -> new TableExceptions("No groups have been generated yet. Call (groupRecords) first"));
    }

    @Override
    public <V> QueryBuilder<T> sorted(String field, Class<V> fieldClass, boolean ascending){
        List<T> sortedRecords = view().stream().sorted((record1, record2) -> {
            V record1Value = getValueFromRecordMethod(record1, field, fieldClass);
            V record2Value = getValueFromRecordMethod(record2, field, fieldClass);
            return Comparable.class.cast(record1Value).compareTo(record2Value);
        }).collect(Collectors.toList());

        if (!ascending){
            Collections.reverse(sortedRecords);
        }
        this.records = sortedRecords;
        return this;
    }

    @Override
    public <V> QueryBuilder<T> filtered (String field, Class<V> fieldClass, V value){
        records = view().stream().filter(record -> {
            V valueInRecordField = getValueFromRecordMethod(record, field, fieldClass);
            return  valueInRecordField.equals(value);
        }).collect(Collectors.toList());
        return this;
    }

    @Override
    public QueryBuilder<T> aggregateRecords(BinaryOperator<T> aggregator) {
        records = aggregate(view(), aggregator);
        return this;
    }

    @Override
    public QueryBuilder<T> aggregateGroups(BinaryOperator<T> aggregator) {
        viewGroups().forEach((group, items) -> groupedRecords.put(group, aggregate(items, aggregator)));
        return this;
    }

    private List<T> aggregate(List<T> itemsToAggregate, BinaryOperator<T> aggregator){
        return itemsToAggregate.stream().reduce(aggregator).map(Arrays::asList).orElseGet(ArrayList::new);
    }

    @Override
    public <V> QueryBuilder<T> groupRecords(String field, Class<V> fieldClass) {
       this.groupedRecords = view().stream().collect(Collectors.groupingBy(
                record -> getValueFromRecordMethod(record, field, fieldClass))
        );
       this.records = null;
       return this;
    }

    @Override
    public QueryBuilder<T> mergeGroups() {
        this.records = groupedRecords.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        groupedRecords = null;
        return this;
    }

    private  <V> V getValueFromRecordMethod(T record, String field, Class<V> fieldClass) {
        try {
            return fieldClass.cast(record.getClass().getMethod(field).invoke(record));
        }  catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new TableExceptions("Either records object type do not contain the given field or the value class is incorrect");
        }
    }
}
