import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserSelectionService } from '../../../services/user-selection.service';
import { UserCreate, UsersService } from '../../../services/users.service';

@Component({
  selector: 'app-users-create',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-create.component.html',
  styleUrls: ['./users-create.component.css']
})
export class UsersCreateComponent {
  email = '';
  firstName = '';
  lastName = '';
  role = 'user';
  phone = '';
  loading = false;
  error = '';
  success = '';

  constructor(
    private readonly usersService: UsersService,
    private readonly selectionService: UserSelectionService,
    private readonly router: Router
  ) {}

  submit(): void {
    this.loading = true;
    this.error = '';
    this.success = '';
    const payload: UserCreate = {
      email: this.email.trim(),
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      role: this.role?.trim() || 'user',
      phone: this.phone?.trim() || undefined
    };
    this.usersService.createUser(payload).subscribe({
      next: (user) => {
        this.loading = false;
        this.success = 'Usuario creado correctamente.';
        this.selectionService.setSelectedUserId(user.id);
        this.router.navigate(['/users', user.id]);
      },
      error: () => {
        this.loading = false;
        this.error = 'No se pudo crear el usuario.';
      }
    });
  }
}
