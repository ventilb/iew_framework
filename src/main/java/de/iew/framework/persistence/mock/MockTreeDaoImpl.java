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

import de.iew.framework.domain.Tree;
import de.iew.framework.persistence.TreeDao;

/**
 * Mock-Implementierung der {@link de.iew.framework.persistence.TreeDao}-Schnittstelle.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 07.12.12 - 22:41
 */
public class MockTreeDaoImpl extends AbstractMockDomainModelDaoImpl<Tree> implements TreeDao {

    public Tree findTreeByLookupKey(String lookupKey) {
        for (Tree tree : findAll()) {
            if (lookupKey.equalsIgnoreCase(tree.getLookupKey())) {
                return tree;
            }
        }
        return null;
    }
}
