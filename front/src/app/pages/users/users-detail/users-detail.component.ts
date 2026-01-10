import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserSelectionService } from '../../../services/user-selection.service';
import { User, UsersService } from '../../../services/users.service';

@Component({
  selector: 'app-users-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-detail.component.html',
  styleUrls: ['./users-detail.component.css']
})
export class UsersDetailComponent implements OnInit {
  user?: User;
  loading = false;
  error = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly usersService: UsersService,
    private readonly selectionService: UserSelectionService
  ) {}

  ngOnInit(): void {
    const userId = Number(this.route.snapshot.paramMap.get('userId'));
    if (!Number.isFinite(userId)) {
      this.error = 'No se encontro el usuario seleccionado.';
      return;
    }
    this.selectionService.setSelectedUserId(userId);
    this.fetchUser(userId);
  }

  private fetchUser(userId: number): void {
    this.loading = true;
    this.error = '';
    this.usersService.getUser(userId).subscribe({
      next: (user) => {
        this.user = user;
        this.loading = false;
      },
      error: () => {
        this.error = 'No se pudo cargar el usuario.';
        this.loading = false;
      }
    });
  }
}
