import { Routes } from '@angular/router';
import { UsersLayoutComponent } from './pages/users-layout/users-layout.component';
import { UsersListComponent } from './pages/users-list/users-list.component';
import { UsersSearchComponent } from './pages/users-search/users-search.component';
import { UsersCreateComponent } from './pages/users-create/users-create.component';
import { UsersDetailComponent } from './pages/users-detail/users-detail.component';
import { UsersSelectedComponent } from './pages/users-selected/users-selected.component';

export const usersRoutes: Routes = [
  {
    path: '',
    component: UsersLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'list' },
      { path: 'list', component: UsersListComponent },
      { path: 'search', component: UsersSearchComponent },
      { path: 'create', component: UsersCreateComponent },
      { path: 'selected', component: UsersSelectedComponent },
      { path: ':userId', component: UsersDetailComponent }
    ]
  }
];
