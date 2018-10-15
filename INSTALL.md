# Endless Install Instructions
*Guide written by caution3 with assistance by Spotlight for using the Endless bot by Artuto. (https://github.com/EndlessBot/Endless)* 
**Warning: Support is not provided for those wanting to self-host Endless. Do not ask for assistance or support.**
### Part One: Preparing your VPS
Please make sure you have the following installed:
- Maven (mvn)
- MySQL Server (mysql-server)
- Git (git)
- OpenJDK 11 Headless (openjdk-11-jdk-headless)
- Endless Bot Source.
*You'll want to clone from my repo, and not the official one, as the official one does not contain the files needed to setup the bot.*
- Before starting, clone this repo into your home directory on the VPS via Git.
#### MySQL Server Setup
*This guide assumes that you know how to use the MySQL Shell and the SQL Dialect.*
1. Create a new user on the server under the name `endless`. Make sure they have the same permissions as root does.
2. You'll need to run the SQL File in this repo called `endless.sql`, this is a set of instructions that will do the preparation work.
3. Exit the shell, and edit the `config.yml` to the endless user, this includes placing the password in the config, this shouldn't be a problem as long as your VPS is configured properly.

### Part Two: Setting up Endless
#### Compiling Endless
To compile Endless, go to the root directory of your cloned repo and run `mvn package`, this may take a while as Java isn't the most efficent and top speed language. If you get a message saying `BUILD SUCCESS`... **congrats!** Endless has now been compiled into a nice little folder called `target/`.
#### Preparing the config.yml
Before you start Endless, you'll need to setup the config.yml. This guide will go over the minimal setup requirements for Endless.
```yml
token: 'need i say' # REQUIRED.
prefix: 'dev!'
game: 'memes'
doneEmote: 'lol'
warnEmote: 'yo:'
errorEmote: 'whoopsies:'
# yeah this is just stupid but it's java so please just change `meme` as the name
dbUrl: 'jdbc:mysql://localhost/endless?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC' # REQUIRED
dbUsername: 'endless' # REQUIRED.
dbPassword: '' # REQUIRED.
# your discord id, REQUIRED.
ownerId: 
discordBotsToken: ''
discordBotListToken: ''
bingMapsKey: ''
darkskyKey: ''
giphyKey: ''
googleKey: ''
googleSearcherId: ''
yandexTranslateKey: ''
youtubeKey: ''
botlogWebhook: 'webhook_goes_here' # REQUIRED.
commandlogWebhook: 'likewise_lol' # REQUIRED.
api: false
# change rootGuildId as you'd like, usually REQUIRED.
rootGuildId: 
# empty list
coOwnerIds:
  -
status: ONLINE
botlog: true
debug: true
deepDebug: true
sentryEnabled: false
sentryDSN: ''
audioEnabled: false
```
A lot of the required fields should be self-explanatory. Webhooks can be created for channels in the channel settings, database login and URL template is provided and so on.
#### Running Endless
**To run Endless properly...**
Use this command in the root directory of your cloned repo: `java -jar target/Endless-5.3.8-jar-with-dependencies.jar normalBoot`.
The normalBoot argument is required, otherwise Endless **will not work.**

**NOTE: SUPPORT IS NOT PROVIDED FOR INSTALLING, BUILDING OR REHOSTING ENDLESS. DO NOT ASK. DO NOT TRY. DO NOT BOTHER.**
