package com.igoryan94.filmsearch.views.recycler.models

import androidx.annotation.DrawableRes

class Product(val id: Int, @DrawableRes val idIcon: Int, val name: String, val desc: String) : Item