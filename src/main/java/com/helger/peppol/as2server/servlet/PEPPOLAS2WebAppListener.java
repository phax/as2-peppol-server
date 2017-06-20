/**
 * Copyright (C) 2012-2017 winenet GmbH - www.winenet.at
 * All Rights Reserved
 *
 * This file is part of the winenet-Kellerbuch software.
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is
 * strictly prohibited.
 */
package com.helger.peppol.as2server.servlet;

import java.io.File;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.ext.CommonsHashMap;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.io.file.FileOperations;
import com.helger.commons.vendor.VendorInfo;
import com.helger.html.hc.config.HCSettings;
import com.helger.peppol.as2server.app.AppSettings;
import com.helger.photon.basic.app.PhotonPathMapper;
import com.helger.photon.basic.app.request.RequestParameterHandlerURLPathNamed;
import com.helger.photon.basic.app.request.RequestParameterManager;
import com.helger.photon.core.ajax.servlet.SecureApplicationAjaxServlet;
import com.helger.photon.core.api.servlet.SecureApplicationAPIServlet;
import com.helger.photon.core.app.CApplication;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.core.app.init.IApplicationInitializer;
import com.helger.photon.core.requesttrack.RequestTracker;
import com.helger.photon.core.servlet.AbstractSecureApplicationServlet;
import com.helger.photon.core.servlet.AbstractWebAppListenerMultiApp;

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
    return AppSettings.getGlobalDebug ();
  }

  @Override
  protected String getInitParameterProduction (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getGlobalProduction ();
  }

  @Override
  protected String getDataPath (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getDataPath ();
  }

  @Override
  protected boolean shouldCheckFileAccess (@Nonnull final ServletContext aSC)
  {
    return AppSettings.isCheckFileAccess ();
  }

  private void _ensureDirectoryExisting (@Nonnull final String sPath)
  {
    final File aFile = new File (sPath);
    if (!aFile.exists ())
    {
      FileOperations.createDirRecursive (aFile);
      s_aLogger.info ("Created special PEPPOL AS2 folder '" + sPath + "'");
    }
  }

  private void _checkSettings ()
  {
    _ensureDirectoryExisting (AppSettings.getFolderForSending ());
    _ensureDirectoryExisting (AppSettings.getFolderForSendingErrors ());
    _ensureDirectoryExisting (AppSettings.getFolderForReceiving ());
    _ensureDirectoryExisting (AppSettings.getFolderForReceivingErrors ());
  }

  @Override
  @Nonnull
  @Nonempty
  protected ICommonsMap <String, IApplicationInitializer <LayoutExecutionContext>> getAllInitializers ()
  {
    final ICommonsMap <String, IApplicationInitializer <LayoutExecutionContext>> ret = new CommonsHashMap <> ();
    ret.put (CApplication.APP_ID_SECURE, new PEPPOLAS2Initializer ());
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
    PhotonPathMapper.setApplicationServletPathMapping (CApplication.APP_ID_SECURE,
                                                       AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH);
    PhotonPathMapper.setAjaxServletPathMapping (CApplication.APP_ID_SECURE,
                                                SecureApplicationAjaxServlet.SERVLET_DEFAULT_PATH);
    PhotonPathMapper.setAPIServletPathMapping (CApplication.APP_ID_SECURE,
                                               SecureApplicationAPIServlet.SERVLET_DEFAULT_PATH);
    PhotonPathMapper.setDefaultApplicationID (CApplication.APP_ID_SECURE);
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
