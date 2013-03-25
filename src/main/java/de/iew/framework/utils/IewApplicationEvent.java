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

package de.iew.framework.utils;

import org.springframework.context.ApplicationEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Implements a base application event and adds additional functionality for use with spring integration.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 25.03.13 - 16:58
 */
public class IewApplicationEvent extends ApplicationEvent implements Serializable {

    private volatile boolean synchronous = true;

    public IewApplicationEvent(Object source) {
        super(source);
    }

    public IewApplicationEvent(Object source, boolean synchronous) {
        super(source);
        this.synchronous = synchronous;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    // The source property is transient but we need it for source checking after the event is delivered from spring
    // integrations message store.

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(source.toString());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        source = in.readObject();
    }
}
