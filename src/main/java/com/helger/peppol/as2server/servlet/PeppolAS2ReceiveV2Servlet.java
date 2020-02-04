/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import com.helger.commons.http.EHttpMethod;
import com.helger.peppol.as2servlet.EPeppolAS2Version;
import com.helger.xservlet.AbstractXServlet;

/**
 * This servlet is used to accept PEPPOL AS2 v2 messages (for the transport
 * profile <code>busdox-transport-as2-ver2p0</code>)
 *
 * @author Philip Helger
 */
public final class PeppolAS2ReceiveV2Servlet extends AbstractXServlet
{
  public static final String SERVLET_DEFAULT_NAME = "as2v2";
  public static final String SERVLET_DEFAULT_PATH = '/' + SERVLET_DEFAULT_NAME;

  public PeppolAS2ReceiveV2Servlet ()
  {
    handlerRegistry ().registerHandler (EHttpMethod.POST,
                                        new PeppolAS2ReceiveXServletHandler (EPeppolAS2Version.V2),
                                        false);
  }
}
