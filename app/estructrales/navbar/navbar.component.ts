import { CommonModule } from "@angular/common"
import { Component, type OnInit, HostListener, type OnDestroy } from "@angular/core"
import { RouterModule } from "@angular/router"
// ⚠️ COMENTADO: Descomentar cuando implementes autenticación
// import { AuthService } from "../../services/auth.service"
// import { UserService, UserProfile } from "../../services/user.service"
// import { Subscription } from "rxjs"
// import type { User } from "@angular/fire/auth"

interface NavLink {
  name: string
  url: string
  hasIcon?: boolean
  iconType?: "heart" | "dashboard" | "profile" | "form" | "management"
}

@Component({
  selector: "app-navbar",
  imports: [CommonModule, RouterModule],
  templateUrl: "./navbar.component.html",
  styleUrl: "./navbar.component.css",
})
export class NavbarComponent implements OnInit, OnDestroy {
  isScrolled = false
  isMobileMenuOpen = false

  // ⚠️ COMENTADO: Variables de usuario - Descomentar cuando implementes autenticación
  // currentUser: User | null = null
  // userProfile: UserProfile | null = null
  // private userSubscription: Subscription = new Subscription()

  // Variables de colores usados en el componente
  colors = {
    primary: "#6366f1", // Color morado para el logo y botones
    textDark: "#333333", // Texto principal oscuro
    textLight: "#666666", // Texto secundario más claro
    borderColor: "#e5e7eb", // Color de borde
    backgroundColor: "#ffffff", // Fondo blanco
    buttonText: "#ffffff", // Texto blanco para botones
  }

  // Enlaces de navegación públicos (siempre visibles)
  publicNavLinks: NavLink[] = [
    { name: "Inicio", url: "#hero", hasIcon: false },
    { name: "Beneficios", url: "#benefits", hasIcon: false },
    { name: "Ubicaciones", url: "#locations", hasIcon: false },
  ]

  // ⚠️ COMENTADO: Enlaces para usuarios autenticados - Descomentar cuando implementes autenticación
  // authenticatedNavLinks: NavLink[] = [
  //   { name: "Dashboard", url: "/dashboard", hasIcon: true, iconType: 'dashboard' },
  //   { name: "Propiedades", url: "/property-management", hasIcon: true, iconType: 'management' },
  //   { name: "Agregar Propiedad", url: "/property-form", hasIcon: true, iconType: 'form' },
  //   { name: "Favoritos", url: "/favorites", hasIcon: true, iconType: 'heart' },
  //   { name: "Perfil", url: "/user-profile", hasIcon: true, iconType: 'profile' }
  // ]

  constructor(
    // ⚠️ COMENTADO: Servicios de autenticación - Descomentar cuando implementes autenticación
    // private authService: AuthService,
    // private userService: UserService
  ) {}

  ngOnInit(): void {
    // ⚠️ COMENTADO: Suscripción a cambios de usuario - Descomentar cuando implementes autenticación
    // this.userSubscription = this.authService.user$.subscribe(async (user) => {
    //   this.currentUser = user
    //
    //   if (user) {
    //     try {
    //       this.userProfile = await this.userService.getUserProfile(user.uid)
    //     } catch (error) {
    //       console.error("Error al cargar perfil del usuario en navbar:", error)
    //     }
    //   } else {
    //     this.userProfile = null
    //   }
    // })
  }

  ngOnDestroy(): void {
    // ⚠️ COMENTADO: Desuscripción - Descomentar cuando implementes autenticación
    // this.userSubscription.unsubscribe()
  }

  // Obtener enlaces (por ahora siempre devuelve los públicos)
  get navLinks(): NavLink[] {
    // ⚠️ COMENTADO: Lógica condicional - Descomentar cuando implementes autenticación
    // return this.currentUser ? this.authenticatedNavLinks : this.publicNavLinks
    return this.publicNavLinks
  }

  // Detectar scroll para cambiar estilos de navbar
  @HostListener("window:scroll", [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 40
  }

  // Alternar menú móvil
  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen
  }

  // ⚠️ COMENTADO: Método de logout - Descomentar cuando implementes autenticación
  // async logout() {
  //   try {
  //     await this.authService.logout()
  //     this.isMobileMenuOpen = false
  //   } catch (error) {
  //     console.error("Error al cerrar sesión:", error)
  //   }
  // }

  // ⚠️ COMENTADO: Métodos de usuario - Descomentar cuando implementes autenticación
  // getUserDisplayName(): string {
  //   if (this.currentUser?.displayName) {
  //     return this.currentUser.displayName
  //   }
  //   if (this.currentUser?.email) {
  //     return this.currentUser.email.split("@")[0]
  //   }
  //   return "Usuario"
  // }

  // getUserPhotoURL(): string | null {
  //   return this.currentUser?.photoURL || this.userProfile?.photoURL || null
  // }

  // hasUserPhoto(): boolean {
  //   return !!this.getUserPhotoURL()
  // }

  // getUserInitials(): string {
  //   const displayName = this.getUserDisplayName()
  //   const words = displayName.split(' ')
  //
  //   if (words.length >= 2) {
  //     return (words[0][0] + words[1][0]).toUpperCase()
  //   } else {
  //     return displayName.substring(0, 2).toUpperCase()
  //   }
  // }

  scrollToSection(sectionId: string): void {
    // Remover el # del inicio si existe
    const id = sectionId.startsWith("#") ? sectionId.substring(1) : sectionId
    const element = document.getElementById(id)

    if (element) {
      element.scrollIntoView({ behavior: "smooth" })
      // Cerrar menú móvil si está abierto
      this.isMobileMenuOpen = false
    }
  }

  // Verificar si es un enlace de scroll interno
  isScrollLink(url: string): boolean {
    return url.startsWith("#")
  }

  // Método para obtener SVG de iconos
  getIconSvg(iconType: NavLink["iconType"] | undefined): string {
    switch (iconType) {
      case "heart":
        return "M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
      case "dashboard":
        return "M3 4a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 12a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H4a1 1 0 01-1-1v-4zM11 4a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1V4zM11 12a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1v-4z"
      case "profile":
        return "M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
      case "form":
        return "M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
      case "management":
        return "M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
      default:
        return "M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
    }
  }
}
