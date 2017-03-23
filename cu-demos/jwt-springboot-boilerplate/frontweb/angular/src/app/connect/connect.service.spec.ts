/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { ConnectService } from './connect.service';

describe('Service: Connect', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ConnectService]
    });
  });

  it('should ...', inject([ConnectService], (service: ConnectService) => {
    expect(service).toBeTruthy();
  }));
});
