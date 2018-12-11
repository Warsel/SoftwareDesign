package models

class User (
    var name: String,
    var surname: String,
    var phoneNumber: String
) {
    constructor() : this("", "", "")
}