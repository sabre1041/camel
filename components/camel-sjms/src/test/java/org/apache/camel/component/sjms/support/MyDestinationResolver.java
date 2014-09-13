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
package org.apache.camel.component.sjms.support;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.camel.component.sjms.jms.DestinationResolver;

/**
 * Allows for a Destination to be replaced during testing
 */
public class MyDestinationResolver implements DestinationResolver {

    private String originalDestination;
    private String newDestination;

    public MyDestinationResolver(String originalDestination, String newDestination) {
        this.originalDestination = originalDestination;
        this.newDestination = newDestination;
    }

    @Override
    public Destination resolveDestination(Session session, String destinationName, boolean topic) throws JMSException {

        if (originalDestination.equals(destinationName)) {
            return session.createQueue(newDestination);
        } else {
            return session.createQueue(destinationName);
        }

    }

}
