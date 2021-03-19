package com.example.socialmedia.Model

import android.widget.NumberPicker
import java.util.*
import kotlin.collections.ArrayList

data class Post(
    var Post_caption:String="",
    var Post_image :String="",
    var User :String="",
    var Time:String = "",
    var Postref:String="",
    var Likearray:ArrayList<String> = ArrayList()
)