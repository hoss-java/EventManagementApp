package com.EventManApp.interfaces.ssh;

import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import java.io.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.EventManApp.BaseInterface;
import com.EventManApp.ResponseCallbackInterface;
import com.EventManApp.lib.DebugUtil;
import com.EventManApp.MenuUI;
import com.EventManApp.InputUI;

public class CustomCommandFactory implements CommandFactory {
    private JSONObject commandsFork;
    protected ResponseCallbackInterface callbackFork = null;
    
    public void setConsoleSettings(ResponseCallbackInterface callback, JSONObject commands) {
        this.callbackFork = callback;
        this.commandsFork = commands;
    }

    public ResponseCallbackInterface getCallback() {
        return callbackFork;
    }
    
    public JSONObject getCommands() {
        if (commandsFork == null) {
            System.err.println("ERROR: commandsFork is null in getCommands()");
            return new JSONObject();
        }
        return commandsFork;
    }

    @Override
    public Command createCommand(ChannelSession channel, String command) {
        System.out.println("Creating command for: " + (command == null || command.isEmpty() ? "interactive shell" : command));
        return new CustomCommand(channel, command, this);
    }

    private class SshConsoleInterface extends BaseInterface {

        // Constructor that accepts SSH streams instead of System.in
        public SshConsoleInterface(ResponseCallbackInterface callback, PrintStream out, InputStream in) {
            super(callback,out,in);
        }

        private void runSelectedCommand(JSONObject selectedMenuObject) {
            InputUI inputUI = new InputUI(this.out, this.in);
            JSONObject payload = new JSONObject();
            payload.put("identifier", selectedMenuObject.getString("identifier"));

            JSONObject command = selectedMenuObject.getJSONObject("command");
            if (command.has("args")) {
                JSONObject args = new JSONObject();
                JSONObject arguments = command.getJSONObject("args");

                for (String argName : arguments.keySet()) {
                    JSONObject argType = arguments.getJSONObject(argName);
                    String argValue = inputUI.getUserInput(argName, argType);
                    String argField = argType.optString("field", argName);
                    args.put(argField, argValue);
                }
                
                JSONObject payloadCommand = new JSONObject();
                payloadCommand.put("id", command.getString("action"));
                payloadCommand.put("data", args);
                payloadCommand.put("args", arguments);

                JSONArray payloadCommandsArray = new JSONArray();
                payloadCommandsArray.put(payloadCommand);
                payload.put("commands", payloadCommandsArray);
            }

            String response = callback.ResponseHandler("ConsoleInterface", payload.toString());
            printJson(response);
            inputUI.waitForKeyPress();
            
            // Send response to SSH client
            println("Response: " + response);                
        }

        @Override
        public JSONObject executeCommands(JSONObject commands) {
            if (commands == null || commands.length() == 0) {
                println("No commands available!");
                return new JSONObject().put("error", "No commands");
            }
            
            MenuUI menuUI = new MenuUI("Available Commands (ConsoleInterface)",this.out, this.in);
            while (true) {
                JSONObject selectedMenuObject = menuUI.displayMenu(commands);
                
                if (selectedMenuObject.isEmpty()) {
                    println("Exiting EventMan console...");
                    return selectedMenuObject.put("ConsoleInterface", "exit");
                }
                
                runSelectedCommand(selectedMenuObject);
            }
        }

    }

    private class CustomCommand implements Command {
        private final ChannelSession channel;
        private final String command;
        private PrintStream out;
        private InputStream in;
        private PrintStream err;
        private ExitCallback exitCallback;
        private Thread commandThread;
        private final CustomCommandFactory factory;

        public CustomCommand(ChannelSession channel, String command, CustomCommandFactory factory) {
            this.channel = channel;
            this.command = command == null ? "" : command;
            this.factory = factory;
        }

        @Override
        public void setInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public void setOutputStream(OutputStream out) {
            this.out = new PrintStream(out, true);  // Convert to PrintStream with auto-flush
        }

        @Override
        public void setErrorStream(OutputStream err) {
            this.err = new PrintStream(err, true);  // Convert to PrintStream with auto-flush
        }

