import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserSelectionService } from '../../../services/user-selection.service';

@Component({
  selector: 'app-users-selected',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users-selected.component.html',
  styleUrls: ['./users-selected.component.css']
})
export class UsersSelectedComponent implements OnInit {
  selectedUserId: number | null = null;

  constructor(
    private readonly selectionService: UserSelectionService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.selectedUserId = this.selectionService.selectedUserId;
  }

  goToSelected(): void {
    if (this.selectedUserId) {
      this.router.navigate(['/users', this.selectedUserId]);
    }
  }
}
