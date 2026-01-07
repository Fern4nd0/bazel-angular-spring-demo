import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface HelloResponse {
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  getHello(): Observable<HelloResponse> {
    return this.http.get<HelloResponse>(`${environment.apiBaseUrl}/hello`);
  }
}