        @Override
        public void setExitCallback(ExitCallback callback) {
            this.exitCallback = callback;
        }

        @Override
        public void start(ChannelSession channel, Environment env) throws IOException {
            commandThread = new Thread(() -> {
                try {
                    if (!command.isEmpty()) {
                        executeSpecificCommand(command);
                    } else {
                        startInteractiveShell();
                    }
                } catch (Exception e) {
                    System.err.println("Error in command execution: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            commandThread.setDaemon(true);
            commandThread.start();
        }

        private void executeSpecificCommand(String cmd) {
            try {
                String result = executeJavaCommand(cmd);
                out.print(result);
                out.flush();
                if (exitCallback != null) {
                    exitCallback.onExit(0);
                }
            } catch (Exception e) {
                err.println("Error executing command: " + e.getMessage());
                err.flush();
                if (exitCallback != null) {
                    exitCallback.onExit(1);
                }
            }
        }

        private void startInteractiveShell() {
            try {
                out.println("Welcome to Java SSH Console! Type 'exit' to quit.");

                StringBuilder inputBuffer = new StringBuilder();
                int character;

                while (true) {
                    out.print("\r\njava> ");
                    out.flush();
                    inputBuffer.setLength(0);

                    while ((character = in.read()) != -1) {
                        if (character == '\n' || character == '\r') {
                            out.println();
                            break;
                        } else if (character == 8 || character == 127) {
                            // Backspace handling
                            if (inputBuffer.length() > 0) {
                                inputBuffer.deleteCharAt(inputBuffer.length() - 1);
                                out.print("\b \b");  // Backspace, space, backspace
                                out.flush();
                            }
                        } else if (character >= 32 && character < 127) {
                            inputBuffer.append((char) character);
                            out.print((char) character);
                            out.flush();
                        }
                    }

                    if (character == -1) {
                        System.out.println("[SERVER] Client disconnected");
                        break;
                    }

                    String input = inputBuffer.toString().trim();

                    if (input.isEmpty()) {
                        continue;
                    }

                    if ("exit".equalsIgnoreCase(input)) {
                        out.println("\r\nExiting Java console...\r\n");
                        break;
                    }

                    String result = executeJavaCommand(input);
                    out.println();
                    out.print(result);
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("IOException in interactive shell: " + e.getMessage());
            } finally {
                close();
                if (exitCallback != null) {
                    exitCallback.onExit(0);
                }
            }
        }

        private String executeJavaCommand(String input) {
            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";
            
            switch (command) {
                case "eventman":
                    return executeEventmanCommand(args);
                case "help":
                    return executeHelpCommand();
                default:
                    return "Unknown command: " + command + ". Type 'help' for available commands.\r\n";
            }
        }

        private String executeEventmanCommand(String args) {
            JSONObject commands = factory.getCommands();
            ResponseCallbackInterface callback = factory.getCallback();

            if (callback == null) {
                return "Error: console-callback is invalid!\r\n";
            }
            
            if (commands == null || commands.length() == 0) {
                return "Error: No commands available!\r\n";
            }
            
            try {
                SshConsoleInterface sshConsoleInterface = new SshConsoleInterface(callback, out, in);
                JSONObject result = sshConsoleInterface.executeCommands(commands);
                return "EventMan execution completed.\r\n";
            } catch (Exception e) {
                System.err.println("Exception in executeEventmanCommand: " + e.getMessage());
                e.printStackTrace();
                return "Error executing EventMan: " + e.getMessage() + "\r\n";
            }
        }

        private String executeHelpCommand() {
            return "Available commands:\r\n" +
                   "  eventman         - Run EventManApp console\r\n" +
                   "  help             - Show this help message\r\n" +
                   "  exit             - Exit the console\r\n";
        }

        @Override
        public void destroy(ChannelSession channel) {
            close();
            if (commandThread != null && commandThread.isAlive()) {
                commandThread.interrupt();
            }
        }

        private void close() {
            try {
                if (in != null) { in.close(); }
                if (out != null) { out.close(); }
                if (err != null) { err.close(); }
            } catch (IOException e) {
                System.err.println("Error closing streams: " + e.getMessage());
            }
        }
    }
}

