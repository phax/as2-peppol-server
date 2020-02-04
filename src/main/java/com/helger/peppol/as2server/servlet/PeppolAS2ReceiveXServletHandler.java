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

import javax.annotation.Nonnull;
import javax.servlet.ServletException;

import com.helger.as2lib.cert.CertificateFactory;
import com.helger.as2lib.exception.AS2Exception;
import com.helger.as2lib.message.AS2Message;
import com.helger.as2lib.partner.xml.XMLPartnershipFactory;
import com.helger.as2lib.processor.DefaultMessageProcessor;
import com.helger.as2lib.processor.receiver.AbstractActiveNetModule;
import com.helger.as2lib.processor.storage.AbstractStorageModule;
import com.helger.as2lib.processor.storage.MDNFileModule;
import com.helger.as2lib.processor.storage.MessageFileModule;
import com.helger.as2lib.session.AS2Session;
import com.helger.as2servlet.AbstractAS2ReceiveXServletHandler;
import com.helger.as2servlet.util.AS2ServletPartnershipFactory;
import com.helger.as2servlet.util.AS2ServletReceiverModule;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.attr.StringMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.relative.FileRelativeIO;
import com.helger.commons.io.relative.IFileRelativeIO;
import com.helger.peppol.as2server.app.AppSettings;
import com.helger.peppol.as2servlet.AS2ServletSBDModule;
import com.helger.peppol.as2servlet.EPeppolAS2Version;
import com.helger.photon.app.io.WebFileIO;

public class PeppolAS2ReceiveXServletHandler extends AbstractAS2ReceiveXServletHandler
{
  private final EPeppolAS2Version m_eAS2Version;

  public PeppolAS2ReceiveXServletHandler (@Nonnull final EPeppolAS2Version eAS2Version)
  {
    ValueEnforcer.notNull (eAS2Version, "AS2Version");
    m_eAS2Version = eAS2Version;
  }

  @Nonnull
  public final EPeppolAS2Version getAS2Version ()
  {
    return m_eAS2Version;
  }

  @Override
  protected AS2Session createAS2Session (@Nonnull final ICommonsMap <String, String> aInitParams) throws AS2Exception,
                                                                                                  ServletException
  {
    final AS2Session ret = new AS2Session ();
    {
      final CertificateFactory aCF = new CertificateFactory ();
      aCF.initDynamicComponent (ret,
                                new StringMap ().add (CertificateFactory.ATTR_TYPE,
                                                      AppSettings.getKeyStoreType ().getID ())
                                                .add (CertificateFactory.ATTR_FILENAME, AppSettings.getKeyStorePath ())
                                                .add (CertificateFactory.ATTR_PASSWORD,
                                                      AppSettings.getKeyStorePassword ())
                                                .add (CertificateFactory.ATTR_SAVE_CHANGES_TO_FILE, true));
      ret.setCertificateFactory (aCF);
    }

    {
      final AS2ServletPartnershipFactory aPF = new AS2ServletPartnershipFactory ();
      aPF.initDynamicComponent (ret,
                                new StringMap ().add (XMLPartnershipFactory.ATTR_FILENAME,
                                                      WebFileIO.getDataIO ()
                                                               .getFile ("as2-server-partnerships.xml")
                                                               .getAbsolutePath ())
                                                .add (XMLPartnershipFactory.ATTR_DISABLE_BACKUP, true));
      ret.setPartnershipFactory (aPF);
    }

    {
      // Processing queue
      final DefaultMessageProcessor aMP = new DefaultMessageProcessor ();
      aMP.initDynamicComponent (ret, null);
      ret.setMessageProcessor (aMP);

      // All processing relative to receiving folder
      final IFileRelativeIO aReceiveIO = new FileRelativeIO (AppSettings.getFolderForReceiving ());

      {
        // Store sent MDNs to a file
        final MDNFileModule aMod = new MDNFileModule ();
        aMod.initDynamicComponent (ret,
                                   new StringMap ().add (AbstractStorageModule.ATTR_FILENAME,
                                                         aReceiveIO.getFile ("as2-mdn/" +
                                                                             "$date.uuuu$/" +
                                                                             "$date.MM$/" +
                                                                             "$mdn.msg.sender.as2_id$-$mdn.msg.receiver.as2_id$-$mdn.msg.headers.message-id$")
                                                                   .getAbsolutePath ())
                                                   .add (AbstractStorageModule.ATTR_PROTOCOL, AS2Message.PROTOCOL_AS2)
                                                   .add (AbstractStorageModule.ATTR_TEMPDIR,
                                                         aReceiveIO.getFile ("as2-temp").getAbsolutePath ()));
        aMP.addModule (aMod);
      }

      {
        // Store received messages and headers to a file
        final MessageFileModule aMod = new MessageFileModule ();
        aMod.initDynamicComponent (ret,
                                   new StringMap ().add (AbstractStorageModule.ATTR_FILENAME,
                                                         aReceiveIO.getFile ("as2-inbox/" +
                                                                             "$date.uuuu$/" +
                                                                             "$date.MM$/" +
                                                                             "$msg.sender.as2_id$-$msg.receiver.as2_id$-$msg.headers.message-id$")
                                                                   .getAbsolutePath ())
                                                   .add (MessageFileModule.ATTR_HEADER,
                                                         aReceiveIO.getFile ("as2-inbox-headers/" +
                                                                             "$date.uuuu$/" +
                                                                             "$date.MM$/" +
                                                                             "$msg.sender.as2_id$-$msg.receiver.as2_id$-$msg.headers.message-id$")
                                                                   .getAbsolutePath ())
                                                   .add (AbstractStorageModule.ATTR_PROTOCOL, AS2Message.PROTOCOL_AS2)
                                                   .add (AbstractStorageModule.ATTR_TEMPDIR,
                                                         aReceiveIO.getFile ("as2-temp").getAbsolutePath ()));
        aMP.addModule (aMod);
      }

      {
        /**
         * The main receiver module that performs the message parsing. This
         * module also sends synchronous MDNs back. Note: the port attribute is
         * required but can be ignored in our case!
         */
        final AS2ServletReceiverModule aMod = new AS2ServletReceiverModule ();
        aMod.initDynamicComponent (ret,
                                   new StringMap ().add (AbstractActiveNetModule.ATTR_PORT, 10080)
                                                   .add (AbstractActiveNetModule.ATTR_ERROR_DIRECTORY,
                                                         aReceiveIO.getFile ("as2-inbox-error/" +
                                                                             "$date.uuuu$/" +
                                                                             "$date.MM$")
                                                                   .getAbsolutePath ())
                                                   .add (AbstractActiveNetModule.ATTR_ERROR_FORMAT,
                                                         "$msg.sender.as2_id$-$msg.receiver.as2_id$-$msg.headers.message-id$")
                                                   .add (AbstractActiveNetModule.ATTR_ERROR_STORE_BODY, true));
        aMP.addModule (aMod);
      }

      {
        // Process incoming SBD documents
        final AS2ServletSBDModule aMod = new AS2ServletSBDModule (m_eAS2Version);
        aMod.initDynamicComponent (ret, null);
        aMP.addModule (aMod);
      }
    }
    return ret;
  }
}
