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

package de.iew.framework.spring.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.util.Assert;

/**
 * Implements a simple {@link MessageHandler} to act as a consumer for message channels. Spring integration throws an
 * exception if a channel does not have any subscribers.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 25.03.13 - 18:18
 */
public class NullMessageSubscriber implements InitializingBean, DisposableBean, MessageHandler {
    private static final Log log = LogFactory.getLog(NullMessageSubscriber.class);

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.messageChannels);
        subscribeAll();
    }

    public void destroy() throws Exception {
        unsubscribeAll();
    }

    public void handleMessage(Message<?> message) throws MessagingException {
    }

    public void subscribeAll() {
        if (this.messageChannels != null) {
            for (SubscribableChannel channel : this.messageChannels) {
                channel.subscribe(this);
            }
        }
    }

    public void unsubscribeAll() {
        if (this.messageChannels != null) {
            for (SubscribableChannel channel : this.messageChannels) {
                channel.unsubscribe(this);
            }
        }
    }

    // Spring dependencies ////////////////////////////////////////////////////

    private SubscribableChannel[] messageChannels;

    public void setMessageChannel(SubscribableChannel messageChannel) {
        setMessageChannels(new SubscribableChannel[]{
                messageChannel
        });
    }

    public void setMessageChannels(SubscribableChannel[] messageChannels) {
        unsubscribeAll();
        this.messageChannels = messageChannels;
    }
}
