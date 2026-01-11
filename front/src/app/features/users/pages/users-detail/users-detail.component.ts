import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserSelectionService } from '../../data-access/user-selection.service';
import { Position, PositionListResponse, TrackingService } from '../../data-access/tracking.service';
import { User, UsersService } from '../../data-access/users.service';

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
  latestPosition?: Position;
  latestLoading = false;
  latestError = '';
  historyLoading = false;
  historyError = '';
  positionHistory: Position[] = [];
  historyPagination?: PositionListResponse['pagination'];
  historyPage = 1;
  historyPageSize = 15;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly usersService: UsersService,
    private readonly selectionService: UserSelectionService,
    private readonly trackingService: TrackingService
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
        this.loadLatestPosition();
        this.loadPositionHistory();
      },
      error: () => {
        this.error = 'No se pudo cargar el usuario.';
        this.loading = false;
      }
    });
  }

  loadLatestPosition(): void {
    if (!this.user?.id) {
      return;
    }
    this.latestLoading = true;
    this.latestError = '';
    this.trackingService.getLatestPosition(this.user.id).subscribe({
      next: (position) => {
        this.latestPosition = position;
        this.latestLoading = false;
      },
      error: () => {
        this.latestError = 'No se pudo cargar la ultima posicion.';
        this.latestLoading = false;
      }
    });
  }

  loadPositionHistory(): void {
    if (!this.user?.id) {
      return;
    }
    this.historyLoading = true;
    this.historyError = '';
    this.trackingService
      .getUserPositions(this.user.id, {
        page: this.historyPage,
        pageSize: this.historyPageSize,
        sort: 'recordedAt:desc'
      })
      .subscribe({
        next: (response) => {
          this.positionHistory = response.data;
          this.historyPagination = response.pagination;
          this.historyLoading = false;
        },
        error: () => {
          this.historyError = 'No se pudo cargar el historial.';
          this.historyLoading = false;
        }
      });
  }

  nextHistoryPage(): void {
    if (this.historyPagination && this.historyPage < this.historyPagination.totalPages) {
      this.historyPage += 1;
      this.loadPositionHistory();
    }
  }

  prevHistoryPage(): void {
    if (this.historyPage > 1) {
      this.historyPage -= 1;
      this.loadPositionHistory();
    }
  }
}
