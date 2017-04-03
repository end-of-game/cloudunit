import { MaterialModule } from '@angular/material';
import { SharedModule } from '../shared/shared.module';

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConnectComponent } from './connect.component';
import { FormsModule }        from '@angular/forms';
import { ConnectService }     from './connect.service';

@NgModule({
  imports: [
    CommonModule,
    SharedModule
  ],
  declarations: [ConnectComponent],
  exports: [ ConnectComponent ],
  providers: [ConnectService]
})
export class ConnectModule { }
