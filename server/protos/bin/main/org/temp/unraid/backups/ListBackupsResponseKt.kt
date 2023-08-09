// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: service.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package org.temp.unraid.backups;

@kotlin.jvm.JvmName("-initializelistBackupsResponse")
public inline fun listBackupsResponse(block: org.temp.unraid.backups.ListBackupsResponseKt.Dsl.() -> kotlin.Unit): org.temp.unraid.backups.BackupServiceProto.ListBackupsResponse =
  org.temp.unraid.backups.ListBackupsResponseKt.Dsl._create(org.temp.unraid.backups.BackupServiceProto.ListBackupsResponse.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `ListBackupsResponse`
 */
public object ListBackupsResponseKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: org.temp.unraid.backups.BackupServiceProto.ListBackupsResponse.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: org.temp.unraid.backups.BackupServiceProto.ListBackupsResponse.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): org.temp.unraid.backups.BackupServiceProto.ListBackupsResponse = _builder.build()

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    public class BackupsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * `repeated .Backup backups = 1;`
     */
     public val backups: com.google.protobuf.kotlin.DslList<org.temp.unraid.backups.BackupServiceProto.Backup, BackupsProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getBackupsList()
      )
    /**
     * `repeated .Backup backups = 1;`
     * @param value The backups to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addBackups")
    public fun com.google.protobuf.kotlin.DslList<org.temp.unraid.backups.BackupServiceProto.Backup, BackupsProxy>.add(value: org.temp.unraid.backups.BackupServiceProto.Backup) {
      _builder.addBackups(value)
    }
    /**
     * `repeated .Backup backups = 1;`
     * @param value The backups to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignBackups")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<org.temp.unraid.backups.BackupServiceProto.Backup, BackupsProxy>.plusAssign(value: org.temp.unraid.backups.BackupServiceProto.Backup) {
      add(value)
    }
    /**
     * `repeated .Backup backups = 1;`
     * @param values The backups to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllBackups")
    public fun com.google.protobuf.kotlin.DslList<org.temp.unraid.backups.BackupServiceProto.Backup, BackupsProxy>.addAll(values: kotlin.collections.Iterable<org.temp.unraid.backups.BackupServiceProto.Backup>) {
      _builder.addAllBackups(values)
    }
    /**
     * `repeated .Backup backups = 1;`
     * @param values The backups to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllBackups")
    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun com.google.protobuf.kotlin.DslList<org.temp.unraid.backups.BackupServiceProto.Backup, BackupsProxy>.plusAssign(values: kotlin.collections.Iterable<org.temp.unraid.backups.BackupServiceProto.Backup>) {
      addAll(values)
    }
    /**
     * `repeated .Backup backups = 1;`
     * @param index The index to set the value at.
     * @param value The backups to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setBackups")
    public operator fun com.google.protobuf.kotlin.DslList<org.temp.unraid.backups.BackupServiceProto.Backup, BackupsProxy>.set(index: kotlin.Int, value: org.temp.unraid.backups.BackupServiceProto.Backup) {
      _builder.setBackups(index, value)
    }
    /**
     * `repeated .Backup backups = 1;`
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearBackups")
    public fun com.google.protobuf.kotlin.DslList<org.temp.unraid.backups.BackupServiceProto.Backup, BackupsProxy>.clear() {
      _builder.clearBackups()
    }

  }
}
@kotlin.jvm.JvmSynthetic
public inline fun org.temp.unraid.backups.BackupServiceProto.ListBackupsResponse.copy(block: org.temp.unraid.backups.ListBackupsResponseKt.Dsl.() -> kotlin.Unit): org.temp.unraid.backups.BackupServiceProto.ListBackupsResponse =
  org.temp.unraid.backups.ListBackupsResponseKt.Dsl._create(this.toBuilder()).apply { block() }._build()
