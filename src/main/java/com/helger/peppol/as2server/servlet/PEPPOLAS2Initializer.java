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

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.locale.LocaleCache;
import com.helger.photon.basic.app.locale.ILocaleManager;
import com.helger.photon.basic.app.menu.IMenuTree;
import com.helger.photon.core.ajax.IAjaxInvoker;
import com.helger.photon.core.api.IAPIInvoker;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.core.app.init.IApplicationInitializer;
import com.helger.photon.core.app.layout.ILayoutManager;

public final class PEPPOLAS2Initializer implements IApplicationInitializer <LayoutExecutionContext>
{
  public static final Locale LOCALE_EN_GB = LocaleCache.getInstance ().getLocale ("en", "GB");

  @Override
  public void initLocales (@Nonnull final ILocaleManager aLocaleMgr)
  {
    aLocaleMgr.registerLocale (LOCALE_EN_GB);
    aLocaleMgr.setDefaultLocale (LOCALE_EN_GB);
  }

  @Override
  public void initLayout (@Nonnull final ILayoutManager <LayoutExecutionContext> aLayoutMgr)
  {}

  @Override
  public void initMenu (@Nonnull final IMenuTree aMenuTree)
  {}

  @Override
  public void initAjax (@Nonnull final IAjaxInvoker aAjaxInvoker)
  {}

  @Override
  public void initAPI (@Nonnull final IAPIInvoker aAPIInvoker)
  {}

  @Override
  public void initRest ()
  {}
}
