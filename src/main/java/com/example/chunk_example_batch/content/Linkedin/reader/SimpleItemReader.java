package com.example.chunk_example_batch.content.Linkedin.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleItemReader implements ItemReader<String> {
    private List<String> dataSet = new ArrayList<>();
    private Iterator<String> iterator;

    public SimpleItemReader(){
        this.dataSet.add("John");
        this.dataSet.add("Jane");
        this.dataSet.add("Rachel");
        this.dataSet.add("Monica");
        this.dataSet.add("Phoebe");
        this.dataSet.add("Chandler");
        this.dataSet.add("Ross");
        this.dataSet.add("Joey");
        this.dataSet.add("Tedd");
        this.dataSet.add("Gunther");
        this.dataSet.add("Marshall");
        this.dataSet.add("Lily");
        this.dataSet.add("Janice");
        this.dataSet.add("Barney");
        this.dataSet.add("Betty");
        this.iterator = this.dataSet.iterator();
    }
    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return this.iterator.hasNext() ? this.iterator.next() : null;
    }
}
