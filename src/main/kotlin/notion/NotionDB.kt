package notion

import universal.LibFlow
import kotlinx.coroutines.runBlocking
import org.jraf.klibnotion.client.Authentication
import org.jraf.klibnotion.client.ClientConfiguration
import org.jraf.klibnotion.client.NotionClient
import org.jraf.klibnotion.model.base.EmojiOrFile
import org.jraf.klibnotion.model.base.UuidString
import org.jraf.klibnotion.model.base.reference.DatabaseReference
import org.jraf.klibnotion.model.database.query.DatabaseQuery
import org.jraf.klibnotion.model.file.File
import org.jraf.klibnotion.model.page.Page
import org.jraf.klibnotion.model.pagination.Pagination
import org.jraf.klibnotion.model.property.sort.PropertySort
import org.jraf.klibnotion.model.property.value.PropertyValueList

class NotionDB {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    lateinit var notionClient: NotionClient
    private val db = HashMap<String, String>()
    fun auth(token: String): NotionDB {
        notionClient = NotionClient.newInstance(
            ClientConfiguration(
                Authentication(token)
            )
        )
        return this
    }

    fun addDB(dbName: String, dbId: String): NotionDB {
        runBlocking {
            try {
                notionClient.databases.getDatabase(dbId)
                db[dbName] = dbId
            } catch (e: Exception) {
                libFlow.failure("データベースの受け取りに失敗しました。")
            }
        }
        return this
    }

    fun query(
        name: String,
        query: DatabaseQuery? = null,
        sort: PropertySort? = null,
        pagination: Pagination = Pagination(),
    ): List<Page> {
        if (!db.containsKey(name))
            throw IllegalArgumentException("データベース: $name が見つかりませんでした！")

        return runBlocking {
            return@runBlocking notionClient
                .databases
                .queryDatabase(db[name]!!, query, sort, pagination)
                .results
        }
    }

    fun add(
        name: String,
        iconURL: String? = null,
        props: PropertyValueList
    ): Page {
        if (!db.containsKey(name))
            throw IllegalArgumentException("データベース: $name が見つかりませんでした！")

        return runBlocking {
            return@runBlocking notionClient.pages.createPage(
                parentDatabase = DatabaseReference(db[name]!!),
                icon = if (iconURL == null) null else File(iconURL),
                properties = props
            )
        }
    }

    fun edit(
        id: UuidString,
        icon: EmojiOrFile? = null,
        cover: File? = null,
        properties: PropertyValueList
    ): Page {
        return runBlocking {
            try {
                notionClient.pages.getPage(id)
            } catch (e: Exception) {
                libFlow.failure("ページの受け取りに失敗しました。")
            }
            return@runBlocking notionClient.pages.updatePage(id, icon, cover, properties)
        }
    }
}