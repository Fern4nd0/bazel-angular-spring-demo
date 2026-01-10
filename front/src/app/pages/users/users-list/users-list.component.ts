import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserSelectionService } from '../../../services/user-selection.service';
import { User, UserListResponse, UserQueryParams, UsersService, UserStatus } from '../../../services/users.service';

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.css']
})
export class UsersListComponent implements OnInit {
  users: User[] = [];
  pagination?: UserListResponse['pagination'];
  loading = false;
  error = '';

  page = 1;
  pageSize = 10;
  sort = 'createdAt,desc';
  status: UserStatus | '' = '';
  role = '';

  constructor(
    private readonly usersService: UsersService,
    private readonly selectionService: UserSelectionService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers(): void {
    this.loading = true;
    this.error = '';
    const statusValue: UserStatus | undefined = this.status === '' ? undefined : this.status;
    const roleValue = this.role.trim() === '' ? undefined : this.role.trim();
    const params: UserQueryParams = {
      page: this.page,
      pageSize: this.pageSize,
      sort: this.sort || undefined,
      status: statusValue,
      role: roleValue
    };
    this.usersService.getUsers(params).subscribe({
      next: (response) => {
        this.users = response.items;
        this.pagination = response.pagination;
        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudo cargar el listado.';
        this.loading = false;
      }
    });
  }

  goToUser(userId: number): void {
    this.selectionService.setSelectedUserId(userId);
    this.router.navigate(['/users', userId]);
  }

  nextPage(): void {
    if (this.pagination && this.page < this.pagination.totalPages) {
      this.page += 1;
      this.fetchUsers();
    }
  }

  prevPage(): void {
    if (this.page > 1) {
      this.page -= 1;
      this.fetchUsers();
    }
  }
}
