package com.example.milim.presentation.interfaces

import com.example.milim.domain.pojo.Deck

interface MainView {
    fun showData(decksFromDB: List<Deck>)
    fun showToastIfDeckExist()
    fun showToastOnDeckCreated()
    fun showToastOnDeckDeleted()
}