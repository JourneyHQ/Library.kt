# Config

> JSONで作成した設定ファイルのプロトタイプを読み込み、  
> Kotlin,Javaから簡単に値を使用できるようにするライブラリです。

## どうやって使うの？

1. まず、設定ファイルを用意します。 `デフォルト: ./config.json`

    ```json
    {
      "doPowa": "true",
      "token": {
        "powaAPI": "powaaaaatoken12345",
        "nazoAPI": "4872395623487"
      }
    }
    ```

2. そして、設定の設計ファイルを用意します。 `デフォルト: ./config.struct.json`

    ```json
    {
      "doPowa": "boolean:optional:false",
      "token": {
        "powaAPI": "string:required",
        "nazoAPI": "number:required",
        "hogeAPI": "number:optional"
      }
    }
    ```

3. 次に、Kotlin,Javaに戻ります。  
   `Config.from()` で読み込み、`Config[”string.value”]` や `Config.number(”limit”)` などで値を取り出します。  
   `optional` かつデフォルト値が設定されていない場合や、デフォルト値の型が設定された型にキャストできない場合のみ `null` が返されます。

    ```kotlin
    fun main() {
    	Config.from("./config.json","./config.struct.json")
    	//とか書いてありますが、デフォルト値の場合はConfig.from()で大丈夫です。
    
    	println(Config.boolean("doPowa")) //-> true
    	println(Config["token.powaAPI"]) //-> powaaaaatoken12345
    	println(Config.number("token.nazoAPI")) //-> 4872395623487
    	println(Config.number("token.hogeAPI")) //-> null
    }
    ```

4. おわりです。
