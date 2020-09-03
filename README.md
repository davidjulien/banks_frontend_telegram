[![Build Status](https://travis-ci.com/davidjulien/banks_frontend_telegram.svg?branch=master)](https://travis-ci.com/davidjulien/banks_frontend_telegram)
[![Code Coverage](https://codecov.io/github/davidjulien/banks_frontend_telegram)](https://codecov.io/gh/davidjulien/banks_frontend_telegram)

banks_frontend_telegram
=======================

A telegram bot to receive notifications whew new transactions are stored by [banks_fetch](https://github.com/davidjulien/banks_fetch).

Current version is just an echo bot.

Configuration
---

1. Open https://telegram.me/botfather on your phone or open a discussion with @BotFather on Telegram
2. Send '/newbot' and answer questions (name and username of your bot)
3. Create a `local.properties` file (from `local.properties.sample`) and replace `YOUR_BOT_TOKEN_FROM_BOTFATHER` by the token given by @BotFather
4. Replace `YOUR_SECURITY_CODE` by a random string
5. Open URL https://t.me/YOUR_BOT_USERNAME to start conversation
6. Send an init command containing your security code : `/init YOUR_SECURITY_CODE`

Build and run
-----

Run this command to build a jar including all dependencies

```console
mvn package assembly:single
```

```console
java -jar target/banks_frontend_telegram-0.1.0-SNAPSHOT-jar-with-dependencies.jar
```

Tests
---

All classes are test. Expected code coverage is 100%. Only main function is currently excluded.

```console
mvn verify
```
