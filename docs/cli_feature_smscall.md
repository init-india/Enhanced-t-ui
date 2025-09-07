CLI Features Plan

📩 SMS

Features

1. List All Messages

Command: sms + enter

Output: Latest 20 messages

Format:

[03 Sept 2025 22:46] Alice: Hey, how are you? (Unread)
[03 Sept 2025 21:30] Bob: Meeting confirmed. (Read)



2. Unread Messages

Command: unread + enter

Output: List of all unread messages (same format as above).



3. Read Messages

Command: read + enter

Output: List of all read messages.



4. Replied Messages

Command: replied + enter

Output: List of messages already replied.



5. Conversation with Contact

Command: sms <contact/number> + enter

Output: Full threaded conversation with timestamp and read/unread status.

Example:

[03 Sept 2025 22:46] Alice: Hey, how are you? (Unread)
[03 Sept 2025 22:47] You: All good, how about you? (Sent)



6. Send Message

Command: sms <contact/number> <message>

Output:

Message sent confirmation.

Last 20 messages in thread shown for relevance.




7. Message Security

Opening full SMS requires biometric authentication.

Inbox list shows only timestamp, sender, first line, status until authenticated.





---

📞 Calls

Features

1. Initiate Call

Command: call <contact/number> + enter

Action: Starts call.



2. Call Log for Contact

Command: call <contact/number> (without enter)

Output: Full call history for that contact, dynamically.

Format:

[03 Sept 2025 20:10] Alice - Answered
[02 Sept 2025 19:05] Alice - Missed
[01 Sept 2025 18:30] Alice - Rejected



3. Answer Incoming Call

Command: ans + enter

Action: Answers the current incoming call.



4. Reject Incoming Call

Command: rej + enter

Action: Rejects the current incoming call.



5. Conference Call

Command: merge + enter

Action: Merge two active calls into a conference.



6. Drop Participant from Conference

Command: drop <contact/number> + enter

Action: Removes participant from conference.



7. Missed Call Notifications

CLI alert + log entry whenever a call is missed.

8. endcall terminates all calls
