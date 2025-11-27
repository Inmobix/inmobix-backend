import { Injectable } from "@angular/core"
import { BehaviorSubject } from "rxjs"
import type { UserResponse } from "../models/user.model"

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private readonly TOKEN_KEY = "inmobix_token"
  private readonly USER_KEY = "inmobix_user"
  private readonly USER_ID_KEY = "inmobix_user_id"
  private readonly USER_ROLE_KEY = "inmobix_user_role"

  private currentUserSubject = new BehaviorSubject<UserResponse | null>(this.getUserFromStorage())
  public currentUser$ = this.currentUserSubject.asObservable()

  constructor() {}

  setAuthData(user: UserResponse): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user))
    localStorage.setItem(this.USER_ID_KEY, user.id)
    if (user.role) {
      localStorage.setItem(this.USER_ROLE_KEY, user.role)
    }
    this.currentUserSubject.next(user)
  }

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token)
  }

  // Obtener token
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY)
  }

  // Obtener usuario actual
  getCurrentUser(): UserResponse | null {
    return this.currentUserSubject.value
  }

  // Obtener user ID
  getUserId(): string | null {
    return localStorage.getItem(this.USER_ID_KEY)
  }

  // Obtener role
  getUserRole(): string | null {
    return localStorage.getItem(this.USER_ROLE_KEY)
  }

  isAuthenticated(): boolean {
    return !!this.getCurrentUser() || !!localStorage.getItem(this.USER_KEY)
  }

  // Cerrar sesión
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY)
    localStorage.removeItem(this.USER_KEY)
    localStorage.removeItem(this.USER_ID_KEY)
    localStorage.removeItem(this.USER_ROLE_KEY)
    this.currentUserSubject.next(null)
  }

  // Obtener usuario del storage
  private getUserFromStorage(): UserResponse | null {
    const userJson = localStorage.getItem(this.USER_KEY)
    if (userJson) {
      try {
        return JSON.parse(userJson)
      } catch {
        return null
      }
    }
    return null
  }
}
