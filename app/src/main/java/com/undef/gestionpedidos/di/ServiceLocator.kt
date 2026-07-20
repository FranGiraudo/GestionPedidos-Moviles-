package com.undef.gestionpedidos.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.undef.gestionpedidos.data.local.AppDatabase
import com.undef.gestionpedidos.data.local.prefs.UserPreferencesRepository
import com.undef.gestionpedidos.data.remote.ApiService
import com.undef.gestionpedidos.data.repository.CategoryRepository
import com.undef.gestionpedidos.data.repository.ClientRepository
import com.undef.gestionpedidos.data.repository.FinanceRepository
import com.undef.gestionpedidos.data.repository.OrderRepository
import com.undef.gestionpedidos.data.repository.ProductRepository
import com.undef.gestionpedidos.data.repository.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    private var database: AppDatabase? = null
    private var apiService: ApiService? = null

    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var clientRepository: ClientRepository
    lateinit var productRepository: ProductRepository
    lateinit var orderRepository: OrderRepository
    lateinit var financeRepository: FinanceRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var userRepository: UserRepository

    const val SUPABASE_BASE_URL = "https://twlzakbodligncahccpa.supabase.co/"
    const val SUPABASE_ANON_KEY = "sb_publishable_7l7eoaGfMi69gHitD2qzKQ_VZiMSy3v"

    private val supabaseRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SUPABASE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val supabaseApiService: ApiService by lazy {
        supabaseRetrofit.create(ApiService::class.java)
    }

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE orders ADD COLUMN comprobanteUri TEXT")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, nombre TEXT NOT NULL)")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS products_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    nombre TEXT NOT NULL DEFAULT '',
                    codigo TEXT NOT NULL,
                    descripcion TEXT,
                    categoryId INTEGER,
                    unidadMedida TEXT NOT NULL,
                    precioUnitario REAL NOT NULL,
                    stockActual INTEGER NOT NULL,
                    activo INTEGER NOT NULL DEFAULT 1
                )
            """.trimIndent())
            db.execSQL("""
                INSERT INTO products_new (id, nombre, codigo, descripcion, categoryId, unidadMedida, precioUnitario, stockActual, activo)
                SELECT id, codigo, codigo, descripcion, NULL, unidadMedida, precioUnitario, stockActual, activo FROM products
            """.trimIndent())
            db.execSQL("DROP TABLE products")
            db.execSQL("ALTER TABLE products_new RENAME TO products")
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE order_lines ADD COLUMN precioUnitario REAL NOT NULL DEFAULT 0.0")
        }
    }

    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    email TEXT NOT NULL,
                    passwordHash TEXT NOT NULL,
                    fullName TEXT NOT NULL,
                    phone TEXT,
                    role TEXT NOT NULL DEFAULT 'operador',
                    isActive INTEGER NOT NULL DEFAULT 1
                )
            """.trimIndent())
        }
    }

    fun init(context: Context) {
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "gestion_pedidos.db"
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
        .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dolarapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        userPreferencesRepository = UserPreferencesRepository(context)

        val db = database!!
        clientRepository = ClientRepository(db.clientDao(), apiService!!)
        productRepository = ProductRepository(db.productDao())
        orderRepository = OrderRepository(db.orderDao(), db.clientDao(), db.productDao())
        financeRepository = FinanceRepository(apiService!!)
        categoryRepository = CategoryRepository(db.categoryDao(), supabaseApiService)
        userRepository = UserRepository(db.userDao())
    }
}
