import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, LoginRequest, RegisterRequest } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, 
    MatCardModule, 
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatTabsModule,
    MatIconModule,
    MatSnackBarModule,
    FormsModule,
    ReactiveFormsModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  registerForm: FormGroup;
  hidePassword = true;
  isLoading = false;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.checkPasswords });
  }

  checkPasswords(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  loginWithGoogle(): void {
    this.isLoading = true;
    this.authService.loginWithGoogle();
  }

  onLogin(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      const request: LoginRequest = {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password
      };

      this.authService.login(request).subscribe({
        next: (response) => {
          console.log('Login erfolgreich, warte auf Weiterleitung...');
          this.isLoading = false;
          // Keine Weiterleitung, da der AuthService das jetzt übernimmt
        },
        error: (error) => {
          console.error('Login fehlgeschlagen:', error);
          this.isLoading = false;
          this.snackBar.open(`Login fehlgeschlagen: ${error.message || 'Unbekannter Fehler'}`, 'OK', {
            duration: 5000
          });
        }
      });
    }
  }

  onRegister(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      const request: RegisterRequest = {
        name: this.registerForm.value.name,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password
      };

      this.authService.register(request).subscribe({
        next: (response) => {
          console.log('Registrierung erfolgreich, warte auf Weiterleitung...');
          this.isLoading = false;
          // Keine Weiterleitung, da der AuthService das jetzt übernimmt
        },
        error: (error) => {
          console.error('Registrierung fehlgeschlagen:', error);
          this.isLoading = false;
          this.snackBar.open(`Registrierung fehlgeschlagen: ${error.message || 'Unbekannter Fehler'}`, 'OK', {
            duration: 5000
          });
        }
      });
    }
  }
}
