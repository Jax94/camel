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
package org.apache.camel.component.cxf.jaxrs;

import java.lang.reflect.Method;

import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.message.Exchange;

public class CxfRsInvoker extends JAXRSInvoker {
    
    private Processor processor;
    private CxfRsEndpoint endpoint;
    
    public CxfRsInvoker(CxfRsEndpoint endpoint, Processor processor) {
        this.endpoint = endpoint;
        this.processor = processor;
    }
    
    protected Object performInvocation(Exchange cxfExchange, final Object serviceObject, Method method,
                                       Object[] paramArray) throws Exception {
        paramArray = insertExchange(method, paramArray, cxfExchange);
        
        ExchangePattern ep = ExchangePattern.InOut;
        if (method.getReturnType() == Void.class) {
            ep = ExchangePattern.InOnly;
        } 
        org.apache.camel.Exchange camelExchange = endpoint.createExchange(ep);
        CxfRsBinding binding = endpoint.getBinding();
        binding.populateExchangeFromCxfRsRequest(cxfExchange, camelExchange, method, paramArray);
        processor.process(camelExchange);
        if (camelExchange.getException() != null) {
            throw camelExchange.getException();
        }
        return binding.populateCxfRsResponseFromExchange(camelExchange, cxfExchange);
    }

}
