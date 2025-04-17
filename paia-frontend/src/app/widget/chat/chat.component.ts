import {Component, CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import 'deep-chat';

@Component({
  selector: 'app-chat',
  imports: [],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ChatComponent {

}
