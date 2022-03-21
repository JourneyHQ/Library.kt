# Discord Framework

> パッケージ毎にコマンド/イベントのクラスを読み込み、 
簡単に Discord Bot を作成できるようにするライブラリです。 
> 

## どうやって使うの？

1. まず、`FrameworkManager` を作成します。 

```kotlin 
val jda = FrameworkManager 
    .setJDABuilder(jdaBuilder) //JDABuilderをここに 
    .initCmdManager("dev.yuua.testbot.function.command") 
    .initEventManager("dev.yuua.testbot.function.event") 
    .build() 
``` 

2. 完成です。 

## コマンドの作り方

1. `FrameworkManager.initCmdManager` で指定したパッケージに、好きな名前のコマンドクラスを作成します。 

```kotlin 
class ping : CmdSubstrate { 
    override fun data(): CmdBuild = CommandData("ping", "reply pong!") 
        .setCmdFunction(this::replyPong) 
        .build() 

    private fun replyPong( 
        jda: JDA, guild: Guild?, isFromGuild: Boolean, 
        channel: MessageChannel, type: ChannelType, 
        member: Member?, user: User, 
        event: SlashCommandInteractionEvent 
    ) { 
        event.reply("Pong!!!!!!!").setEphemeral(true).queue() 
    } 
} 
``` 

2. 完成です。
