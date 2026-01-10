import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserSelectionService {
  private readonly selectedUserIdSubject = new BehaviorSubject<number | null>(null);

  get selectedUserId$(): Observable<number | null> {
    return this.selectedUserIdSubject.asObservable();
  }

  get selectedUserId(): number | null {
    return this.selectedUserIdSubject.value;
  }

  setSelectedUserId(userId: number | null): void {
    this.selectedUserIdSubject.next(userId);
  }
}
