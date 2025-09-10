package ohi.andre.consolelauncher.managers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import ohi.andre.consolelauncher.tuils.Tuils;

import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.ExecutePack;
public class GmailManager {

    private final Context context;

    // Mock structure for emails: sender -> list of emails
    private final HashMap<String, List<Email>> emails = new HashMap<>();

    // Scanner for CLI input
    private final Scanner scanner = new Scanner(System.in);

    public GmailManager(Context context) {
        this.context = context;

        // Sample/mock data for testing
        addEmail("alice@gmail.com", "Meeting update", "Hi, the meeting is moved to 3 PM", "2025-09-03 10:15", false);
        addEmail("bob@gmail.com", "Project deadline", "Please submit the report by Friday", "2025-09-03 09:50", true);
        addEmail("alice@gmail.com", "Follow-up", "Don't forget to send the files", "2025-09-02 17:20", false);
    }

    private void addEmail(String sender, String subject, String body, String timestamp, boolean read) {
        Email email = new Email(sender, subject, body, timestamp, read);
        emails.computeIfAbsent(sender, k -> new ArrayList<>()).add(email);
    }

    /** Command to list all emails or filter by sender **/
    public void handleMailCommand(String input) {
        String[] parts = input.trim().split("\\s+", 2);

        if (parts.length == 1) {
            // Only "mail" typed: show summary of all emails
            listAllEmailsSummary();
        } else {
            // "mail <sender>" typed
            String query = parts[1].toLowerCase();
            List<String> matches = new ArrayList<>();

            for (String sender : emails.keySet()) {
                if (sender.toLowerCase().startsWith(query)) {
                    matches.add(sender);
                }
            }

            if (matches.isEmpty()) {
                Tuils.sendOutput("No sender matched: " + query, context);
                return;
            }

            String selectedSender;
            if (matches.size() == 1) {
                selectedSender = matches.get(0);
            } else {
                // Show numbered suggestions
                Tuils.sendOutput("Multiple matches found:", context);
                for (int i = 0; i < matches.size(); i++) {
                    Tuils.sendOutput((i + 1) + ": " + matches.get(i), context);
                }
                Tuils.sendOutput("Select number: ", context);
                int choice = 1;
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (Exception e) {
                    Tuils.sendOutput("Invalid input. Defaulting to first match.", context);
                }
                if (choice < 1 || choice > matches.size()) choice = 1;
                selectedSender = matches.get(choice - 1);
            }

            // Show full emails from selected sender
            listEmailsFromSender(selectedSender);
        }
    }

    /** List all emails summary **/
    private void listAllEmailsSummary() {
        Tuils.sendOutput("=== All Emails Summary ===", context);
        for (String sender : emails.keySet()) {
            List<Email> senderEmails = emails.get(sender);
            for (Email email : senderEmails) {
                String status = email.read ? "[Read]" : "[Unread]";
                Tuils.sendOutput(email.timestamp + " " + status + " " + sender + " : " + email.subject, context);
            }
        }
    }

    /** List emails from a specific sender **/
    private void listEmailsFromSender(String sender) {
        List<Email> senderEmails = emails.get(sender);
        if (senderEmails == null || senderEmails.isEmpty()) {
            Tuils.sendOutput("No emails from: " + sender, context);
            return;
        }

        Tuils.sendOutput("=== Emails from " + sender + " ===", context);
        for (Email email : senderEmails) {
            String status = email.read ? "[Read]" : "[Unread]";
            Tuils.sendOutput(email.timestamp + " " + status + " Subject: " + email.subject, context);
        }

        Tuils.sendOutput("Type the email number to read full content: ", context);
        int choice = 1;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            Tuils.sendOutput("Invalid input. Showing first email.", context);
        }

        if (choice < 1 || choice > senderEmails.size()) choice = 1;
        Email email = senderEmails.get(choice - 1);

        // Show full email
        Tuils.sendOutput("=== Email Content ===", context);
        Tuils.sendOutput("From: " + email.sender, context);
        Tuils.sendOutput("Subject: " + email.subject, context);
        Tuils.sendOutput("Time: " + email.timestamp, context);
        Tuils.sendOutput("Body:\n" + email.body, context);

        email.read = true; // mark as read
    }

    /** Email object **/
    static class Email {
        String sender;
        String subject;
        String body;
        String timestamp;
        boolean read;

        public Email(String sender, String subject, String body, String timestamp, boolean read) {
            this.sender = sender;
            this.subject = subject;
            this.body = body;
            this.timestamp = timestamp;
            this.read = read;
        }
    }
}
