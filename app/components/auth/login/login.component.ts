import { Component, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core"
import { CommonModule } from "@angular/common"
import { RouterModule, Router } from "@angular/router"
import { FormsModule } from "@angular/forms"
import { ApiService } from "../../../services/api.service"
import { AuthService } from "../../../services/auth.service"
import type { LoginRequest } from "../../../models/user.model"
import Swal from "sweetalert2"

@Component({
  selector: "app-login",
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: "./login.component.html",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class LoginComponent {
  email = ""
  password = ""
  emailError = ""
  passwordError = ""
  isLoading = false
  errorMessage = ""

  private emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

  constructor(
    private apiService: ApiService,
    private authService: AuthService,
    private router: Router,
  ) {}

  validateEmail() {
    if (!this.email) {
      this.emailError = "El email es obligatorio."
    } else if (!this.emailRegex.test(this.email)) {
      this.emailError = "Formato de email inválido."
    } else {
      this.emailError = ""
    }
  }

  validatePassword() {
    if (!this.password) {
      this.passwordError = "La contraseña es obligatoria."
    } else {
      this.passwordError = ""
    }
  }

  onSubmit() {
    this.validateEmail()
    this.validatePassword()

    if (!this.emailError && !this.passwordError) {
      this.isLoading = true

      const credentials: LoginRequest = {
        email: this.email,
        password: this.password,
      }

      this.apiService.loginUser(credentials).subscribe({
        next: (response) => {
          this.authService.setAuthData(response)

          this.isLoading = false

          const message = response.message || "Inicio de sesión exitoso"

          Swal.fire({
            icon: "success",
            title: "¡Inicio de sesión exitoso!",
            text: message,
            timer: 1500,
            timerProgressBar: true,
            showConfirmButton: false,
          })

          setTimeout(() => {
            this.router.navigate(["/dashboard"])
          }, 1500)
        },
        error: (error: any) => {
          this.isLoading = false

          const errorMsg = error.error?.message || "Verifica tus credenciales."

          const requiresVerification =
            errorMsg.toLowerCase().includes("verifica") ||
            errorMsg.toLowerCase().includes("verificación") ||
            errorMsg.toLowerCase().includes("verificar")

          if (requiresVerification) {
            // Guardar email para poder reenviar código de verificación
            localStorage.setItem('emailToVerify', this.email)

            Swal.fire({
              icon: "warning",
              title: "Email no verificado",
              text: errorMsg,
              showCancelButton: true,
              confirmButtonText: "Verificar email",
              cancelButtonText: "Cancelar",
              confirmButtonColor: "#4F46E5",
            }).then((result) => {
              if (result.isConfirmed) {
                this.router.navigate(["/verify-email"])
              }
            })
          } else {
            Swal.fire({
              icon: "error",
              title: "Error al iniciar sesión",
              text: errorMsg,
            })
          }
        },
      })
    }
  }
}
