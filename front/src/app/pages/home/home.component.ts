import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  message = '';
  error = '';
  loading = false;

  constructor(private readonly apiService: ApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.apiService.getHello().subscribe({
      next: (data) => {
        this.message = data.message;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to reach backend.';
        this.loading = false;
      }
    });
  }
}
