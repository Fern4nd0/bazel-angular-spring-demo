import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { AboutComponent } from './pages/about/about.component';
import { UsersLayoutComponent } from './pages/users/users-layout/users-layout.component';
import { UsersListComponent } from './pages/users/users-list/users-list.component';
import { UsersSearchComponent } from './pages/users/users-search/users-search.component';
import { UsersCreateComponent } from './pages/users/users-create/users-create.component';
import { UsersDetailComponent } from './pages/users/users-detail/users-detail.component';
import { UsersSelectedComponent } from './pages/users/users-selected/users-selected.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'home' },
  { path: 'home', component: HomeComponent },
  { path: 'about', component: AboutComponent },
  {
    path: 'users',
    component: UsersLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'list' },
      { path: 'list', component: UsersListComponent },
      { path: 'search', component: UsersSearchComponent },
      { path: 'create', component: UsersCreateComponent },
      { path: 'selected', component: UsersSelectedComponent },
      { path: ':userId', component: UsersDetailComponent }
    ]
  },
  { path: '**', redirectTo: 'home' }
];
