import {AfterViewInit, Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, inject, ViewChild} from '@angular/core';
import 'deep-chat';
import {RequestDetails} from 'deep-chat/dist/types/interceptors';
import {OAuthService} from 'angular-oauth2-oidc';

@Component({
  selector: 'app-chat',
  imports: [],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ChatComponent implements AfterViewInit {
  private authService = inject(OAuthService);

  oauthService = inject(OAuthService);

  @ViewChild('deepChat', {static: true}) chatElementRef!: ElementRef;

  ngAfterViewInit(): void {
    //this.chatElementRef = document.getElementById('meinChatElement') as DeepChat;

    // Asynchroner requestInterceptor
    this.chatElementRef.nativeElement.requestInterceptor = async (requestDetails: RequestDetails) => {
      console.log('RequestInterceptor: Ursprüngliche Request Details:', requestDetails);
      try {
        const localToken = localStorage.getItem('auth_token');
        const token = localToken || this.oauthService.getIdToken();

        if (token) {
          requestDetails.headers = requestDetails.headers || {};
          requestDetails.headers['Authorization'] = `Bearer ${token}`;
          console.log('RequestInterceptor: Authorization Header hinzugefügt.');
        } else {
          console.warn('RequestInterceptor: Kein Token vom AuthService erhalten.');
          // Optional: Anfrage abbrechen, wenn kein Token vorhanden ist
          // return { error: 'Authentifizierungstoken fehlt.' };
        }
        return requestDetails;
      } catch (error) {
        console.error('Fehler beim Abrufen des Tokens im requestInterceptor:', error);
        // Anfrage bei Fehler abbrechen
        return {error: 'Fehler bei der Token-Beschaffung.'};
      }
    };
  }
}
