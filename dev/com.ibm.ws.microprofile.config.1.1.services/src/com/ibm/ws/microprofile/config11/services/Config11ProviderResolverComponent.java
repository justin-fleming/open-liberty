/*******************************************************************************
 * Copyright (c) 2018, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.microprofile.config11.services;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.osgi.service.component.annotations.Component;

import com.ibm.ws.container.service.state.ApplicationStateListener;
import com.ibm.ws.microprofile.config.impl.ConfigProviderResolverImpl;

import io.openliberty.microprofile.config.internal.common.ConfigIntrospectionProvider;

@Component(service = { ConfigProviderResolver.class, ApplicationStateListener.class, ConfigIntrospectionProvider.class }, property = { "service.vendor=IBM" }, immediate = true)
public class Config11ProviderResolverComponent extends ConfigProviderResolverImpl {}