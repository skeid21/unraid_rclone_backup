// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: service.proto

// Generated files should ignore deprecation warnings
@file:Suppress("DEPRECATION")
package org.temp.unraid.backups;

@kotlin.jvm.JvmName("-initializelistBackupsRequest")
public inline fun listBackupsRequest(block: org.temp.unraid.backups.ListBackupsRequestKt.Dsl.() -> kotlin.Unit): org.temp.unraid.backups.BackupServiceProto.ListBackupsRequest =
  org.temp.unraid.backups.ListBackupsRequestKt.Dsl._create(org.temp.unraid.backups.BackupServiceProto.ListBackupsRequest.newBuilder()).apply { block() }._build()
/**
 * Protobuf type `ListBackupsRequest`
 */
public object ListBackupsRequestKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  public class Dsl private constructor(
    private val _builder: org.temp.unraid.backups.BackupServiceProto.ListBackupsRequest.Builder
  ) {
    public companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: org.temp.unraid.backups.BackupServiceProto.ListBackupsRequest.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): org.temp.unraid.backups.BackupServiceProto.ListBackupsRequest = _builder.build()
  }
}
@kotlin.jvm.JvmSynthetic
public inline fun org.temp.unraid.backups.BackupServiceProto.ListBackupsRequest.copy(block: org.temp.unraid.backups.ListBackupsRequestKt.Dsl.() -> kotlin.Unit): org.temp.unraid.backups.BackupServiceProto.ListBackupsRequest =
  org.temp.unraid.backups.ListBackupsRequestKt.Dsl._create(this.toBuilder()).apply { block() }._build()

