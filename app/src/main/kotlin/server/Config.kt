package server

import java.util.Properties

val config =
    Properties().apply {
      load(ClassLoader.getSystemClassLoader().getResourceAsStream("config.properties"))
      // allow command line jvm args to override
      keys.forEach { key ->
        System.getProperty(key as String)?.let { value -> setProperty(key, value) }
      }
    }

val Properties.databaseFilePath: String
  get() = this.getProperty("application.sqlite.data_path")!!
