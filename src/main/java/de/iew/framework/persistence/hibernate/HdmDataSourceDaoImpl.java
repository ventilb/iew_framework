/*
 * Copyright 2012-2013 Manuel Schulze <manuel_schulze@i-entwicklung.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iew.framework.persistence.hibernate;

import de.iew.framework.domain.DataSource;
import de.iew.framework.persistence.DataSourceDao;
import org.springframework.stereotype.Repository;

/**
 * Implementierung der {@link DataSourceDao}-Schnittstelle für Hibernate.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 30.11.12 - 21:40
 */
@Repository(value = "dataSourceDao")
public class HdmDataSourceDaoImpl extends AbstractHbmDomainModelDaoImpl<DataSource> implements DataSourceDao {
}
