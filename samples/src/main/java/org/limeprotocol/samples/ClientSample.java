package org.limeprotocol.samples;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.client.ClientChannelImpl;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.messaging.resource.Presence;
import org.limeprotocol.messaging.resource.Receipt;
import org.limeprotocol.messaging.resource.UriTemplates;
import org.limeprotocol.network.*;
import org.limeprotocol.network.tcp.CustomTrustManager;
import org.limeprotocol.network.tcp.SocketTcpClientFactory;
import org.limeprotocol.network.tcp.TcpTransport;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;

public class ClientSample {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        Scanner inScanner = new Scanner(in);
        out.print("Host name (ENTER for default): ");
        String hostName = inScanner.nextLine();
        if (hostName == null || hostName.isEmpty()) {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        
        out.print("Port number (ENTER for default): ");
        int portNumber;
        try {
            portNumber = Integer.parseInt(inScanner.nextLine());
        } catch (NumberFormatException e) {
            portNumber = 55321;
        }

        // Creates a new transport and connect to the server
        URI serverUri = new URI(String.format("net.tcp://%s:%d", hostName, portNumber));
        TcpTransport transport = new TcpTransport(
                new JacksonEnvelopeSerializer(),
                new SocketTcpClientFactory(new CustomTrustManager(null)),
                new TraceWriter() {
                    @Override
                    public void trace(String data, DataOperation operation) {
                        System.out.printf("%s: %s", operation.toString(), data);
                        System.out.println();
                    }

                    @Override
                    public boolean isEnabled() {
                        return true;
                    }
                });
        transport.open(serverUri);

        // Creates a new client channel
        ClientChannel clientChannel = new ClientChannelImpl(transport, true, true, true);
        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();

        clientChannel.establishSession(
                SessionCompression.NONE,
                SessionEncryption.TLS,
                new Identity("samples", "take.io"),
                new PlainAuthentication() {{
                    setToBase64Password("take1234");
                }},
                "default",
                new ClientChannel.EstablishSessionListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        out.printf("Session establishment failed: ");
                        exception.printStackTrace();
                        semaphore.release();
                    }

                    @Override
                    public void onReceiveSession(Session session) {
                        out.printf(String.format("Session with id '%s' received: State: %s - Reason: %s", session.getId(), session.getState(), session.getReason()));
                        out.println();
                        semaphore.release();
                    }
                }
        );

        if (semaphore.tryAcquire(1, 60, TimeUnit.SECONDS)) {
            if (clientChannel.getState() == Session.SessionState.ESTABLISHED) {
                System.out.printf("Session established - Id: %s - Remote node: %s - Local node: %s", clientChannel.getSessionId(), clientChannel.getRemoteNode(), clientChannel.getLocalNode());
                System.out.println();
                clientChannel.addMessageListener(new MessageChannel.MessageChannelListener() {
                    @Override
                    public void onReceiveMessage(Message message) {
                        out.printf(String.format("Message with id '%s' received from '%s': %s", message.getId(), message.getFrom(), message.getContent()));
                        out.println();
                    }
                }, false);
                clientChannel.addCommandListener(new CommandChannel.CommandChannelListener() {
                    @Override
                    public void onReceiveCommand(Command command) {
                        out.printf(String.format("Command with id '%s' received from '%s':  Method: %s - URI: %s - Status: %s", command.getId(), command.getFrom(), command.getMethod(), command.getUri(), command.getStatus()));
                        out.println();
                    }
                }, false);
                clientChannel.addNotificationListener(new NotificationChannel.NotificationChannelListener() {
                    @Override
                    public void onReceiveNotification(Notification notification) {
                        out.printf(String.format("Notification with id '%s' received from '%s': Event: %s", notification.getId(), notification.getFrom(), notification.getEvent()));
                        out.println();
                    }
                }, false);
                
                clientChannel.enqueueSessionListener(new SessionChannel.SessionChannelListener() {
                    @Override
                    public void onReceiveSession(Session session) {
                        out.printf(String.format("Session with id '%s' received: State: %s - Reason: %s", session.getId(), session.getState(), session.getReason()));
                        out.println();
                        semaphore.release();
                    }
                });

                // Sets the presence
                final Presence presence = new Presence() {{
                    setStatus(Presence.PresenceStatus.AVAILABLE);
                }};
                Command presenceCommand = new Command(UUID.randomUUID()) {{
                    setMethod(Command.CommandMethod.SET);
                    setResource(presence);
                    setUri(new LimeUri(UriTemplates.PRESENCE));
                }};
                clientChannel.sendCommand(presenceCommand);
                
                // Sets the receipts
                final Receipt receipt = new Receipt() {{
                    setEvents(new Notification.Event[] {
                        Notification.Event.DISPATCHED , Notification.Event.RECEIVED });
                }};
                Command receiptCommand = new Command(UUID.randomUUID()) {{
                    setMethod(Command.CommandMethod.SET);
                    setResource(receipt);
                    setUri(new LimeUri(UriTemplates.RECEIPT));
                }};
                clientChannel.sendCommand(receiptCommand);

                while (clientChannel.getState() == Session.SessionState.ESTABLISHED) {
                    out.print("Destination node (Type EXIT to quit): ");
                    String toInput = inScanner.nextLine();
                    if (toInput != null &&
                            toInput.equalsIgnoreCase("exit")) {
                        out.println("Finishing the session...");
                        clientChannel.sendFinishingSession();
                        break;
                    }

                    if (toInput != null && !toInput.isEmpty()) {
                        final Node node = Node.parse(toInput);
                        out.print("Message text: ");
                        final PlainText plainText = new PlainText(inScanner.nextLine());
                        Message message = new Message(UUID.randomUUID()) {{
                            setTo(node);
                            setContent(plainText);
                        }};

                        clientChannel.sendMessage(message);
                    }
                }

                if (semaphore.tryAcquire(1, 5000, TimeUnit.MILLISECONDS)) {
                    out.println("Session finished");
                } else {
                    out.println("Timeout exceeded");
                }
            }
        } else {
            out.println("Timeout exceeded");
        }
        
        exit(0);
    }
}
