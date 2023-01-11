# AWS S3 Trigger Function to send file to Discord server

An AWS Lambda function that can be used as an S3 trigger function for automatically sending new files to a Discord chat via a Discord Webhook.

Runtime: Java 11 Corretto

## Usage

### Create new Discord webhook
Create a new Webhook in the Discord server settings. You need sufficient permissions to do so.
Make sure to note the URL of the newly created Webhook, as you need it later.

### Create S3 trigger function
Create a new Lambda function, configure it as an S3 trigger function, and copy the code from the ``S3TriggerToDiscord`` class.

This class contains a variable called ``DISCORD_WEBHOOK_URL``, set it to the URL of the Discord Webhook you created earlier.
````java
// TODO enter your Discord webhook URL here
private static final String DISCORD_WEBHOOK_URL = "YOUR_WEBHOOK_HERE";
````

The ``S3TriggerToDiscord`` class also contains a method called ``handleRequest``, which needs to be set as the request handler for the S3 trigger function.

---
## General advice when working with S3 Triggers

This won't happen when you followed the guide from above, but it is still worth mentioning nonetheless.

When working with AWS S3 triggers it is very important that you don't accidentally create a loop
(i.e. An S3 trigger is triggered when a new file is uploaded: The trigger uploads a new file, which triggers the trigger function, which uploads a new file, which triggers the Trigger function, ...).
If that happens, and you don't catch it right away, you will consume a lot of resources and your AWS bill will skyrocket.

---