package com.ericaskari.w4d5graph.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ericaskari.w4d5graph.bluetoothdeviceservice.BluetoothDeviceService
import com.ericaskari.w4d5graph.bluetoothdeviceservice.BluetoothDeviceServiceDao
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristic
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristic.BluetoothDeviceServiceCharacteristicDao
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptor
import com.ericaskari.w4d5graph.bluetoothdeviceservicecharacteristicdescriptor.BluetoothDeviceServiceCharacteristicDescriptorDao
import com.ericaskari.w4d5graph.bluetoothsearch.BluetoothDevice
import com.ericaskari.w4d5graph.bluetoothsearch.BluetoothDeviceDao
import com.ericaskari.w4d5graph.nordicsemiconductordatabase.BluetoothServiceInfo
import com.ericaskari.w4d5graph.nordicsemiconductordatabase.BluetoothServiceInfoDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author Mohammad Askari
 */
@Database(
    entities = [BluetoothDevice::class, BluetoothDeviceService::class, BluetoothDeviceServiceCharacteristic::class, BluetoothDeviceServiceCharacteristicDescriptor::class, BluetoothServiceInfo::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bluetoothDeviceDao(): BluetoothDeviceDao
    abstract fun bluetoothDeviceServiceDao(): BluetoothDeviceServiceDao
    abstract fun bluetoothDeviceServiceCharacteristicDao(): BluetoothDeviceServiceCharacteristicDao
    abstract fun bluetoothDeviceServiceCharacteristicDescriptorDao(): BluetoothDeviceServiceCharacteristicDescriptorDao
    abstract fun bluetoothServiceInfoDao(): BluetoothServiceInfoDao

    companion object {
        private var DB_NAME: String = "w4d5graph"

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
