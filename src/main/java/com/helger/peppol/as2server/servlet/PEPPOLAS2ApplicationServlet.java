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

import com.helger.photon.core.app.html.IHTMLProvider;
import com.helger.photon.core.servlet.AbstractSecureApplicationServlet;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public final class PEPPOLAS2ApplicationServlet extends AbstractSecureApplicationServlet
{
  @Override
  protected void onRequestBegin (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope)
  {
    super.onRequestBegin (aRequestScope);
  }

  @Override
  @Nonnull
  protected IHTMLProvider createHTMLProvider (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope)
  {
    return new PEPPOLAS2HtmlProvider ();
  }

  @Override
  protected void onRequestEnd (final boolean bExceptionOccurred)
  {
    super.onRequestEnd (bExceptionOccurred);
  }
}
