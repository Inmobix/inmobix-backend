import { Component, type OnInit, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core"
import { CommonModule } from "@angular/common"
import { RouterModule, Router } from "@angular/router"
import { FormsModule } from "@angular/forms"
import { ApiService } from "../../../services/api.service"
import type { ResetPasswordWithTokenRequest } from "../../../models/user.model"
import Swal from "sweetalert2"

@Component({
  selector: "app-reset-password",
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: "./reset-password.component.html",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ResetPasswordComponent implements OnInit {
  password = ""
  confirmPassword = ""
  code = ""
  resetPasswordToken = ""
  passwordInvalid = false
  confirmPasswordInvalid = false
  codeInvalid = false
  isLoading = false
  showPassword = false
  showConfirmPassword = false

  constructor(
    private apiService: ApiService,
    private router: Router,
  ) {}

  ngOnInit() {
    this.resetPasswordToken = localStorage.getItem("resetPasswordToken") || ""

    if (!this.resetPasswordToken) {
      Swal.fire({
        icon: "error",
        title: "Token no encontrado",
        text: "No se encontró un token de recuperación. Por favor, solicita uno nuevamente.",
      }).then(() => {
        this.router.navigate(["/forgot-password"])
      })
    }
  }

  validatePassword() {
    if (!this.password.trim()) {
      this.passwordInvalid = true
      return
    }

    this.passwordInvalid = this.password.length < 8
  }

  validateConfirmPassword() {
    if (!this.confirmPassword.trim()) {
      this.confirmPasswordInvalid = true
      return
    }

    this.confirmPasswordInvalid = this.password !== this.confirmPassword
  }

  validateCode() {
    if (!this.code.trim()) {
      this.codeInvalid = true
      return
    }

    this.codeInvalid = this.code.length !== 6
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword
  }

  onSubmit() {
    this.validatePassword()
    this.validateConfirmPassword()
    this.validateCode()

    if (this.passwordInvalid || this.confirmPasswordInvalid || this.codeInvalid) return

    this.isLoading = true

    const request: ResetPasswordWithTokenRequest = {
      resetPasswordToken: this.resetPasswordToken,
      code: this.code,
      newPassword: this.password,
    }

    this.apiService.resetPassword(request).subscribe({
      next: (response) => {
        this.isLoading = false

        localStorage.removeItem("resetPasswordToken")

        const message = response.message || "Contraseña restablecida exitosamente."

        Swal.fire({
          icon: "success",
          title: "¡Contraseña restablecida!",
          text: message,
          timer: 2000,
          timerProgressBar: true,
          showConfirmButton: false,
        }).then(() => {
          this.router.navigate(["/login"])
        })
      },
      error: (error) => {
        this.isLoading = false

        const errorMsg =
          error.error?.message || "No se pudo restablecer la contraseña. El token o código pueden haber expirado."

        Swal.fire({
          icon: "error",
          title: "Error",
          text: errorMsg,
        })
      },
    })
  }
}
