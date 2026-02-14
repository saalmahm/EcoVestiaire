import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { animate, style, transition, trigger, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatIconModule
  ],
  templateUrl: './register.component.html',
  animations: [
    trigger('pageAnimations', [
      transition(':enter', [
        query('.animate-item', [
          style({ opacity: 0, transform: 'translateY(10px)' }),
          stagger(50, [
            animate('300ms cubic-bezier(0.35, 0, 0.25, 1)', style({ opacity: 1, transform: 'translateY(0)' }))
          ])
        ], { optional: true })
      ])
    ])
  ],
  styles: [`
    :host {
      display: block;
      height: 100vh;
    }
    .custom-input {
      width: 100%;
      padding: 10px 14px;
      border: 1px solid #e5e7eb;
      border-radius: 8px;
      font-size: 14px;
      outline: none;
      transition: border-color 0.2s;
      background-color: white;
      color: #374151;
    }
    .custom-input:focus {
      border-color: #059669;
    }
    .custom-input::placeholder {
      color: #9ca3af;
      opacity: 0.6;
    }
    .error-msg {
      color: #dc2626;
      font-size: 11px;
      height: 14px;
      margin-top: 2px;
    }
  `]
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required, Validators.maxLength(100)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    password: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(100)]]
  });

  errorMessage: string = '';
  isLoading: boolean = false;

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      this.authService.register(this.registerForm.value).subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = "Une erreur est survenue lors de l'inscription.";
          console.error('Register error', err);
        }
      });
    }
  }
}
