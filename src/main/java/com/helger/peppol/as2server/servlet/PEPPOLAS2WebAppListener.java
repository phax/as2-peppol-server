/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.vendor.VendorInfo;
import com.helger.html.hc.config.HCSettings;
import com.helger.peppol.as2server.app.AppSettings;
import com.helger.peppol.as2server.app.WebAppSettings;
import com.helger.photon.basic.app.CApplicationID;
import com.helger.photon.basic.app.PhotonPathMapper;
import com.helger.photon.basic.app.request.RequestParameterHandlerURLPathNamed;
import com.helger.photon.basic.app.request.RequestParameterManager;
import com.helger.photon.core.ajax.servlet.SecureApplicationAjaxServlet;
import com.helger.photon.core.api.servlet.SecureApplicationAPIServlet;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.core.app.init.IApplicationInitializer;
import com.helger.photon.core.servlet.AbstractSecureApplicationServlet;
import com.helger.photon.core.servlet.AbstractWebAppListenerMultiApp;
import com.helger.xservlet.requesttrack.RequestTracker;

/**
 * Callbacks for the application server
 *
 * @author Philip Helger
 */
public final class PEPPOLAS2WebAppListener extends AbstractWebAppListenerMultiApp <LayoutExecutionContext>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PEPPOLAS2WebAppListener.class);

  @Override
  protected void onTheVeryBeginning (@Nonnull final ServletContext aSC)
  {}

  @Override
  protected String getInitParameterDebug (@Nonnull final ServletContext aSC)
  {
    return WebAppSettings.getGlobalDebug ();
  }

  @Override
  protected String getInitParameterProduction (@Nonnull final ServletContext aSC)
  {
    return WebAppSettings.getGlobalProduction ();
  }

  @Override
  protected String getDataPath (@Nonnull final ServletContext aSC)
  {
    return WebAppSettings.getDataPath ();
  }

  @Override
  protected boolean shouldCheckFileAccess (@Nonnull final ServletContext aSC)
  {
    return WebAppSettings.isCheckFileAccess ();
  }

  private static void _ensureDirectoryExisting (@Nonnull @Nonempty final String sWhat, @Nullable final File aPath)
  {
    if (aPath == null)
      throw new IllegalStateException ("The special PEPPOL AS2 folder for " + sWhat + " is unspecified!");
    if (!aPath.exists ())
    {
      FileOperations.createDirRecursive (aPath);
      s_aLogger.info ("Created special PEPPOL AS2 folder '" + aPath.getAbsolutePath () + "'");
    }
    else
    {
      if (s_aLogger.isDebugEnabled ())
        s_aLogger.debug ("Special PEPPOL AS2 folder '" +
                         aPath.getAbsolutePath () +
                         "' for " +
                         sWhat +
                         " already exists!");
    }
  }

  private static void _checkSettings ()
  {
    _ensureDirectoryExisting ("sending", AppSettings.getFolderForSending ());
    _ensureDirectoryExisting ("sending errors", AppSettings.getFolderForSendingErrors ());
    _ensureDirectoryExisting ("receiving", AppSettings.getFolderForReceiving ());
    _ensureDirectoryExisting ("receiving errors", AppSettings.getFolderForReceivingErrors ());
  }

  @Override
  @Nonnull
  @Nonempty
  protected ICommonsMap <String, IApplicationInitializer <LayoutExecutionContext>> getAllInitializers ()
  {
    final ICommonsMap <String, IApplicationInitializer <LayoutExecutionContext>> ret = new CommonsHashMap <> ();
    ret.put (CApplicationID.APP_ID_SECURE, new PEPPOLAS2Initializer ());
    return ret;
  }

  @Override
  protected void initGlobals ()
  {
    VendorInfo.setVendorName ("Philip Helger");
    VendorInfo.setInceptionYear (2015);
    VendorInfo.setVendorEmail ("as2-peppol-server@helger.com");
    VendorInfo.setVendorURL ("https://github.com/phax/as2-peppol-server");
    VendorInfo.setVendorLocation ("Vienna");

    super.initGlobals ();

    _checkSettings ();

    // KBErrorCallback.doSetup (false);

    // On demand
    HCSettings.setOutOfBandDebuggingEnabled (false);

    // set default UI properties

    // Must be invoked before meta manager init!
    // KBDefaultSecurity.init ();

    // Ensure meta managers are initialized

    // Use path name instead of parameter name for menu item IDs
    RequestParameterManager.getInstance ().setParameterHandler (new RequestParameterHandlerURLPathNamed ());

    // For debugging
    if (GlobalDebug.isDebugMode ())
    {
      RequestTracker.getInstance ().getRequestTrackingMgr ().setLongRunningCheckEnabled (false);
    }
    else
    {
      // For performance reasons
      ValueEnforcer.setEnabled (false);
    }

    PhotonPathMapper.removeAllPathMappings ();
    PhotonPathMapper.setApplicationServletPathMapping (CApplicationID.APP_ID_SECURE,
                                                       AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH);
    PhotonPathMapper.setAjaxServletPathMapping (CApplicationID.APP_ID_SECURE,
                                                SecureApplicationAjaxServlet.SERVLET_DEFAULT_PATH);
    PhotonPathMapper.setAPIServletPathMapping (CApplicationID.APP_ID_SECURE,
                                               SecureApplicationAPIServlet.SERVLET_DEFAULT_PATH);
    PhotonPathMapper.setDefaultApplicationID (CApplicationID.APP_ID_SECURE);
  }

  @Override
  protected void afterContextAndInitializersDone (@Nonnull final ServletContext aSC)
  {
    super.afterContextAndInitializersDone (aSC);
  }

  @Override
  protected void beforeContextDestroyed (@Nonnull final ServletContext aSC)
  {
    super.beforeContextDestroyed (aSC);
  }
}
