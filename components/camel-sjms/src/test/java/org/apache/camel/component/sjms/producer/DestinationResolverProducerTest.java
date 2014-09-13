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
import org.junit.Test;

/**
 * Integration test that verifies the ability of the SJMS Producer to utilize a
 * {@link DestinationResolver}
 */
public class DestinationResolverProducerTest extends JmsTestSupport {

    @EndpointInject(uri = "direct:resolverIn")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        SjmsComponent sjmsComponent = camelContext.getComponent("sjms", SjmsComponent.class);
        sjmsComponent.setDestinationResolver(new MyDestinationResolver("test.invalid.queue", "test.valid.queue"));

        return camelContext;
    }

    @Test
    public void testDestinationResolver() throws InterruptedException {

        String body = "Custom Destination";
        String header = "destinationHeader";
        String headerValue = "Destination Header";

        resultEndpoint.expectedBodiesReceived(body);
        resultEndpoint.message(0).header(header).isEqualTo(headerValue);

        producerTemplate.sendBodyAndHeader(body, header, headerValue);

        resultEndpoint.assertIsSatisfied();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:resolverIn").to("sjms:queue:test.invalid.queue");
                from("sjms:queue:test.valid.queue").to("mock:result");
            }
        };
    }

}
