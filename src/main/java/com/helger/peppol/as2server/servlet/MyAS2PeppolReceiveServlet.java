/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.peppol.as2server.servlet;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;

import com.helger.as2servlet.AS2ReceiveServlet;
import com.helger.commons.string.StringHelper;
import com.helger.commons.system.SystemProperties;

/**
 * Special version of the {@link AS2ReceiveServlet} customizing the path to the
 * AS2 data file. The absolute path to the configuration file can be provided
 * using the "ap.server.config.path" system property.
 *
 * @author Philip Helger
 */
public class MyAS2PeppolReceiveServlet extends AS2ReceiveServlet
{
  public static final String DEFAULT_CONFIG_FILENAME = "as2-server-data/as2-server-config.xml";

  @Override
  @Nonnull
  protected File getConfigurationFile () throws ServletException
  {
    String sConfigurationFilename = SystemProperties.getPropertyValue ("ap.server.config.path");
    if (StringHelper.hasNoText (sConfigurationFilename))
    {
      // Default value
      sConfigurationFilename = DEFAULT_CONFIG_FILENAME;
    }

    try
    {
      final File aFile = new File (sConfigurationFilename).getCanonicalFile ();
      return aFile;
    }
    catch (final IOException ex)
    {
      throw new ServletException ("Failed to get the canonical file from '" + sConfigurationFilename + "'");
    }
  }
}
