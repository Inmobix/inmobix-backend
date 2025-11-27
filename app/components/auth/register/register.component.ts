import { Component, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core"
import { CommonModule } from "@angular/common"
import { RouterModule, Router } from "@angular/router"
import { FormsModule } from "@angular/forms"
import { ApiService } from "../../../services/api.service"
import type { UserRequest } from "../../../models/user.model"
import Swal from "sweetalert2"

@Component({
  selector: "app-register",
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: "./register.component.html",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class RegisterComponent {
  name = ""
  username = ""
  email = ""
  password = ""
  confirmPassword = ""
  documento = ""
  telefono?: string
  fechaNacimiento?: string

  nameError = false
  usernameError = false
  emailError = false
  passwordError = false
  confirmPasswordError = false
  confirmPasswordMismatch = false
  documentoError = false
  isLoading = false
  errorMessage = ""
  successMessage = ""

  constructor(
    private apiService: ApiService,
    private router: Router,
  ) {}

  validateName() {
    this.nameError = !this.name.trim()
  }

  validateUsername() {
    this.usernameError = !this.username.trim()
  }

  validateEmail() {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    this.emailError = !this.email.trim() || !emailPattern.test(this.email)
  }

  validatePassword() {
    this.passwordError = !this.password
    this.validateConfirmPassword()
  }

  validateConfirmPassword() {
    this.confirmPasswordError = !this.confirmPassword
    this.confirmPasswordMismatch = !!this.password && !!this.confirmPassword && this.password !== this.confirmPassword
  }

  validateDocumento() {
    this.documentoError = !this.documento.trim()
  }

  onSubmit() {
    this.validateName()
    this.validateUsername()
    this.validateEmail()
    this.validatePassword()
    this.validateConfirmPassword()
    this.validateDocumento()
    this.errorMessage = ""
    this.successMessage = ""

    if (
      this.nameError ||
      this.usernameError ||
      this.emailError ||
      this.passwordError ||
      this.confirmPasswordError ||
      this.confirmPasswordMismatch ||
      this.documentoError
    ) {
      return
    }

    this.isLoading = true

    const formattedBirthDate = this.fechaNacimiento
      ? new Date(this.fechaNacimiento).toISOString().split("T")[0]
      : undefined

    const userData: UserRequest = {
      name: this.name,
      username: this.username,
      email: this.email,
      password: this.password,
      documento: this.documento,
      phone: this.telefono,
      birthDate: formattedBirthDate,
    }

    this.apiService.registerUser(userData).subscribe({
      next: (response) => {
        this.isLoading = false
        console.log('[v0] Respuesta del registro:', response)
        console.log('[v0] Token de verificación recibido:', response.verificationToken)

        const message = response.message || "Registro exitoso. Verifica tu email."
        const verificationToken = response.verificationToken

        if (verificationToken) {
          localStorage.setItem('verificationToken', verificationToken)
          console.log('[v0] Token guardado en localStorage')
        } else {
          console.warn('[v0] No se recibió token de verificación')
        }

        localStorage.setItem('emailToVerify', this.email)

        Swal.fire({
          icon: "success",
          title: "¡Registro exitoso!",
          text: message,
          timer: 3000,
          timerProgressBar: true,
          showConfirmButton: false,
        }).then(() => {
          this.router.navigate(["/verify-email"])
        })
      },
      error: (error) => {
        this.isLoading = false

        let errorMsg = "Intenta nuevamente."
        if (error.error?.message) {
          errorMsg = error.error.message
        } else if (error.message) {
          errorMsg = error.message
        } else if (error.status === 0) {
          errorMsg = "No se puede conectar con el servidor. Verifica tu conexión."
        }

        Swal.fire({
          icon: "error",
          title: "¡Error al registrar!",
          text: errorMsg,
        })
      },
    })
  }
}
