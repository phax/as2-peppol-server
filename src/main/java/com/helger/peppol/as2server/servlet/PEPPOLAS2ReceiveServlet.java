/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import com.helger.as2servlet.AS2ReceiveServlet;
import com.helger.commons.http.EHttpMethod;
import com.helger.xservlet.AbstractXServlet;

/**
 * Special version of the {@link AS2ReceiveServlet} customizing the path to the
 * AS2 configuration file. The absolute path to the configuration file can be
 * provided using the "peppol.ap.server.config.path" or the
 * "ap.server.config.path" system property.
 *
 * @author Philip Helger
 */
public final class PEPPOLAS2ReceiveServlet extends AbstractXServlet
{
  public static final String SERVLET_DEFAULT_NAME = "as2";
  public static final String SERVLET_DEFAULT_PATH = '/' + SERVLET_DEFAULT_NAME;

  public PEPPOLAS2ReceiveServlet ()
  {
    handlerRegistry ().registerHandler (EHttpMethod.POST, new PEPPOLAS2ReceiveXServletHandler (), false);
  }
}
