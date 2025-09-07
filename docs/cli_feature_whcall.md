▶️ Command Behavior

1️⃣ Command:

whcall

2️⃣ Output Example (Initial Display)

WhatsApp Call History (latest first):

1. +1234567890 | John Doe       | Video Call | 2025-09-07 15:22 | Answered
2. +1987654321 | Jane Smith     | Voice Call | 2025-09-07 13:15 | Missed
3. +1357924680 | Mark Lee       | Voice Call | 2025-09-06 20:10 | Rejected
4. +1472583690 | Alice Johnson  | Video Call | 2025-09-06 18:50 | Answered
... (up to last 20 entries)

Type number to call or type a search keyword:


---

3️⃣ Example: Search by Typing Keyword

User types:

Jane

Dynamic updated display:

Search Results:

1. +1987654321 | Jane Smith | Voice Call | 2025-09-07 13:15 | Missed

Select entry number to call or continue typing:


---

4️⃣ User Chooses to Call Jane Smith

User types:

1

Then CLI responds:

Calling +1987654321 (Jane Smith) via WhatsApp Voice Call...
(call session starts)

Type 'merge' to merge call, 'drop' to drop participant, or 'endcall' to end call.


---

5️⃣ Missed Call Alerts

When there are missed calls, the CLI shows an alert upon opening the CLI or at prompt:

⚠️ You have 2 missed WhatsApp calls. Type 'whcall' to review.

