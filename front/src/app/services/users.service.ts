import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type UserStatus = 'active' | 'inactive' | 'blocked';

export interface Pagination {
  page: number;
  pageSize: number;
  totalItems: number;
  totalPages: number;
}

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  status: UserStatus;
  phone?: string | null;
  metadata?: Record<string, unknown>;
  createdAt: string;
  updatedAt: string;
}

export interface UserListResponse {
  items: User[];
  pagination: Pagination;
}

export interface UserCreate {
  email: string;
  firstName: string;
  lastName: string;
  role?: string;
  phone?: string | null;
  metadata?: Record<string, unknown>;
}

export interface UserUpdate {
  email?: string;
  firstName?: string;
  lastName?: string;
  role?: string;
  status?: UserStatus;
  phone?: string | null;
  metadata?: Record<string, unknown>;
}

export interface UserQueryParams {
  page?: number;
  pageSize?: number;
  sort?: string;
  status?: UserStatus;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsersService {
  private readonly baseUrl = `${environment.apiBaseUrl}/users`;

  constructor(private readonly http: HttpClient) {}

  getUsers(params: UserQueryParams = {}): Observable<UserListResponse> {
    const httpParams = this.buildParams(params);
    return this.http.get<UserListResponse>(this.baseUrl, { params: httpParams });
  }

  searchUsers(query: string, params: UserQueryParams = {}): Observable<UserListResponse> {
    const httpParams = this.buildParams({ ...params, q: query });
    return this.http.get<UserListResponse>(`${this.baseUrl}/search`, { params: httpParams });
  }

  getUser(userId: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/${userId}`);
  }

  createUser(payload: UserCreate): Observable<User> {
    return this.http.post<User>(this.baseUrl, payload);
  }

  updateUser(userId: number, payload: UserUpdate): Observable<User> {
    return this.http.patch<User>(`${this.baseUrl}/${userId}`, payload);
  }

  deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${userId}`);
  }

  private buildParams(params: UserQueryParams & { q?: string }): HttpParams {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value === undefined || value === null || value === '') {
        return;
      }
      httpParams = httpParams.set(key, String(value));
    });
    return httpParams;
  }
}
