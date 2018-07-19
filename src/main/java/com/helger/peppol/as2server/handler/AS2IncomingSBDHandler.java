/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import java.io.File;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unece.cefact.namespaces.sbdh.StandardBusinessDocument;
import org.w3c.dom.Element;

import com.helger.as2lib.exception.OpenAS2Exception;
import com.helger.collection.pair.IPair;
import com.helger.collection.pair.Pair;
import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.id.factory.GlobalIDFactory;
import com.helger.commons.io.EAppend;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.log.InMemoryLogger;
import com.helger.jaxb.validation.CollectingValidationEventHandler;
import com.helger.peppol.as2server.app.AppSettings;
import com.helger.peppol.as2servlet.IAS2IncomingSBDHandlerSPI;
import com.helger.peppol.sbdh.PeppolSBDHDocument;
import com.helger.peppol.sbdh.read.PeppolSBDHDocumentReadException;
import com.helger.peppol.sbdh.read.PeppolSBDHDocumentReader;
import com.helger.sbdh.builder.SBDHWriter;
import com.helger.ubl21.EUBL21DocumentType;
import com.helger.ubl21.UBL21DocumentTypes;
import com.helger.ubl21.UBL21ReaderBuilder;
import com.helger.ubl21.UBL21WriterBuilder;
import com.helger.xml.serialize.write.XMLWriter;

@IsSPIImplementation
public class AS2IncomingSBDHandler implements IAS2IncomingSBDHandlerSPI
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AS2IncomingSBDHandler.class);

  /**
   * Interpret the payload of the provided SBD as a UBL document and return the
   * parsed domain object. This method is mainly provided as a "proof of concept
   * implementation" to show how to extract data in a reasonable way.
   *
   * @param aStandardBusinessDocument
   *        The source SBD. May not be <code>null</code>.
   * @return Never <code>null</code>. A pair of document type and parsed domain
   *         object (e.g.
   *         {@link oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType}
   *         or
   *         {@link oasis.names.specification.ubl.schema.xsd.order_21.OrderType}
   *         etc. - depending on the type).
   * @throws PeppolSBDHDocumentReadException
   *         If the SBD does not comply to the PEPPOL rules.
   * @throws OpenAS2Exception
   *         In case the payload is not a valid UBL.
   */
  @Nonnull
  public static IPair <EUBL21DocumentType, Object> extractUBLDocument (@Nonnull final StandardBusinessDocument aStandardBusinessDocument) throws PeppolSBDHDocumentReadException,
                                                                                                                                          OpenAS2Exception
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
      final Object aUBLDocument = UBL21ReaderBuilder.createGeneric (eDocType)
                                                    .setValidationEventHandler (aEventHdl)
                                                    .read (aElement);
      if (aUBLDocument == null)
      {
        aErrors.error ("Failed to read the UBL document as " +
                       eDocType.name () +
                       ":\n" +
                       aEventHdl.getErrorList ().getAllErrors ().toString ());
      }
      else
      {
        // Handle UBL document
        LOGGER.info ("Successfully read UBL 2.1 " + eDocType.name () + " document. Continue from here!");
        return Pair.create (eDocType, aUBLDocument);
      }
    }

    // Save SBDH to error folder
    final File aFile = new File (AppSettings.getFolderForReceivingErrors (),
                                 GlobalIDFactory.getNewPersistentStringID () + ".xml");
    if (true)
      SBDHWriter.standardBusinessDocument ().write (aStandardBusinessDocument, aFile);
    else
      XMLWriter.writeToStream (aElement, FileHelper.getOutputStream (aFile, EAppend.TRUNCATE));
    LOGGER.error ("Wrote received erroneous SBDH to " + aFile.getAbsolutePath ());

    throw new OpenAS2Exception ("Invalid UBL 2.1 document provided:\n" + aErrors.getAllMessages ());
  }

  public void handleIncomingSBD (@Nonnull final StandardBusinessDocument aStandardBusinessDocument) throws Exception
  {
    // Grab data and parse (for XSD validation)
    // Throws Exception on error
    final IPair <EUBL21DocumentType, Object> aPair = extractUBLDocument (aStandardBusinessDocument);

    // Write UBL to receiving folder
    final File aFile = new File (AppSettings.getFolderForReceiving (),
                                 GlobalIDFactory.getNewPersistentStringID () + ".xml");
    if (true)
      SBDHWriter.standardBusinessDocument ().write (aStandardBusinessDocument, aFile);
    else
      new UBL21WriterBuilder <> (aPair.getFirst ()).write (aPair.getSecond (), aFile);

    LOGGER.error ("Wrote received good SBDH to " + aFile.getAbsolutePath ());
  }
}
