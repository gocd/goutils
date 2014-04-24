package com.thoughtworks.go.utils.database;

import org.apache.commons.dbcp.BasicDataSource;

public class H2Database {
    private BasicDataSource dataSource;

    public BasicDataSource createDataSource(String url) {
        if (this.dataSource == null) {
            BasicDataSource source = new BasicDataSource();
            configureDataSource(source, url);
            this.dataSource = source;
        }
        return dataSource;
    }

    private void configureDataSource(BasicDataSource source, String url) {
        source.setDriverClassName("org.h2.Driver");
        source.setUrl(url);
        source.setUsername("sa");
        source.setPassword("");
        source.setMaxActive(32);
        source.setMaxIdle(32);
    }


}
