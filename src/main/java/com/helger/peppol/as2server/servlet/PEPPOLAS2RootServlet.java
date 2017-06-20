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

import javax.annotation.Nonnull;

import com.helger.commons.string.StringHelper;
import com.helger.photon.core.servlet.AbstractSecureApplicationServlet;
import com.helger.photon.core.servlet.AbstractUnifiedResponseServlet;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public class PEPPOLAS2RootServlet extends AbstractUnifiedResponseServlet
{
  @Override
  protected void handleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                                @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    String sRedirectURL = aRequestScope.getContextPath () + AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH;
    final String sQueryString = aRequestScope.getQueryString ();
    if (StringHelper.hasText (sQueryString))
      sRedirectURL += "?" + sQueryString;
    aUnifiedResponse.setRedirect (sRedirectURL);
  }
}
