<h1 align="center">Discord Framework</h1>
<h3 align="center">Easy to Use & Package-Based Discord Framework</h3>

## How to

1. Create a `FrameworkManager` 

```kotlin 
    val framework = FrameworkManager
        .create( 
            //It depends on jda-ktx and has the same syntax.
            "token here",
            true, Duration.INFINITE,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_VOICE_STATES
        )
        //Specifies the package in which the command and event classes will be placed.
        //How to create classes is described below.
        .initCmdManager("dev.yuua.nazobot.commands") 
        .initEventManager("dev.yuua.nazobot.events")
        .build()
``` 

2. Create a command class. (SlashCommand Only)

```kotlin 
class Ping : FrCmdStruct {
    override fun cmd() = FrCmd("ping", "ping pong")
        .addOptions(FrOption<String>("name", "your name.", true))
        .setFunction(ping)
        .build()

    //If the parameter is not used, it can be replaced by "_".
    private val ping = FrSlashFunction { jda, guild, isFromGuild, isFromThread, channel, channelType, member, user, event ->
        event.reply("${event.getOption<String>("name")} Pong!").queue()
    }
}
``` 

3. Create a command class. (SlashCommand + Accepts TextCommand)

```kotlin 
class Ping : FrCmdStruct {
    override fun cmd() = FrCmd("ping", "ping pong", /* aliases -> */ "pong", "p") 
        .addOptions(FrOption<String>("name", "your name.", true))
        .setFunction(ping)
        .build()

    private val ping = FrTextFunction { jda, guild, isFromGuild, isFromThread, channel, channelType, member, user, event ->
        event.reply("${event.option<String>("name")} Pong!").queue()
    }
}
``` 

4. Create an event class.

```kotlin 
class Message : FrEventStruct {
    override fun listener(jda: JDA) = jda.listener<MessageReceivedEvent> {
        println("msg: " + it.message.contentRaw)
    }
}
``` 
