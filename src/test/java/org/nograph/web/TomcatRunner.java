/* 

Copyright 2020 aholinch

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package org.nograph.web;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;


public class TomcatRunner 
{
    public static void main(String args[])
    {
    	try
    	{
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);

            Context ctx = tomcat.addContext("/nograph", new File("public").getAbsolutePath());
            Tomcat.initWebappDefaults(ctx);
            
            Tomcat.addServlet(ctx, "asset", AssetServlet.class.getName());
            ctx.addServletMapping("/asset/*", "asset");
            
            Tomcat.addServlet(ctx, "graph", GraphServlet.class.getName());
            ctx.addServletMapping("/graph/*", "graph");

            tomcat.start();
            tomcat.getServer().await();
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
}
