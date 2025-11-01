package com.example.mitienda

import Model.Product

object CartManager {
    private val items = mutableListOf<Product>()
    fun add(product: Product) = items.add(product)
    fun getAll(): List<Product> = items.toList()
}