/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysema.query.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.QueryException;

/**
 * SQLResultIterator is an Iterator adapter for JDBC result sets with customizable projections
 *
 * @author tiwe
 *
 * @param <T>
 */
public abstract class SQLResultIterator<T> implements CloseableIterator<T> {

    @Nullable
    private Boolean next = null;

    private final ResultSet rs;

    private final Statement stmt;

    public SQLResultIterator(Statement stmt, ResultSet rs) {
        this.stmt = stmt;
        this.rs = rs;
    }

    @Override
    public void close() {
        try{
            try {
                if (rs != null) {
                    rs.close();
                }
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
        }catch(SQLException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public boolean hasNext() {
        if (next == null) {
            try {
                next = rs.next();
            } catch (SQLException e) {
                close();
                throw new QueryException(e);
            }
        }
        return next;
    }

    @Override
    public T next() {
        if (hasNext()) {
            next = null;
            try {
                return produceNext(rs);
            } catch (Exception e) {
                close();
                throw new QueryException(e);
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    protected abstract T produceNext(ResultSet rs) throws Exception;

    @Override
    public void remove() {
        try {
            rs.deleteRow();
        } catch (SQLException e) {
            close();
            throw new QueryException(e);
        }
    }

}
