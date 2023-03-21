<h1 align="center">Qnortz</h1>
<h3 align="center">Easy to Use & Package-Based Discord Framework</h3>
<h4 align="center">Coming soon™ with more functions</h4>

```kotlin
fun main() {
    Qnortz {
        name = "QnortzBot" // name of your bot
        token = "token hereeeeeeeeeeeeeeeeeeeeee." // discord token
        initCommands("package.where.command.classes.is.placed") // package where command classes is placed
        intents(GatewayIntent.GUILD_MESSAGES) // intents
        
        if (isDevEnvironment)
            devEnv(
                "dev", // additional command prefix for development environment.
                "1234567890" // guilds for testing. commands will be only available on these guilds.
            ) 
    }.build()
}
```

```kotlin
class Hello : CommandStruct {
    override val command = Command("hello", "hello command :wave:") {
        subcommandGroup("subcommandgroup1", "test1") {
            subcommand("subcommand1", "say hello") {
                option<String>("name", "your name")
                slashFunction {
                    reply_("${getOption<String>("name")} hello!").setEphemeral(true).queue()
                }
            }
            subcommand("subcommand2", "say hello") {
                option<Member>("member", "member to say hello", true)
                rules {
                    if (!isFromGuild) RulesResult("Guild only!", RulesResultType.Failed)
                    else RulesResult(type = RulesResultType.Passed)
                }
                textFunction {
                    reply("${option<Member>("name").nickname} hello!").queue()
                }
            }
        }
    }.build()
}

```

# Todo
- [ ] Improve my poor English in the KDoc.
- [ ] Java language support
- [x] Guild-only command (Nonnull Guild & Member)
- [ ] English & Japanese support
- [ ] KDoc (<- Important)
- [ ] Inline Autocomplete Function
- [ ] No timeout button / selectmenu support (Class)
- [ ] ContextMenu Support (Class)
