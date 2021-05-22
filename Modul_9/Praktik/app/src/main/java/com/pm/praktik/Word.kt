package com.pm.praktik

@Entity(tableName="word_table")
data class Word (@PrimayKey @ColumInfo(name= "word") val word: Sring)