package models

class User (
    var name: String,
    var surname: String,
    var phoneNumber: String,
    var imageUri: String
) {
    constructor() : this("", "", "", "")
}