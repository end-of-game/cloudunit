import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '@angular/material';

@NgModule({
    imports:        [FormsModule, MaterialModule],
    exports:        [FormsModule, MaterialModule]
})
export class SharedModule { }
