package com.example.whatsappcloone

import com.google.firebase.firestore.FieldValue

data class User(
    val name:String,
    val imageUrl:String,
    val thumbImage:String,
    val uId:String,
    val deviceToken:String,
    val status:String,
    val onlineStatus:String
){
    constructor():this("","","","","","","")
    constructor(name:String,imageUrl: String,thumbImage: String,uId: String):this(
        name,
        imageUrl,
        thumbImage,
        uId,
        "",
        "Hey I am using whatsapp ",
        ""
    )
}
