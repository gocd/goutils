package com.thoughtworks.go.utils.database;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class H2DatabaseTest {

    @Test
    public void shouldReturnDataSourceForH2() throws Exception {
        H2Database database = new H2Database();
        String url = "file:h2://foo";
        BasicDataSource dataSource = database.createDataSource(url);
        assertThat(dataSource.getUrl(), is(url));
        assertThat(dataSource.getDriverClassName(), is("org.h2.Driver"));
        assertThat(dataSource.getUsername(), is("sa"));
        assertThat(dataSource.getPassword(), is(""));
        assertThat(dataSource.getMaxActive(), is(32));
        assertThat(dataSource.getMaxIdle(), is(32));
    }
}