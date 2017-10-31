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
package com.helger.peppol.as2server.ui;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.metadata.HCStyle;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.html.sections.HCH1;
import com.helger.html.hc.html.sections.HCH2;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.peppol.as2server.app.AppSettings;
import com.helger.peppol.as2server.app.WebAppSettings;
import com.helger.peppol.as2server.servlet.PEPPOLAS2ReceiveServlet;
import com.helger.photon.core.app.html.AbstractHTMLProvider;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public final class PEPPOLAS2HtmlProvider extends AbstractHTMLProvider
{
  @Override
  protected void fillHeadAndBody (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                                  @Nonnull final HCHtml aHtml,
                                  @Nonnull final Locale aDisplayLocale)
  {
    // Add all meta elements
    addMetaElements (aRequestScope, aHtml.head ());
    aHtml.head ()
         .addCSS (new HCStyle ("* { font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\";}"));

    // Fill the body
    final HCBody aBody = aHtml.body ();
    aBody.addChild (new HCH1 ().addChild ("as2-peppol-server"));
    if (WebAppSettings.isTestVersion ())
      aBody.addChild (new HCH2 ().addChild ("TEST version!"));
    aBody.addChild (new HCP ().addChild ("This site has no further user interface."));
    aBody.addChild (new HCP ().addChild ("The AS2 endpoint is located at ")
                              .addChild (new HCA (new SimpleURL (aRequestScope.getContextPath () +
                                                                 PEPPOLAS2ReceiveServlet.SERVLET_DEFAULT_PATH)).addChild (PEPPOLAS2ReceiveServlet.SERVLET_DEFAULT_PATH))
                              .addChild (" and it can only be accessed via HTTP POST."));

    if (WebAppSettings.isTestVersion ())
    {
      final HCUL aUL = new HCUL ();
      aUL.addItem ()
         .addChild ("Incoming AS2 files reside at: ")
         .addChild (new HCCode ().addChild (AppSettings.getFolderForReceiving ().getAbsolutePath ()));
      aUL.addItem ()
         .addChild ("Outgoing AS2 files sent from: ")
         .addChild (new HCCode ().addChild (AppSettings.getFolderForSending ().getAbsolutePath ()));
      aBody.addChild (aUL);
    }

    aBody.addChild (new HCP ().addChild ("Open Source Software by ")
                              .addChild (new HCA ().setHref (new SimpleURL ("https://twitter.com/philiphelger"))
                                                   .setTargetBlank ()
                                                   .addChild ("@PhilipHelger"))
                              .addChild (" - ")
                              .addChild (new HCA ().setHref (new SimpleURL ("https://github.com/phax/as2-peppol-server"))
                                                   .setTargetBlank ()
                                                   .addChild ("GitHub project")));
  }
}
