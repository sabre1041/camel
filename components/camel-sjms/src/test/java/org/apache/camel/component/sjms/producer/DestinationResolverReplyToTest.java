/**
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

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sjms.SjmsComponent;
import org.apache.camel.component.sjms.jms.DestinationResolver;
import org.apache.camel.component.sjms.support.JmsTestSupport;
import org.apache.camel.component.sjms.support.MyDestinationResolver;
import org.apache.camel.model.language.SimpleExpression;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test that verifies the ability of the SJMS
 * {@link DestinationResolver} to process a replyTo Destination
 */
public class DestinationResolverReplyToTest extends JmsTestSupport {

    @EndpointInject(uri = "direct:resolverIn")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    private String responseMessage = "Response Message";

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        SjmsComponent sjmsComponent = camelContext.getComponent("sjms", SjmsComponent.class);
        sjmsComponent.setDestinationResolver(new MyDestinationResolver("test.invalid.replyto.queue", "test.valid.replyto.queue"));

        return camelContext;
    }

    @Before
    public void setup() {
        responseMessage = "Response Message";
    }

    @Test
    public void testDestinationResolver() throws InterruptedException {

        String header = "destinationHeader";
        String headerValue = "Destination Header";

        resultEndpoint.expectedBodiesReceived(responseMessage);
        resultEndpoint.message(0).header(header).isEqualTo(headerValue);

        producerTemplate.sendBodyAndHeader("Sent Message", header, headerValue);

        resultEndpoint.assertIsSatisfied();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:resolverIn").to("sjms:queue:test.valid.queue?namedReplyTo=test.invalid.replyto.queue").to("mock:result");

                from("sjms:queue:test.valid.queue").setBody(new SimpleExpression(responseMessage)).to("sjms:queue:test.valid.replyto.queue");
            }
        };
    }

}
