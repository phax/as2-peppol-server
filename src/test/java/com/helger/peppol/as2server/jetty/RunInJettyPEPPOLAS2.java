/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.as2server.jetty;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.system.SystemProperties;
import com.helger.peppol.utils.ConfigFile;
import com.helger.photon.jetty.JettyStarter;

/**
 * Run as2-peppol-server as a standalone web application in Jetty on port 8080.
 * <br>
 * http://localhost:8080/
 *
 * @author Philip Helger
 */
@Immutable
public final class RunInJettyPEPPOLAS2
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (RunInJettyPEPPOLAS2.class);

  public static void main (final String [] args) throws Exception
  {
    if (System.getSecurityManager () != null)
      throw new IllegalStateException ("Security Manager is set but not supported - aborting!");

    // Proxy configuration is simply applied by setting system properties
    final ConfigFile aCF = ConfigFile.create ("private-configProxy.properties", "configProxy.properties");
    for (final Map.Entry <String, Object> aEntry : aCF.getAllEntries ().entrySet ())
    {
      final String sKey = aEntry.getKey ();
      final String sValue = String.valueOf (aEntry.getValue ());
      SystemProperties.setPropertyValue (sKey, sValue);
      s_aLogger.info ("Setting Proxy property " + sKey + "=" + sValue);
    }

    new JettyStarter (RunInJettyPEPPOLAS2.class).run ();
  }
}
