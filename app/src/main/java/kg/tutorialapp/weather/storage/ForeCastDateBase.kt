package kg.tutorialapp.weather.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kg.tutorialapp.weather.models.ForeCast

@Database(
    entities = [ForeCast::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(ModelsConverter ::class, CollectionsConverter::class)
abstract class ForeCastDateBase: RoomDatabase() {
    abstract fun forecastDao():ForeCastDao
    companion object{
        const val DB_NAME="foreCast_DB"

        }
    }

