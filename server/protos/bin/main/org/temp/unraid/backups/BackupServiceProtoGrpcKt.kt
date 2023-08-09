package org.temp.unraid.backups

import com.google.protobuf.Empty
import io.grpc.CallOptions
import io.grpc.CallOptions.DEFAULT
import io.grpc.Channel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerServiceDefinition.builder
import io.grpc.ServiceDescriptor
import io.grpc.Status
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineServerImpl
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.kotlin.ClientCalls
import io.grpc.kotlin.ClientCalls.unaryRpc
import io.grpc.kotlin.ServerCalls
import io.grpc.kotlin.ServerCalls.unaryServerMethodDefinition
import io.grpc.kotlin.StubFor
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import org.temp.unraid.backups.BackupServiceGrpc.getServiceDescriptor

/**
 * Holder for Kotlin coroutine-based client and server APIs for BackupService.
 */
public object BackupServiceGrpcKt {
  public const val SERVICE_NAME: String = BackupServiceGrpc.SERVICE_NAME

  @JvmStatic
  public val serviceDescriptor: ServiceDescriptor
    get() = BackupServiceGrpc.getServiceDescriptor()

  public val createBackupMethod:
      MethodDescriptor<BackupServiceProto.CreateBackupRequest, BackupServiceProto.Backup>
    @JvmStatic
    get() = BackupServiceGrpc.getCreateBackupMethod()

  public val getBackupMethod:
      MethodDescriptor<BackupServiceProto.GetBackupRequest, BackupServiceProto.Backup>
    @JvmStatic
    get() = BackupServiceGrpc.getGetBackupMethod()

  public val listBackupMethod:
      MethodDescriptor<BackupServiceProto.ListBackupsRequest, BackupServiceProto.ListBackupsResponse>
    @JvmStatic
    get() = BackupServiceGrpc.getListBackupMethod()

  public val deleteBackupMethod: MethodDescriptor<BackupServiceProto.DeleteBackupRequest, Empty>
    @JvmStatic
    get() = BackupServiceGrpc.getDeleteBackupMethod()

  /**
   * A stub for issuing RPCs to a(n) BackupService service as suspending coroutines.
   */
  @StubFor(BackupServiceGrpc::class)
  public class BackupServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT,
  ) : AbstractCoroutineStub<BackupServiceCoroutineStub>(channel, callOptions) {
    public override fun build(channel: Channel, callOptions: CallOptions):
        BackupServiceCoroutineStub = BackupServiceCoroutineStub(channel, callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun createBackup(request: BackupServiceProto.CreateBackupRequest,
        headers: Metadata = Metadata()): BackupServiceProto.Backup = unaryRpc(
      channel,
      BackupServiceGrpc.getCreateBackupMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun getBackup(request: BackupServiceProto.GetBackupRequest, headers: Metadata =
        Metadata()): BackupServiceProto.Backup = unaryRpc(
      channel,
      BackupServiceGrpc.getGetBackupMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun listBackup(request: BackupServiceProto.ListBackupsRequest, headers: Metadata
        = Metadata()): BackupServiceProto.ListBackupsResponse = unaryRpc(
      channel,
      BackupServiceGrpc.getListBackupMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun deleteBackup(request: BackupServiceProto.DeleteBackupRequest,
        headers: Metadata = Metadata()): Empty = unaryRpc(
      channel,
      BackupServiceGrpc.getDeleteBackupMethod(),
      request,
      callOptions,
      headers
    )
  }

  /**
   * Skeletal implementation of the BackupService service based on Kotlin coroutines.
   */
  public abstract class BackupServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for BackupService.createBackup.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun createBackup(request: BackupServiceProto.CreateBackupRequest):
        BackupServiceProto.Backup = throw
        StatusException(UNIMPLEMENTED.withDescription("Method BackupService.createBackup is unimplemented"))

    /**
     * Returns the response to an RPC for BackupService.getBackup.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun getBackup(request: BackupServiceProto.GetBackupRequest):
        BackupServiceProto.Backup = throw
        StatusException(UNIMPLEMENTED.withDescription("Method BackupService.getBackup is unimplemented"))

    /**
     * Returns the response to an RPC for BackupService.listBackup.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun listBackup(request: BackupServiceProto.ListBackupsRequest):
        BackupServiceProto.ListBackupsResponse = throw
        StatusException(UNIMPLEMENTED.withDescription("Method BackupService.listBackup is unimplemented"))

    /**
     * Returns the response to an RPC for BackupService.deleteBackup.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun deleteBackup(request: BackupServiceProto.DeleteBackupRequest): Empty =
        throw
        StatusException(UNIMPLEMENTED.withDescription("Method BackupService.deleteBackup is unimplemented"))

    public final override fun bindService(): ServerServiceDefinition =
        builder(getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = BackupServiceGrpc.getCreateBackupMethod(),
      implementation = ::createBackup
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = BackupServiceGrpc.getGetBackupMethod(),
      implementation = ::getBackup
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = BackupServiceGrpc.getListBackupMethod(),
      implementation = ::listBackup
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = BackupServiceGrpc.getDeleteBackupMethod(),
      implementation = ::deleteBackup
    )).build()
  }
}
