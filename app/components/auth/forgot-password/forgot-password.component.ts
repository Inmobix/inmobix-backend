import { Component, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core"
import { CommonModule } from "@angular/common"
import { RouterModule, Router } from "@angular/router"
import { FormsModule } from "@angular/forms"
import { ApiService } from "../../../services/api.service"
import type { ForgotPasswordRequest } from "../../../models/user.model"
import Swal from "sweetalert2"

@Component({
  selector: "app-forgot-password",
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: "./forgot-password.component.html",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class ForgotPasswordComponent {
  email = ""
  emailInvalid = false
  isLoading = false
  successMessage = ""
  errorMessage = ""

  constructor(
    private apiService: ApiService,
    private router: Router,
  ) {}

  validateEmail() {
    if (!this.email.trim()) {
      this.emailInvalid = true
      return
    }

    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    this.emailInvalid = !pattern.test(this.email)
  }

  onSubmit() {
    this.validateEmail()
    this.successMessage = ""
    this.errorMessage = ""

    if (this.emailInvalid) return

    this.isLoading = true

    const request: ForgotPasswordRequest = {
      email: this.email,
    }

    this.apiService.forgotPassword(request).subscribe({
      next: (response) => {
        this.isLoading = false
        this.email = ""

        if (response.resetPasswordToken) {
          localStorage.setItem("resetPasswordToken", response.resetPasswordToken)
        }

        const message = response.message || "Revisa tu bandeja de entrada para recuperar tu contraseña."

        Swal.fire({
          icon: "success",
          title: "¡Correo enviado!",
          text: message,
          timer: 3000,
          timerProgressBar: true,
          showConfirmButton: false,
        }).then(() => {
          this.router.navigate(["/reset-password"])
        })

        setTimeout(() => {
          this.router.navigate(["/reset-password"])
        }, 3000)
      },
      error: (error) => {
        this.isLoading = false

        const errorMsg = error.error?.message || "No se pudo enviar el correo de recuperación."

        Swal.fire({
          icon: "error",
          title: "Error",
          text: errorMsg,
        })
      },
    })
  }
}
