package org.example;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.commons.cli.*;

public class Main {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            Options options = new Options();
            options.addOption("u", "user", true, "username");
            options.addOption("f", "file", true, "specify file to load");
            options.addOption("li", "list", false, "list all files");
            options.addOption("cr", "create", false, "list all files");
            options.addOption("?", "help", false, "print help");
            options.addOption(null, "create", false, "create user");

            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            String userName;
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();

                final PrintWriter writer = new PrintWriter(System.out);
                formatter.printUsage(writer,80,"TerminalRecorder", options);
                writer.flush();

                System.out.println("Commands:");
                System.out.println("create - Creates user");
                System.out.println("upload - Upload file to database");
                System.out.println("delete - Deletes file from database");
                System.out.println("test - Run upload/delete loop test");
                System.out.println("list - List all files for user");
                return;
            }
            if (line.hasOption("user")) {
                userName = line.getOptionValue("user");
            } else {
                System.out.println("user name not provided");
                return;
            }
            var backend = new CosmosDBBackend();
            try {
                backend.connectDatabase();
                var argList = line.getArgList();
                for (var arg : argList) {
                    switch (arg) {
                        case "create":
                            System.out.println("Create user " + userName +"…");
                            var user = backend.getUser(userName);
                            if (user == null) {
                                backend.createUser(userName);
                            }
                            return;
                        case "upload":
                            upload_command(line, userName, backend);
                            return;
                        case "delete":
                            delete_command(line, userName, backend);
                            return;
                        case "test":
                            while (true) {
                                upload_command(line, userName, backend);
                                delete_command(line, userName, backend);
                            }
                        case "list":
                            System.out.println("All entries:");
                            for (var entry : backend.getEntries()) {
                                System.out.println(entry.header.title);
                            }
                            return;
                    }
                }
            } finally {
                backend.close();
            }
        }
        catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
        catch (Exception e) {
            System.err.println("Error: " + e);
        }
    }

    private static void delete_command(CommandLine line, String userName, CosmosDBBackend backend) {
        User user;
        user = backend.getUser(userName);
        if (user == null) {
            System.err.println("User " + userName + " not found.");
            return;
        }
        var fileName = line.getOptionValue("file");
        if (fileName == null) {
            System.err.println("No file name provided.");
            return;
        }
        System.out.println("Delete file '" + fileName +"'…");
        backend.deleteFile(user, fileName);
    }

    private static void upload_command(CommandLine line, String userName, CosmosDBBackend backend) throws org.json.simple.parser.ParseException, IOException {
        User user;
        user = backend.getUser(userName);
        if (user == null) {
            System.err.println("User " + userName + " not found.");
            return;
        }
        var fileName = line.getOptionValue("file");
        System.out.println("Found user " + userName +"…");

        if (fileName == null) {
            System.err.println("No file name provided.");
            return;
        }
        System.out.println("Upload file '" + fileName +"'…");
        var file = File.loadFile(fileName);
        file.getHeader().setUserId(user.id);
        backend.update(file);
    }
}
// -u C:\work\terminal_rec\test.cast