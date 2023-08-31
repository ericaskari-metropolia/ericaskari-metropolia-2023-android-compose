package com.ericaskari.w1d5composelist

import android.util.Log

/* Singleton consept obtained from here
https://medium.com/@adinugroho/singleton-in-kotlin-better-approach-8c5e28a140a5 */

object DataProvider {
    val presidents: MutableList<President> = java.util.ArrayList()
    init {
        Log.d("USR", "This ($this) is a singleton")
        presidents.add(President("Kaarlo Stahlberg", 1919, 1925, "Eka presidentti"))
        presidents.add(President("Lauri Relander", 1925, 1931, "Toka presidentti"))
        presidents.add(President("P. E. Svinhufvud", 1931, 1937, "Kolmas presidentti"))
        presidents.add(President("Kyösti Kallio", 1937, 1940, "Neljas presidentti"))
        presidents.add(President("Risto Ryti", 1940, 1944, "Viides presidentti"))
        presidents.add(President("Carl Gustaf Emil Mannerheim", 1944, 1946, "Kuudes presidentti"))
        presidents.add(President("Juho Kusti Paasikivi", 1946, 1956, "Äkäinen ukko"))
        presidents.add(President("Urho Kekkonen", 1956, 1982, "Pelimies"))
        presidents.add(President("Mauno Koivisto", 1982, 1994, "Manu"))
        presidents.add(President("Martti Ahtisaari", 1994, 2000, "Mahtisaari"))
        presidents.add(President("Tarja Halonen", 2000, 2012, "Eka naispresidentti"))
        presidents.add(President("Sauli Niinistö", 2012, 2024, "Ensimmäisen koiran, Oskun, omistaja"))
        presidents.sortBy { it.name }
    }
}