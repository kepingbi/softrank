softsearch
==========

Softrank is a smart ranking of twittos (members of twitter). It ranks those twittos, based on one keyword search and reliated hashtags (tags put on tweets) which are dinamically found.

Softrank is a good way to understand how you may use Akka (see http://akka.io).

To launch your own ranking, modify, in the src/main/resources/application.conf file the 

```keyword = "usa"```

by your own keyword. Softrank will rank the twittos which have twitt using this keyword as much as hashtag present in any processed twitt.

Twitter configuration
---------------------
For security reason, you need to add, in the src/main/resources/application.conf file, your twitter credentials, they may be found at http://dev.twitter.com
```
twitter{
    consumer-key = ""
    consumer-secret = ""
    token = ""
    token-secret = ""
}
```
Basic configuration
-------------------

In the src/main/resources/application.conf file: 
  - results-limit defines how many tweets or retweet would be scanned before returning the result
  - workers defines the number of paralell twitt processing

The other settings define how much information are asked to twitter. Modifying those may cause the application to stop providing results due to twitter restriction rules.
