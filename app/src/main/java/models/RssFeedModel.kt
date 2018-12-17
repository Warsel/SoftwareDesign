package models

class RssFeedModel(
    var title: String,
    var link: String,
    var description: String,
    var pubDate: String,
    var imageUri: String
) {
    constructor(): this("", "", "", "", "")
}
