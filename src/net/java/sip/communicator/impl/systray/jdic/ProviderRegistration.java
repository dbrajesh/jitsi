/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package net.java.sip.communicator.impl.systray.jdic;

import net.java.sip.communicator.service.gui.*;
import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.util.*;

/**
 * The <tt>ProviderRegistration</tt> is used by the systray plugin
 * to make the registration to a protocol provider. This operation
 * is implemented within a thread, so that sip-communicator can
 * continue its execution during this operation.
 *
 * @author Nicolas Chamouard
 */

public class ProviderRegistration
    extends Thread
    implements SecurityAuthority
{
    /**
     * The protocol provider to whom we want to register
     */
    private ProtocolProviderService protocolProvider;

    private UIService uiService;

    /**
     * The logger for this class.
     */
    private Logger logger = Logger.getLogger(ProviderRegistration.class.getName());

    /**
     * Creates an instance of <tt>ProviderRegistration</tt>.
     *
     * @param uiService a reference to the currently valid ui service instance.
     * @param protocolProvider the provider we want to register
     */
    public ProviderRegistration(UIService uiService,
        ProtocolProviderService protocolProvider)
    {
        this.protocolProvider = protocolProvider;
        this.uiService = uiService;
    }

    /**
     * Start the thread which will register to the provider
     */
    public void run()
    {
        try {
            protocolProvider.register(this);
        }
        catch (OperationFailedException ex)
        {
            int errorCode = ex.getErrorCode();
            if (errorCode == OperationFailedException.GENERAL_ERROR)
            {
                logger.error("Provider could not be registered"
                    + " due to the following general error: ", ex);
            }
            else if (errorCode == OperationFailedException.INTERNAL_ERROR)
            {
                logger.error("Provider could not be registered"
                    + " due to the following internal error: ", ex);
            }
            else if (errorCode == OperationFailedException.NETWORK_FAILURE)
            {
                logger.error("Provider could not be registered"
                        + " due to a network failure: " + ex);
            }
            else if (errorCode == OperationFailedException
                    .INVALID_ACCOUNT_PROPERTIES)
            {
                logger.error("Provider could not be registered"
                    + " due to an invalid account property: ", ex);
            }
            else
            {
                logger.error("Provider could not be registered.", ex);
            }
        }
    }

    /**
     * Used to login to the protocol providers
     *
     * @param realm the realm that the credentials are needed for
     * @param userCredentials the values to propose the user by default
     *
     * @return The Credentials associated with the speciefied realm
     */
    public UserCredentials obtainCredentials(String realm,
            UserCredentials userCredentials)
    {
        ExportedWindow loginWindow = uiService.getAuthenticationWindow(
            protocolProvider, realm, userCredentials);

        loginWindow.setVisible(true);

        return userCredentials;
    }


 }
