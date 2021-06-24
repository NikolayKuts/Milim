package com.example.milim.interfaces

import com.example.milim.domain.pojo.Deck

interface MainView {
    fun showData(decksFromDB: List<Deck>)
    fun showToastIfDeckExist()
    fun showToastOnDeckCreated()
}