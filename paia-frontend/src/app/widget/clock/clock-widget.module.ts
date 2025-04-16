import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClockWidgetComponent } from './clock-widget.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ClockWidgetComponent
  ],
  exports: [
    ClockWidgetComponent
  ]
})
export class ClockWidgetModule { } 