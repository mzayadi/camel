/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.sjms.producer;

import javax.jms.Message;

import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.sjms.MessageProducerResources;
import org.apache.camel.component.sjms.SjmsProducer;
import org.apache.camel.component.sjms.TransactionCommitStrategy;
import org.apache.camel.component.sjms.tx.DefaultTransactionCommitStrategy;

/**
 * A Camel Producer that provides the InOnly Exchange pattern.
 */
public class InOnlyProducer extends SjmsProducer {

    public InOnlyProducer(final Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected TransactionCommitStrategy getCommitStrategy() {
        if (isEndpointTransacted()) {
            return super.getCommitStrategy() == null ? new DefaultTransactionCommitStrategy() : super.getCommitStrategy();
        }
        return null;
    }

    @Override
    public void sendMessage(final Exchange exchange, final AsyncCallback callback, final MessageProducerResources producer, final ReleaseProducerCallback releaseProducerCallback) throws Exception {
        try {
            Message message = getEndpoint().getBinding().makeJmsMessage(exchange, producer.getSession());
            producer.getMessageProducer().send(message);
        } catch (Exception e) {
            exchange.setException(new CamelExchangeException("Unable to complete sending the JMS message", exchange, e));
        } finally {
            releaseProducerCallback.release(producer);
            callback.done(isSynchronous());
        }
    }

}
