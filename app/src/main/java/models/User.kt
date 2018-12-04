package models

import android.net.Uri

class User (
    var name: String,
    var surname: String,
    var phoneNumber: String,
    var email: String
) {
    constructor() : this("", "", "", "")
}