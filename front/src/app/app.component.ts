import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav>
      <a routerLink="/home" routerLinkActive="active">Home</a>
      <a routerLink="/users" routerLinkActive="active">Usuarios</a>
      <a routerLink="/about" routerLinkActive="active">About</a>
    </nav>
    <main>
      <router-outlet></router-outlet>
    </main>
  `
})
export class AppComponent {}
