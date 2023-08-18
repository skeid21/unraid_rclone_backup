import { Component } from '@angular/core';
import { BackupService } from './backup.service';
import { GetBackupRequest } from 'generated/service_pb';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(private backup: BackupService) {

  }
  title = 'web-client';
  serverResponse='not called yet'

  handleClick(): void {
   let req =  new GetBackupRequest()
    this.backup.client.getBackup(req, (err, resp) => {

    })
   this.serverResponse = "fake click"
  }
}
