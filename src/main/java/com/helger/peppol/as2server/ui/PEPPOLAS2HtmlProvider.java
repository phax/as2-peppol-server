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

import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.html.sections.HCH1;
import com.helger.html.hc.html.sections.HCH2;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.peppol.as2server.app.WebAppSettings;
import com.helger.peppol.as2server.servlet.PEPPOLAS2ReceiveServlet;
import com.helger.photon.core.app.context.ISimpleWebExecutionContext;
import com.helger.photon.core.app.html.AbstractHTMLProvider;
import com.helger.photon.xservlet.forcedredirect.ForcedRedirectException;

public final class PEPPOLAS2HtmlProvider extends AbstractHTMLProvider
{
  @Override
  protected void fillBody (final ISimpleWebExecutionContext aSWEC, final HCHtml aHtml) throws ForcedRedirectException
  {
    final HCBody aBody = aHtml.getBody ();
    aBody.addChild (new HCH1 ().addChild ("as2-peppol-server"));
    if (WebAppSettings.isTestVersion ())
      aBody.addChild (new HCH2 ().addChild ("TEST version"));
    aBody.addChild (new HCP ().addChild ("This site has no further user interface."));
    aBody.addChild (new HCP ().addChild ("The AS2 endpoint is located at ")
                              .addChild (new HCA (new SimpleURL (aSWEC.getRequestScope ().getContextPath () +
                                                                 PEPPOLAS2ReceiveServlet.SERVLET_DEFAULT_PATH)).addChild (new HCCode ().addChild (PEPPOLAS2ReceiveServlet.SERVLET_DEFAULT_PATH))));
  }
}
