package isen.CedricLucieFlorent.benfit.Models

import kotlin.collections.ArrayList

class Sport
{
    var name: String? = ""
    var categories: ArrayList<Category> = ArrayList()

    constructor(name : String, categories : ArrayList<Category>){
        this.name = name
        this.categories = categories
    }
    fun getSportName() : String {
        return "${name}"
    }
}
