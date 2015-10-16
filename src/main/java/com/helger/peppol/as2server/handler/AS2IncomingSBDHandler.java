/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.peppol.as2server.handler;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unece.cefact.namespaces.sbdh.StandardBusinessDocument;
import org.w3c.dom.Element;

import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.as2servlet.sbd.IAS2IncomingSBDHandlerSPI;
import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.log.InMemoryLogger;
import com.helger.jaxb.validation.CollectingValidationEventHandler;
import com.helger.peppol.sbdh.PeppolSBDHDocument;
import com.helger.peppol.sbdh.read.PeppolSBDHDocumentReader;
import com.helger.ubl21.EUBL21DocumentType;
import com.helger.ubl21.UBL21DocumentTypes;
import com.helger.ubl21.UBL21Marshaller;

@IsSPIImplementation
public class AS2IncomingSBDHandler implements IAS2IncomingSBDHandlerSPI
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (AS2IncomingSBDHandler.class);

  public void handleIncomingSBD (@Nonnull final StandardBusinessDocument aStandardBusinessDocument) throws Exception
  {
    final InMemoryLogger aErrors = new InMemoryLogger ();

    final PeppolSBDHDocument aDocumentData = new PeppolSBDHDocumentReader ().extractData (aStandardBusinessDocument);
    final Element aElement = aDocumentData.getBusinessMessage ();

    // Try to determine the UBL document type from the namespace URI
    final EUBL21DocumentType eDocType = UBL21DocumentTypes.getDocumentTypeOfNamespace (aElement.getNamespaceURI ());
    if (eDocType == null)
    {
      aErrors.error ("The contained document is not a supported UBL 2.1 document!");
    }
    else
    {
      // Try to read the UBL document - performs implicit XSD validation
      final CollectingValidationEventHandler aEventHdl = new CollectingValidationEventHandler ();
      final Object aUBLDocument = UBL21Marshaller.readUBLDocument (aElement,
                                                                   (ClassLoader) null,
                                                                   eDocType.getImplementationClass (),
                                                                   aEventHdl);
      if (aUBLDocument == null)
      {
        aErrors.error ("Failed to read the UBL document as " +
                       eDocType.name () +
                       ":\n" +
                       aEventHdl.getResourceErrors ().getAllResourceErrors ().toString ());
      }
      else
      {
        // Handle UBL document
        s_aLogger.info ("Successfully read UBL 2.1 " + eDocType.name () + " document. Continue from here!");
        // TODO - main handling comes here
      }
    }

    if (!aErrors.isEmpty ())
      throw new OpenAS2Exception ("Invalid UBL 2.1 document provided:\n" + aErrors.getAllMessages ());
  }
}
