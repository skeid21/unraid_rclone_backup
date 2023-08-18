import { TestBed } from '@angular/core/testing';

import { BackupService } from './backup.service';

describe('BackupServiceService', () => {
  let service: BackupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BackupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
