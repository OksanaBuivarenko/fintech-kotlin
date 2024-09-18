package com.fintech.kotlin.dsl

import com.fintech.kotlin.data.News

fun prettyPrint(list: Collection<News>) = newsList {
    for (el in list) {
        news {
            id { +el.id.toString() }
            title { +el.title }
            description { +el.description }
            siteUrl { +el.siteUrl }
            place {
                placeId { +el.place?.id.toString() }
                placeTitle { +el.place?.title.toString() }
                placeAddress { +el.place?.address.toString() }
                placeLocation { +el.place?.location.toString() }
                placeSlug { +el.place?.slug.toString() }
                placeIsClosed { +el.place?.isClosed.toString() }
                placeSiteUrl { +el.place?.siteUrl.toString() }
            }
            favoritesCount { +el.favoritesCount.toString() }
            commentsCount { +el.commentsCount.toString() }
            rating { +el.rating.toString() }
            publicationDate { +el.getLocalDate().toString() }
        }
    }
}

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

@DslMarker
annotation class TagMarker

@TagMarker
abstract class Quotes(val name: String) : Element {
    val children = arrayListOf<Element>()

    protected fun <T : Element> initQuotes(quotes: T, init: T.() -> Unit): T {
        quotes.init()
        children.add(quotes)
        return quotes
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$colorTurquoise$indent\"$name:\"\n$formatReset")
        for (c in children) {
            c.render(builder, indent + "\t")
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

abstract class QuotesWithText(name: String) : Quotes(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

fun newsList(init: NewsList.() -> Unit): NewsList {
    val newsList = NewsList()
    newsList.init()
    return newsList
}

val boldItalics: String = "\u001B[1;3m"
val underlined: String = "\u001B[4m"
val colorTurquoise: String = "\u001B[36m"
val formatReset: String = "\u001B[0m"

class NewsList : QuotesWithText("${boldItalics}PRETTY PRINT LIST NEWS") {
    fun news(init: NewsDSL.() -> Unit) = initQuotes(NewsDSL(), init)
}

class NewsDSL : QuotesWithText("${underlined}NEWS") {
    fun id(init: Id.() -> Unit) = initQuotes(Id(), init)
    fun title(init: Title.() -> Unit) = initQuotes(Title(), init)
    fun description(init: Description.() -> Unit) = initQuotes(Description(), init)
    fun siteUrl(init: SiteUrl.() -> Unit) = initQuotes(SiteUrl(), init)
    fun favoritesCount(init: FavoritesCount.() -> Unit) = initQuotes(FavoritesCount(), init)
    fun commentsCount(init: CommentsCount.() -> Unit) = initQuotes(CommentsCount(), init)
    fun publicationDate(init: PublicationDate.() -> Unit) = initQuotes(PublicationDate(), init)
    fun rating(init: Rating.() -> Unit) = initQuotes(Rating(), init)
    fun place(init: Place.() -> Unit) = initQuotes(Place(), init)
}

class Id : QuotesWithText("id")

class Title : QuotesWithText("title")

class Description : QuotesWithText("description")

class SiteUrl : QuotesWithText("site URL")

class FavoritesCount : QuotesWithText("favorites count")

class CommentsCount : QuotesWithText("comments count")

class PublicationDate : QuotesWithText("publication date")

class Rating : QuotesWithText("rating")

class PlaceId : QuotesWithText("place id")

class PlaceTitle : QuotesWithText("place title")

class PlaceSlug : QuotesWithText("place slug")

class PlaceAddress : QuotesWithText("place address")

class PlaceSiteUrl : QuotesWithText("place site URL")

class PlaceIsClosed : QuotesWithText("place is closed")

class PlaceLocation : QuotesWithText("place location")

class Place : QuotesWithText("place") {
    fun placeId(init: PlaceId.() -> Unit) = initQuotes(PlaceId(), init)
    fun placeTitle(init: PlaceTitle.() -> Unit) = initQuotes(PlaceTitle(), init)
    fun placeSlug(init: PlaceSlug.() -> Unit) = initQuotes(PlaceSlug(), init)
    fun placeAddress(init: PlaceAddress.() -> Unit) = initQuotes(PlaceAddress(), init)
    fun placeSiteUrl(init: PlaceSiteUrl.() -> Unit) = initQuotes(PlaceSiteUrl(), init)
    fun placeIsClosed(init: PlaceIsClosed.() -> Unit) = initQuotes(PlaceIsClosed(), init)
    fun placeLocation(init: PlaceLocation.() -> Unit) = initQuotes(PlaceLocation(), init)
}