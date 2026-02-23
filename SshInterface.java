package com.EventManApp.interfaces;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorKeyPairProvider; // Check this import
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.env.Environment;
import org.apache.sshd.server.command.ExitCallback; // Check this import

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.BaseInterface;
import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.lib.TokenizedString;

public class SshInterface extends BaseInterface {
    private final Scanner scanner;
    private SshServer sshd;
    private List<User> users = new ArrayList<>();
    private String configFile = "sshinterface.properties";
    private String serviceAddress = "172.32.0.11";
    private int servicePort = 32722;

    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + configFile);
                return;
            }

            // Load properties file
            props.load(input);
            this.servicePort = Integer.parseInt(props.getProperty("ssh.port"));
            this.serviceAddress = props.getProperty("ssh.address");

            for (int i = 1; ; i++) {
                String username = props.getProperty("sshuser" + i + ".username");
                String password = props.getProperty("sshuser" + i + ".password");
                if (username == null || password == null) {
                    break;
                }
                users.add(new User(username, password));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SshInterface(ResponseCallbackInterface callback) {
        super(callback);
        this.scanner = new Scanner(System.in);
        loadProperties();
        sshCreateServer(servicePort);
    }

    @Override
    public JSONObject executeCommands(JSONObject commands) {
        start(); // Start the server upon instantiation

        try {
            // Keep the service running while the running flag is true
            while (getRunningFlag()) {
                Thread.sleep(100); // Short sleep to yield control
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Service thread was interrupted.");
        } finally {
            shutDown(); // Call shutdown method before exiting
        }
        return new JSONObject().put("SshInterface", "stopped");
    }

    public void sshCreateServer(int port) {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new SimpleGeneratorKeyPairProvider(Paths.get("hostkey.ser")));
        sshd.setPasswordAuthenticator((user, pwd, session) -> 
                users.stream().anyMatch(u -> u.username.equals(user) && u.password.equals(pwd))
        );

        sshd.setCommandFactory(new CommandFactory() {
            @Override
            public Command createCommand(ChannelSession channel, String command) {
                return new Command() {
                    private OutputStream out;
                    private InputStream in;

                    @Override
                    public void setInputStream(InputStream in) {
                        this.in = in;
                    }

                    @Override
                    public void setOutputStream(OutputStream out) {
                        this.out = out;
                    }

                    @Override
                    public void setErrorStream(OutputStream err) {
                        // Optional error handling can be added here
                    }

                    @Override
                    public void start(Environment env) {
                        try (Scanner commandScanner = new Scanner(in)) {
                            out.write("Welcome to the Java console! Type 'exit' to close.\n".getBytes());
                            out.flush();

                            while (true) {
                                out.write("java> ".getBytes());
                                out.flush();

                                String input = commandScanner.nextLine(); // Read user input

                                if ("exit".equalsIgnoreCase(input.trim())) {
                                    out.write("Exiting Java console...\n".getBytes());
                                    out.flush();
                                    break;  // End the command
                                }

                                // Process the command here
                                String result = executeJavaCommand(input);
                                out.write(result.getBytes());
                                out.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            close();
                        }
                    }

                    private String executeJavaCommand(String input) {
                        // Execute the command and return a placeholder result
                        return "Executed command: " + input + "\n";
                    }

                    private void close() {
                        try {
                            if (in != null) in.close();
                            if (out != null) out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void destroy(ChannelSession session) {
                        // Cleanup resources if needed
                    }

                    @Override
                    public void setExitCallback(ExitCallback callback) {
                        // Store or use ExitCallback
                    }
                };
            }
        });
    }

    public void start() throws IOException {
        if (sshd != null) {
            sshd.start();
            System.out.println("SSH Server started on port: " + sshd.getPort());
        }
    }

    public void shutDown() {
        if (sshd != null) {
            try {
                sshd.stop();
                System.out.println("SSH Server stopped.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Example User class to store user data
    private static class User {
        String username;
        String password;

        User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
}
