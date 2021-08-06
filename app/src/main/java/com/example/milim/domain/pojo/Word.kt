package com.example.milim.domain.pojo

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

private const val WORDS_TABLE_NAME = "words"

@Entity(tableName = WORDS_TABLE_NAME)
data class Word(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val wordId: Int = 0,
    val deckId: Int,
    val word: String
) : Parcelable {
    constructor() : this(deckId = -1, word = "empty")
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(wordId)
        dest?.writeInt(deckId)
        dest?.writeString(word)
    }

    companion object CREATOR : Parcelable.Creator<Word> {
        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }

}