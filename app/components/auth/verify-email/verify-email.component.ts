import { Component, type OnInit, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core"
import { CommonModule } from "@angular/common"
import { RouterModule, Router, ActivatedRoute } from "@angular/router"
import { FormsModule } from "@angular/forms"
import { ApiService } from "../../../services/api.service"
import type { VerifyWithTokenRequest } from "../../../models/user.model"
import Swal from "sweetalert2"

@Component({
  selector: "app-verify-email",
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: "./verify-email.component.html",
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class VerifyEmailComponent implements OnInit {
  verificationToken = ""
  code = ""
  codeError = ""
  isLoading = false
  message = ""
  emailToVerify = ""

  constructor(
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit() {
    const storedToken = localStorage.getItem('verificationToken')
    const storedEmail = localStorage.getItem('emailToVerify')

    if (storedToken) {
      this.verificationToken = storedToken
      console.log("[v0] Token de verificación cargado desde localStorage")
    } else {
      // Fallback: intentar obtener de query params
      this.route.queryParams.subscribe((params) => {
        if (params["token"]) {
          this.verificationToken = params["token"]
          console.log("[v0] Token de verificación cargado desde URL")
        } else {
          console.log("[v0] No se encontró token de verificación")
        }
      })
    }

    // Guardar email si existe para función de reenvío
    if (storedEmail) {
      this.emailToVerify = storedEmail
    }
  }

  validateCode() {
    if (!this.code || this.code.length !== 6) {
      this.codeError = "El código debe tener 6 dígitos"
    } else {
      this.codeError = ""
    }
  }

  onSubmit() {
    this.validateCode()

    if (!this.codeError && this.verificationToken && this.code) {
      this.isLoading = true

      const verifyData: VerifyWithTokenRequest = {
        verificationToken: this.verificationToken,
        code: this.code,
      }

      this.apiService.verifyEmail(verifyData).subscribe({
        next: (response) => {
          this.isLoading = false
          const message = response.message || "Email verificado exitosamente"

          localStorage.removeItem('verificationToken')
          localStorage.removeItem('emailToVerify')

          Swal.fire({
            icon: "success",
            title: "¡Verificación exitosa!",
            text: message,
            timer: 2000,
            timerProgressBar: true,
            showConfirmButton: false,
          }).then(() => {
            this.router.navigate(["/login"])
          })
        },
        error: (error: any) => {
          this.isLoading = false
          const errorMsg = error.error?.message || "Error al verificar el email. Intenta nuevamente."

          Swal.fire({
            icon: "error",
            title: "Error de verificación",
            text: errorMsg,
          })
        },
      })
    } else if (!this.verificationToken) {
      Swal.fire({
        icon: "warning",
        title: "Token faltante",
        text: "No se encontró el token de verificación. Por favor, vuelve a registrarte o solicita un nuevo código.",
      })
    }
  }

  resendCode() {
    if (!this.emailToVerify) {
      Swal.fire({
        icon: "warning",
        title: "Email faltante",
        text: "No se puede reenviar el código. Por favor, vuelve a iniciar sesión.",
      })
      return
    }

    this.isLoading = true

    // Usar el endpoint de reenvío con el email guardado
    this.apiService.resendVerificationEmail({ email: this.emailToVerify }).subscribe({
      next: (response) => {
        this.isLoading = false

        // Si el backend devuelve un nuevo token, guardarlo
        if (response.verificationToken) {
          localStorage.setItem('verificationToken', response.verificationToken)
          this.verificationToken = response.verificationToken
        }

        const message = response.message || "Código reenviado exitosamente"

        Swal.fire({
          icon: "success",
          title: "¡Código reenviado!",
          text: message,
          timer: 2000,
          timerProgressBar: true,
          showConfirmButton: false,
        })
      },
      error: (error) => {
        this.isLoading = false
        const errorMsg = error.error?.message || "Error al reenviar el código."

        Swal.fire({
          icon: "error",
          title: "Error",
          text: errorMsg,
        })
      }
    })
  }
}
