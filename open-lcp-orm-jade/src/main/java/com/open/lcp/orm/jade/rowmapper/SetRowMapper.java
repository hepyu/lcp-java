/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License i distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.open.lcp.orm.jade.rowmapper;

import java.util.Collection;
import java.util.HashSet;

import com.open.lcp.orm.jade.statement.StatementMetaData;

/**
 * 将SQL结果集的一行映射为Set
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * @author 廖涵 [in355hz@gmail.com]
 */
public class SetRowMapper extends AbstractCollectionRowMapper {

    public SetRowMapper(StatementMetaData modifier) {
        super(modifier);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection createCollection(int columnSize) {
        return new HashSet(columnSize * 2);
    }
}
