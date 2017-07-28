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