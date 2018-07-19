/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.peppol.as2server.app;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.scope.singleton.AbstractGlobalSingleton;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.configfile.ConfigFile;
import com.helger.settings.exchange.configfile.ConfigFileBuilder;

/**
 * This class provides access to the web application settings. The order of the
 * properties file resolving is as follows:
 * <ol>
 * <li>Check for the value of the system property
 * <code>peppol.ap.webapp.properties.path</code></li>
 * <li>Check for the value of the system property
 * <code>ap.webapp.properties.path</code></li>
 * <li>The filename <code>private-webapp.properties</code> in the root of the
 * classpath</li>
 * <li>The filename <code>webapp.properties</code> in the root of the
 * classpath</li>
 * </ol>
 *
 * @author Philip Helger
 */
public final class WebAppSettings extends AbstractGlobalSingleton
{
  private static final Logger LOGGER = LoggerFactory.getLogger (WebAppSettings.class);

  private static final ConfigFile s_aConfigFile;

  static
  {
    final ConfigFileBuilder aCFB = new ConfigFileBuilder ().addPathFromSystemProperty ("peppol.ap.webapp.properties.path")
                                                           .addPathFromSystemProperty ("ap.webapp.properties.path")
                                                           .addPath ("private-webapp.properties")
                                                           .addPath ("webapp.properties");

    s_aConfigFile = aCFB.build ();
    if (!s_aConfigFile.isRead ())
      throw new IllegalStateException ("Failed to read PEPPOL AP web app properties from " + aCFB.getAllPaths ());
    LOGGER.info ("Read PEPPOL AP web app properties from " + s_aConfigFile.getReadResource ().getPath ());
  }

  @Deprecated
  @UsedViaReflection
  private WebAppSettings ()
  {}

  @Nonnull
  public static ISettings getSettingsObject ()
  {
    return s_aConfigFile.getSettings ();
  }

  @Nonnull
  public static IReadableResource getSettingsResource ()
  {
    return s_aConfigFile.getReadResource ();
  }

  /**
   * @return <code>true</code> if global debug is enabled. Should be turned off
   *         in production systems!
   */
  @Nullable
  public static String getGlobalDebug ()
  {
    return s_aConfigFile.getAsString ("global.debug");
  }

  /**
   * @return <code>true</code> if global production mode is enabled. Should only
   *         be turned on in production systems!
   */
  @Nullable
  public static String getGlobalProduction ()
  {
    return s_aConfigFile.getAsString ("global.production");
  }

  /**
   * @return The path where the application stores its data. Should be an
   *         absolute path.
   */
  @Nullable
  public static String getDataPath ()
  {
    return s_aConfigFile.getAsString ("webapp.datapath");
  }

  public static boolean isCheckFileAccess ()
  {
    return s_aConfigFile.getAsBoolean ("webapp.checkfileaccess", false);
  }

  /**
   * @return <code>true</code> if this is a public testable version,
   *         <code>false</code> if not.
   */
  public static boolean isTestVersion ()
  {
    return s_aConfigFile.getAsBoolean ("webapp.testversion", GlobalDebug.isDebugMode ());
  }
}
