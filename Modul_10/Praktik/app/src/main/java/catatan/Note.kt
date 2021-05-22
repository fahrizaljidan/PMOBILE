package catatan

@Entity(tableName = "note_table")
data class Note(
    var title: String,
    var description: String,
    var priority: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: int = 0
}

