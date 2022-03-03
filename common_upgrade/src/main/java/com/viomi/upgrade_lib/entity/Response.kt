package com.viomi.upgrade_lib.entity

/**
 * Created by mai on 2020/9/29.
 */
class Response<T> {

    val code: Int = 0
    val desc: String? = null
    val result: T? = null


    override fun toString(): String {
        return "Response(code=$code, desc=$desc, result=$result)"
    }

}