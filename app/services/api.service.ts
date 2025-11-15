import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import type { Observable } from "rxjs"
import { map, catchError } from "rxjs/operators"
import { throwError } from "rxjs"
import { environment } from "../../environments/environment"
import type {
  UserRequest,
  UserResponse,
  LoginRequest,
  ForgotPasswordRequest,
  ApiResponse,
  VerifyWithTokenRequest,
  ResetPasswordWithTokenRequest,
  UserUpdateRequest,
  ForgotPasswordResponse,
} from "../models/user.model"
import { AuthService } from "./auth.service"

@Injectable({
  providedIn: "root",
})
export class ApiService {
  private apiUrl = environment.apiUrl

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) {
    console.log("[v0] ApiService inicializado con URL:", this.apiUrl)
  }

  private getHeaders(): HttpHeaders {
    const headers: any = {
      "Content-Type": "application/json",
    }

    const token = this.authService.getToken()
    if (token) {
      headers["Authorization"] = `Bearer ${token}`
    }

    const userId = this.authService.getUserId()
    if (userId) {
      headers["X-User-Id"] = userId
    }

    const userRole = this.authService.getUserRole()
    if (userRole) {
      headers["X-User-Role"] = userRole
    }

    return new HttpHeaders(headers)
  }

  // Usuarios

  // POST /api/register
  registerUser(userData: UserRequest): Observable<UserResponse> {
    const url = `${this.apiUrl}/register`
    console.log('[v0] Registrando usuario:', userData.email)

    return this.http
      .post<ApiResponse<UserResponse>>(url, userData, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          console.log('[v0] Respuesta completa del registro:', response)
          const verificationToken = (response.data as any)?.verificationToken || (response as any).verificationToken
          console.log('[v0] Token de verificación encontrado:', verificationToken)

          return {
            ...response.data,
            message: response.message,
            verificationToken: verificationToken,
          }
        }),
        catchError((error) => {
          console.error('[v0] Error en registro:', error)
          return throwError(() => error)
        }),
      )
  }

  // POST /api/login
  loginUser(credentials: LoginRequest): Observable<UserResponse> {
    const url = `${this.apiUrl}/login`

    return this.http
      .post<ApiResponse<UserResponse>>(url, credentials, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return {
            ...response.data,
            message: response.message,
          }
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  // POST /api/forgot-password
  forgotPassword(request: ForgotPasswordRequest): Observable<ForgotPasswordResponse> {
    const url = `${this.apiUrl}/forgot-password`

    return this.http
      .post<ApiResponse<ForgotPasswordResponse>>(url, request, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return { ...response.data, message: response.message }
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  verifyEmail(request: VerifyWithTokenRequest): Observable<any> {
    const url = `${this.apiUrl}/user/verify`

    return this.http
      .post<ApiResponse<any>>(url, request, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return { message: response.message, data: response.data }
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  resetPassword(request: ResetPasswordWithTokenRequest): Observable<any> {
    const url = `${this.apiUrl}/user/reset-password`

    return this.http
      .post<ApiResponse<any>>(url, request, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return { message: response.message, data: response.data }
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  resendVerificationEmail(request: ForgotPasswordRequest): Observable<UserResponse> {
    const url = `${this.apiUrl}/user/resend-verification`

    return this.http
      .post<ApiResponse<UserResponse>>(url, request, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return { ...response.data, message: response.message }
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  getUserById(id: number): Observable<UserResponse> {
    const url = `${this.apiUrl}/user/${id}`

    return this.http
      .get<ApiResponse<UserResponse>>(url, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response.data
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  getByDocumento(documento: string): Observable<UserResponse> {
    const url = `${this.apiUrl}/user/documento/${documento}`

    return this.http
      .get<ApiResponse<UserResponse>>(url, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response.data
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  // GET /api/users - Solo ADMIN
  getAllUsers(): Observable<UserResponse[]> {
    const url = `${this.apiUrl}/users`

    return this.http
      .get<ApiResponse<UserResponse[]>>(url, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response.data
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  requestEditToken(id: string): Observable<void> {
    const url = `${this.apiUrl}/user/request-edit/${id}`

    return this.http
      .post<ApiResponse<void>>(url, null, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response.data
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  confirmUpdate(token: string, request: UserUpdateRequest): Observable<UserResponse> {
    const url = `${this.apiUrl}/user/confirm-edit?token=${token}`

    return this.http
      .put<ApiResponse<UserResponse>>(url, request, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response.data
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  requestDeleteToken(id: string): Observable<void> {
    const url = `${this.apiUrl}/user/request-delete/${id}`

    return this.http
      .post<ApiResponse<void>>(url, null, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response.data
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }

  confirmDelete(token: string): Observable<void> {
    const url = `${this.apiUrl}/user/confirm-delete?token=${token}`

    return this.http
      .delete<ApiResponse<void>>(url, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((response) => {
          return response.data
        }),
        catchError((error) => {
          return throwError(() => error)
        }),
      )
  }
}
