[![Build Status](https://travis-ci.com/davidjulien/banks_frontend_telegram.svg?branch=master)](https://travis-ci.com/davidjulien/banks_frontend_telegram)
[![Code Coverage](https://codecov.io/gh/davidjulien/banks_frontend_telegram/branch/master/graph/badge.svg)](https://codecov.io/gh/davidjulien/banks_frontend_telegram)

banks_frontend_telegram
=======================

A telegram bot to receive notifications whew new transactions are stored by [banks_fetch](https://github.com/davidjulien/banks_fetch).

Each time new transactions are discovered in `banks_fetch` database, bot sends account current balance and for each transaction a message describing it.

<img src="https://user-images.githubusercontent.com/726552/93601514-9d393d80-f9c1-11ea-912a-46752eab8342.png" title="Bot displaying transactions"></img>


Configuration
---

1. Open https://telegram.me/botfather on your phone or open a discussion with @BotFather on Telegram
2. Send '/newbot' and answer questions (name and username of your bot)
3. Create a `local.properties` file (from `local.properties.sample`) and replace `YOUR_BOT_TOKEN_FROM_BOTFATHER` by the token given by @BotFather
4. Replace `YOUR_SECURITY_CODE` by a random string in `local.properties`

Build and run
-----

Run this command to build a jar including all dependencies

```console
mvn package assembly:single
```

Launch bot

```console
java -jar target/banks_frontend_telegram-0.1.0-SNAPSHOT-jar-with-dependencies.jar
```

Init conversation on Telegram

- Open URL https://t.me/YOUR_BOT_USERNAME to start conversation
- Send an init command containing your security code : `/init YOUR_SECURITY_CODE`
- Wait for events (new transactions detected)

Don't forget to run `banks_fetch` in an other session to fetch data from your banks!

Tests
---

All classes are tested. Expected code coverage is 100%. Only main function is currently excluded.

```console
mvn verify
```
