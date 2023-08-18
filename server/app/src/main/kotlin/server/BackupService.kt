package server

/**
 * Implementation of the backup service
 */
class BackupService : BackupServiceGrpcKt.BackupServiceCoroutineImplBase() {
    override suspend fun getBackup(request: Service.GetBackupRequest): Service.Backup {
        return backup {
            name = "Test"
        }
    }
}
