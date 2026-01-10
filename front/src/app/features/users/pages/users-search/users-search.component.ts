import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserSelectionService } from '../../data-access/user-selection.service';
import { User, UserListResponse, UsersService } from '../../data-access/users.service';

@Component({
  selector: 'app-users-search',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-search.component.html',
  styleUrls: ['./users-search.component.css']
})
export class UsersSearchComponent {
  query = '';
  users: User[] = [];
  pagination?: UserListResponse['pagination'];
  loading = false;
  error = '';

  constructor(
    private readonly usersService: UsersService,
    private readonly selectionService: UserSelectionService,
    private readonly router: Router
  ) {}

  search(): void {
    const trimmed = this.query.trim();
    if (trimmed.length < 2) {
      this.error = 'Ingresa al menos 2 caracteres para buscar.';
      this.users = [];
      return;
    }
    this.loading = true;
    this.error = '';
    this.usersService.searchUsers(trimmed, { page: 1, pageSize: 10 }).subscribe({
      next: (response) => {
        this.users = response.items;
        this.pagination = response.pagination;
        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudo completar la busqueda.';
        this.loading = false;
      }
    });
  }

  goToUser(userId: number): void {
    this.selectionService.setSelectedUserId(userId);
    this.router.navigate(['/users', userId]);
  }
}
