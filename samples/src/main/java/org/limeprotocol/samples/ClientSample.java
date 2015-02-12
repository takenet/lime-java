package org.limeprotocol.samples;

import org.limeprotocol.*;
import org.limeprotocol.client.ClientChannel;
import org.limeprotocol.client.ClientChannelImpl;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.network.*;
import org.limeprotocol.network.tcp.SocketTcpClientFactory;
import org.limeprotocol.network.tcp.TcpTransport;
import org.limeprotocol.security.PlainAuthentication;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
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
                new SocketTcpClientFactory(),
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
        ClientChannel clientChannel = new ClientChannelImpl(transport);

        final Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        final Session[] receivedSession = {null};
        SessionChannel.SessionChannelListener sessionChannelListener = new SessionChannel.SessionChannelListener() {
            @Override
            public void onReceiveSession(Session session) {
                
                out.printf(String.format("Session with id '%s' received: State: %s - Reason: %s", session.getId(), session.getState(), session.getReason()));
                out.println();
                receivedSession[0] = session;
                semaphore.release();
            }
        };

        clientChannel.startNewSession(sessionChannelListener);

        if (semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS) &&
                receivedSession[0] != null) {
            if (receivedSession[0].getState() == Session.SessionState.NEGOTIATING) {
                receivedSession[0] = null;
                clientChannel.negotiateSession(SessionCompression.NONE, SessionEncryption.TLS, sessionChannelListener);
                if (semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS) &&
                        receivedSession[0] != null) {
                    if (receivedSession[0].getState() == Session.SessionState.NEGOTIATING) {
                        clientChannel.getTransport().setEncryption(SessionEncryption.TLS);
                        receivedSession[0] = null;
                        clientChannel.enqueueSessionListener(sessionChannelListener);
                        if (semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS) &&
                                receivedSession[0] != null) {
                            if (receivedSession[0].getState() == Session.SessionState.AUTHENTICATING) {
                                Identity identity = new Identity("samples", "take.io");
                                PlainAuthentication authentication = new PlainAuthentication();
                                authentication.setToBase64Password("take1234");
                                receivedSession[0] = null;
                                clientChannel.authenticateSession(identity, authentication, "default", sessionChannelListener);
                                if (semaphore.tryAcquire(1, 1000, TimeUnit.MILLISECONDS) &&
                                        receivedSession[0] != null) {
                                    if (receivedSession[0].getState() == Session.SessionState.ESTABLISHED) {
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
                                                out.printf(String.format("Command with id '%s' received from '%s':  Method: %s - URI: %s", command.getId(), command.getFrom(), command.getMethod(), command.getUri()));
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

                                        clientChannel.enqueueSessionListener(sessionChannelListener);
                                        
                                        while (clientChannel.getState() == Session.SessionState.ESTABLISHED) {
                                            out.print("Destination node (Type EXIT to quit): ");
                                            String toInput = inScanner.nextLine();
                                            if (toInput != null &&
                                                    toInput.equalsIgnoreCase("exit")) {
                                                break;
                                            }
                                            
                                            if (toInput != null && !toInput.isEmpty()) {
                                                Node node = Node.parse(toInput);
                                                out.print("Message text: ");
                                                PlainText plainText = new PlainText(inScanner.nextLine());
                                                Message message = new Message();
                                                message.setTo(node);
                                                message.setContent(plainText);
                                                clientChannel.sendMessage(message);
                                            }
                                        }
                                        
                                        semaphore.acquire();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        

    }
}
