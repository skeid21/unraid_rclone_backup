package server

import java.util.Properties

val config =
    Properties().apply {
      //		load(javaClass.classLoader.getResourceAsStream("config.properties"))
      load(ClassLoader.getSystemClassLoader().getResourceAsStream("config.properties"))
      // allow comamnd line jvm args to override
      keys.forEach { key ->
        System.getProperty(key as String)?.let { value -> setProperty(key, value) }
      }
    }

val Properties.databaseFilePath: String
  get() = this.getProperty("application.sqlite.data_path")!!
