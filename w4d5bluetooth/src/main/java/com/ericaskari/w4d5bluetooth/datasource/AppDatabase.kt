package com.ericaskari.w4d5bluetooth.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceService
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservice.BluetoothDeviceServiceDao
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicDao
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor
import com.ericaskari.w4d5bluetooth.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorDao
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDevice
import com.ericaskari.w4d5bluetooth.bluetoothsearch.BluetoothDeviceDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Mohammad Askari
 */
@Database(
    entities = [BluetoothDevice::class, BluetoothDeviceService::class, BluetoothDeviceServiceCharacteristic::class, BluetoothDeviceServiceCharacteristicDescriptor::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bluetoothDeviceDao(): BluetoothDeviceDao
    abstract fun bluetoothDeviceServiceDao(): BluetoothDeviceServiceDao
    abstract fun bluetoothDeviceServiceCharacteristicDao(): BluetoothDeviceServiceCharacteristicDao
    abstract fun bluetoothDeviceServiceCharacteristicDescriptorDao(): BluetoothDeviceServiceCharacteristicDescriptorDao

    companion object {
        private var DB_NAME: String = "w4d5bluetooth"

        @Volatile
        private var Instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback())
                    .build()
                    .also { Instance = it }
            }
        }


        /**
         * Database initializer
         */
        private class AppDatabaseCallback() : Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        Instance!!.populateDatabase()
                    }
                }
            }
        }
    }

    /**
     * Database initializer
     */
    suspend fun populateDatabase() {
        println("one time populating the database")
    }

}
