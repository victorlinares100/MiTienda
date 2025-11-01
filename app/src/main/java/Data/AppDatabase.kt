package Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import Model.Product

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "mitienda_db")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}