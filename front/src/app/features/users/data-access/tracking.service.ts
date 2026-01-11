import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface GeoPoint {
  latitude: number;
  longitude: number;
  altitudeMeters?: number | null;
  accuracyMeters?: number | null;
  headingDegrees?: number | null;
  speedMps?: number | null;
}

export type PositionSource = 'gps' | 'wifi' | 'cell' | 'manual' | 'unknown';

export interface Position {
  id: string;
  userId: number;
  point: GeoPoint;
  source: PositionSource;
  recordedAt: string;
  receivedAt: string;
  metadata?: Record<string, unknown>;
}

export interface PositionListResponse {
  data: Position[];
  pagination: {
    page: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  };
}

export interface PositionQueryParams {
  page?: number;
  pageSize?: number;
  sort?: string;
  from?: string;
  to?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TrackingService {
  private readonly baseUrl = environment.trackingApiBaseUrl || '/tracking';

  constructor(private readonly http: HttpClient) {}

  getLatestPosition(userId: number): Observable<Position> {
    return this.http.get<Position>(`${this.baseUrl}/users/${userId}/positions/latest`);
  }

  getUserPositions(userId: number, params: PositionQueryParams = {}): Observable<PositionListResponse> {
    let httpParams = new HttpParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value === undefined || value === null || value === '') {
        return;
      }
      httpParams = httpParams.set(key, String(value));
    });
    return this.http.get<PositionListResponse>(`${this.baseUrl}/users/${userId}/positions`, {
      params: httpParams
    });
  }
}
