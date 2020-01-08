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
package com.helger.peppol.as2server.app;

import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.exception.InitializationException;
import com.helger.peppol.utils.PeppolKeyStoreHelper;
import com.helger.scope.singleton.AbstractGlobalSingleton;
import com.helger.security.keystore.EKeyStoreLoadError;
import com.helger.security.keystore.KeyStoreHelper;
import com.helger.security.keystore.LoadedKey;
import com.helger.security.keystore.LoadedKeyStore;

/**
 * This class holds the private key for signing and the certificate for
 * checking.
 *
 * @author Philip Helger
 */
public final class APKeyManager extends AbstractGlobalSingleton
{
  private static final Logger LOGGER = LoggerFactory.getLogger (APKeyManager.class);

  private static final AtomicBoolean s_aCertificateValid = new AtomicBoolean (false);
  private static EKeyStoreLoadError s_eInitError;
  private static String s_sInitError;

  private KeyStore m_aKeyStore;
  private KeyStore.PrivateKeyEntry m_aKeyEntry;

  private void _loadCertificates () throws InitializationException
  {
    // Reset every time
    s_aCertificateValid.set (false);
    s_sInitError = null;
    m_aKeyStore = null;
    m_aKeyEntry = null;

    // Load the key store and get the signing key
    final LoadedKeyStore aLoadedKeyStore = KeyStoreHelper.loadKeyStore (AppSettings.getKeyStoreType (),
                                                                        AppSettings.getKeyStorePath (),
                                                                        AppSettings.getKeyStorePassword ());
    if (aLoadedKeyStore.isFailure ())
    {
      s_eInitError = aLoadedKeyStore.getError ();
      s_sInitError = PeppolKeyStoreHelper.getLoadError (aLoadedKeyStore);
      throw new InitializationException (s_sInitError);
    }
    m_aKeyStore = aLoadedKeyStore.getKeyStore ();

    final LoadedKey <KeyStore.PrivateKeyEntry> aLoadedKey = KeyStoreHelper.loadPrivateKey (m_aKeyStore,
                                                                                           AppSettings.getKeyStorePath (),
                                                                                           AppSettings.getKeyStoreKeyAlias (),
                                                                                           AppSettings.getKeyStoreKeyPassword ());
    if (aLoadedKey.isFailure ())
    {
      s_sInitError = PeppolKeyStoreHelper.getLoadError (aLoadedKey);
      throw new InitializationException (s_sInitError);
    }

    m_aKeyEntry = aLoadedKey.getKeyEntry ();
    LOGGER.info ("APKeyManager successfully initialized with keystore '" +
                    AppSettings.getKeyStorePath () +
                    "' and alias '" +
                    AppSettings.getKeyStoreKeyAlias () +
                    "'");
    s_aCertificateValid.set (true);
  }

  @Deprecated
  @UsedViaReflection
  public APKeyManager () throws InitializationException
  {
    _loadCertificates ();
  }

  @Nonnull
  public static APKeyManager getInstance ()
  {
    return getGlobalSingleton (APKeyManager.class);
  }

  /**
   * @return The configured keystore. May be <code>null</code> if loading
   *         failed. In that case check the result of
   *         {@link #getInitializationError()}.
   */
  @Nullable
  public KeyStore getKeyStore ()
  {
    return m_aKeyStore;
  }

  /**
   * @return The configured private key. May be <code>null</code> if loading
   *         failed. In that case check the result of
   *         {@link #getInitializationError()}.
   */
  @Nullable
  public KeyStore.PrivateKeyEntry getPrivateKeyEntry ()
  {
    return m_aKeyEntry;
  }

  /**
   * @return A shortcut method to determine if the certification configuration
   *         is valid or not. This method can be used, even if
   *         {@link #getInstance()} throws an exception.
   */
  public static boolean isCertificateValid ()
  {
    return s_aCertificateValid.get ();
  }

  /**
   * If the certificate is not valid according to {@link #isCertificateValid()}
   * this method can be used to determine the error detail code.
   *
   * @return <code>null</code> if initialization was successful.
   */
  @Nullable
  public static EKeyStoreLoadError getInitializationErrorCode ()
  {
    return s_eInitError;
  }

  /**
   * If the certificate is not valid according to {@link #isCertificateValid()}
   * this method can be used to determine the error detail message.
   *
   * @return <code>null</code> if initialization was successful.
   */
  @Nullable
  public static String getInitializationError ()
  {
    return s_sInitError;
  }

  public static void reloadFromConfiguration ()
  {
    try
    {
      final APKeyManager aInstance = getGlobalSingletonIfInstantiated (APKeyManager.class);
      if (aInstance != null)
        aInstance._loadCertificates ();
      else
        getInstance ();
    }
    catch (final Exception ex)
    {}
  }
}
