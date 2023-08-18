import { Injectable } from '@angular/core';
import { BackupServiceClient } from 'generated/service_pb_service';

@Injectable({
  providedIn: 'root'
})
export class BackupService {
  client: BackupServiceClient = new BackupServiceClient('http://localhost:8080')
  constructor() { }
}
