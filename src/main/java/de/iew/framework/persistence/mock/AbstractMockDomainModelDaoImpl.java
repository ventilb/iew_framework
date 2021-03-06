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

package de.iew.framework.persistence.mock;

import de.iew.framework.domain.AbstractModel;
import de.iew.framework.domain.utils.DomainModelByIdComparator;
import de.iew.framework.persistence.DomainModelDao;

import java.util.*;

/**
 * Basisklasse für Mock-Implementierungen für
 * {@link DomainModelDao}-Schnittstellen.
 * <p>
 * Die Mock-Implementierungen sollen das Schreiben von Unittests erleichtern.
 * </p>
 * <p>
 * Die Implementierung ist nicht Thread-Safe.
 * </p>
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 17.11.12 - 10:14
 */
public abstract class AbstractMockDomainModelDaoImpl<M extends AbstractModel> implements DomainModelDao<M> {

    /**
     * Die als letztes verwendete Id in diesem Dao. Wird für das Abspeichern
     * neuer Domainmodelle benötigt.
     */
    protected long lastIdUsed = 0;

    /**
     * Aufschlüsselung (nach Id) der gespeicherten Domainmodelle.
     */
    private final Hashtable<Long, M> models = new Hashtable<Long, M>();

    public M save(M domainModel) {
        if (domainModel.getId() == null) {
            domainModel.setId(newId());
        }

        this.models.put(domainModel.getId(), domainModel);

        this.lastIdUsed = Math.max(this.lastIdUsed, domainModel.getId());

        return domainModel;
    }

    public void remove(M domainModel) {
        this.models.remove(domainModel.getId());
    }

    public M findById(long id) {
        return this.models.get(id);
    }

    public Collection<M> findAll() {
        return this.models.values();
    }

    public Collection<M> findAll(long firstResult, long maxResults) {
        List<M> models = (List<M>) findAll();
        Collections.sort(models, DomainModelByIdComparator.ASCENDING);

        if (models.size() < firstResult) {
            return models.subList((int) firstResult, (int) (firstResult + maxResults));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public Collection<M> findAllOrderedAscending() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    public long count() {
        return this.models.size();
    }

    public void refresh(M domainModel) {
    }

    protected long newId() {
        return ++this.lastIdUsed;
    }
}
