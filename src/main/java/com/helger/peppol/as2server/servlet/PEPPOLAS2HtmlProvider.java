package com.helger.peppol.as2server.servlet;

import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.html.sections.HCH1;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.photon.core.app.context.ISimpleWebExecutionContext;
import com.helger.photon.core.app.html.AbstractHTMLProvider;
import com.helger.photon.core.app.redirect.ForcedRedirectException;

public final class PEPPOLAS2HtmlProvider extends AbstractHTMLProvider
{
  @Override
  protected void fillBody (final ISimpleWebExecutionContext aSWEC, final HCHtml aHtml) throws ForcedRedirectException
  {
    final HCBody aBody = aHtml.getBody ();
    aBody.addChild (new HCH1 ().addChild ("as2-peppol-server"));
    aBody.addChild (new HCP ().addChild ("This site has no further user interface."));
    aBody.addChild (new HCP ().addChild ("The AS2 endpoint is located at ")
                              .addChild (new HCA (new SimpleURL (aSWEC.getRequestScope ().getContextPath () +
                                                                 "/as2")).addChild (new HCCode ().addChild ("/as2"))));
  }
}
