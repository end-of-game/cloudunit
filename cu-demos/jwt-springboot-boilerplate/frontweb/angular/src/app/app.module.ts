import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { CoreModule } from './core/core.module';
import { SharedModule } from './shared/shared.module';
import { AppComponent } from './app.component';
import { ConnectModule } from './connect/connect.module';
import { ConnectComponent } from './connect/connect.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    CoreModule,
    BrowserModule,
    SharedModule,
    ConnectModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
