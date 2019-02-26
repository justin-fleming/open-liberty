/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.concurrent.mp.fat;

import java.io.File;

import org.eclipse.microprofile.concurrent.spi.ThreadContextProvider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.test.context.location.CityContextProvider;
import org.test.context.location.StateContextProvider;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.annotation.TestServlets;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import concurrent.mp.fat.cdi.web.MPConcurrentCDITestServlet;
import concurrent.mp.fat.multimodule.nocdi.MultiModuelAppTestServlet;

@RunWith(FATRunner.class)
public class MPConcurrentCDITest extends FATServletClient {

    private static final String CDI_APP = "MPConcurrentCDIApp";
    private static final String MULTI_MODULE_APP = "multiModuleAppWar";

    @Server("MPConcurrentCDITestServer")
    @TestServlets({
                    @TestServlet(servlet = MPConcurrentCDITestServlet.class, contextRoot = CDI_APP),
                    @TestServlet(servlet = MultiModuelAppTestServlet.class, contextRoot = MULTI_MODULE_APP)
    })
    public static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        ShrinkHelper.defaultApp(server, CDI_APP, "concurrent.mp.fat.cdi.web");

        JavaArchive customContextProviders = ShrinkWrap.create(JavaArchive.class, "customContextProviders.jar")
                        .addPackage("org.test.context.location")
                        .addAsServiceProvider(ThreadContextProvider.class, CityContextProvider.class, StateContextProvider.class);
        ShrinkHelper.exportToServer(server, "lib", customContextProviders);

        // Build an .ear app with 2 modules: 1 with CDI enabled, 1 with CDI disabled
        WebArchive noCdiModule = ShrinkHelper.buildDefaultApp(MULTI_MODULE_APP, "concurrent.mp.fat.multimodule.nocdi");
        JavaArchive withCdiModule = ShrinkHelper.buildJavaArchive("multiModuleAppLib", "concurrent.mp.fat.multimodule.withcdi");
        EnterpriseArchive multiModuleApp = ShrinkWrap.create(EnterpriseArchive.class, "multiModuleApp.ear")
                        .add(new FileAsset(new File("test-applications/multiModuleApp/resources/META-INF/application.xml")), "/META-INF/application.xml")
                        .addAsModule(noCdiModule)
                        .addAsLibraries(withCdiModule);
        ShrinkHelper.exportDropinAppToServer(server, multiModuleApp);

        server.startServer();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stopServer();
    }
}
