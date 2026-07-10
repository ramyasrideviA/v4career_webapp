package com.learn.learningarea.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class BranchRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return BranchContext.getBranch();
    }
}