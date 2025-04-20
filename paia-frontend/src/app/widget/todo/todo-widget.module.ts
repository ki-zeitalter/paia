import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TodoWidgetComponent } from './todo-widget.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    TodoWidgetComponent
  ],
  exports: [
    TodoWidgetComponent
  ]
})
export class TodoWidgetModule { } 