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

import de.iew.framework.domain.MessageBundle;
import de.iew.framework.domain.TextItem;
import de.iew.framework.persistence.MessageBundleDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

/**
 * Hibernate Implementierung der {@link MessageBundleDao}-Schnittstelle.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 25.11.12 - 12:42
 */
@Repository(value = "messageDao")
public class HbmMessageBundleDaoImpl extends AbstractHbmDomainModelDaoImpl<MessageBundle> implements MessageBundleDao {
    public MessageBundle findByTextKeyAndLocale(String textKey, String languageCode, String countryCode) {
        Criteria crit = createCriteria()
                .setCacheable(true)
                .createAlias("textItem", "txt", JoinType.INNER_JOIN)
                .add(Restrictions.eq("textKey", textKey))
                .add(Restrictions.like("txt.languageCode", languageCode))
                .add(Restrictions.like("txt.countryCode", countryCode));
        return (MessageBundle) crit.uniqueResult();
    }

    public List<MessageBundle> findByLocale(String languageCode, String countryCode) {
        Criteria crit = createCriteria()
                .setCacheable(true)
                .createAlias("textItem", "txt", JoinType.INNER_JOIN)
                .add(Restrictions.like("txt.languageCode", languageCode))
                .add(Restrictions.like("txt.countryCode", countryCode));
        return crit.list();
    }

    public List<MessageBundle> findByLocaleAndBasename(String languageCode, String countryCode, String basename) {
        Criteria crit = createCriteria()
                .setCacheable(true)
                .createAlias("textItem", "txt", JoinType.INNER_JOIN)
                .add(Restrictions.like("basename", basename))
                .add(Restrictions.like("txt.languageCode", languageCode))
                .add(Restrictions.like("txt.countryCode", countryCode));
        return crit.list();
    }

    public List<Locale> getSupportedLocales() {
        Criteria crit = getCurrentSession().createCriteria(TextItem.class)
                .setCacheable(true)
                .setProjection(
                        Projections.distinct(
                                Projections.projectionList()
                                        .add(Projections.property("languageCode"))
                                        .add(Projections.property("countryCode")))
                );
        crit.setCacheable(true);
        crit.setResultTransformer(LocaleTupleResultTransformer.DEFAULT);
        return crit.list();
    }
}
