// Request para registro
export interface UserRequest {
  name: string
  email: string
  username: string
  password: string
  phone?: string
  birthDate?: string
  documento?: string
}

// Request para login
export interface LoginRequest {
  email: string
  password: string
}

// Request para recuperar contraseña
export interface ForgotPasswordRequest {
  email: string
}

export interface VerifyWithTokenRequest {
  verificationToken: string
  code: string
}

export interface ResetPasswordWithTokenRequest {
  resetPasswordToken: string
  code: string
  newPassword: string
}

export interface UserUpdateRequest {
  name?: string
  username?: string
  phone?: string
  birthDate?: string
  documento?: string
}

// Response del backend
export interface UserResponse {
  id: string
  name: string
  email: string
  username: string
  phone?: string
  birthDate?: string
  documento?: string
  role?: string
  token?: string
  verificationToken?: string
  resetPasswordToken?: string
  message?: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

// Response de login
export interface LoginResponse {
  message: string
  token?: string
}

export interface ForgotPasswordResponse {
  message: string
  resetPasswordToken?: string
}
