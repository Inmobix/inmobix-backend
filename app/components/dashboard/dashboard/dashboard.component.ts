import { Component, type OnInit } from "@angular/core"
import { Router, RouterLink } from "@angular/router"
import { PropertyService } from "../../../services/property.service"
import { ApiService } from "../../../services/api.service"
import type { PropertyRequest, PropertyResponse } from "../../../models/property.model"
import Swal from "sweetalert2"
import { CommonModule } from "@angular/common"
import { FormsModule } from "@angular/forms"
import type { UserResponse } from "../../../models/user.model"

@Component({
  selector: "app-dashboard",
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: "./dashboard.component.html",
})
export class DashboardComponent implements OnInit {
  currentView: "menu" | "list" | "create" | "edit" | "search" | "profile" = "menu"
  loading = false

  // Lista de propiedades (PropertyResponse del backend)
  properties: PropertyResponse[] = []

  // Formulario para crear/editar (PropertyRequest para enviar al backend)
  propertyForm: PropertyRequest = {
    title: "",
    description: "",
    address: "",
    city: "",
    state: "",
    price: 0,
    area: 0,
    bedrooms: 0,
    bathrooms: 0,
    garages: 0,
    propertyType: "HOUSE",
    transactionType: "SALE",
    available: true,
    imageUrl: "",
    userId: 0,
  }

  // ID de la propiedad que se está editando
  editingPropertyId: number | null = null

  // Variables de búsqueda
  searchCity = ""
  searchType = ""
  searchTransaction = ""
  minPrice: number | null = null
  maxPrice: number | null = null

  constructor(
    private propertyService: PropertyService,
    private apiService: ApiService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadProperties()
  }

  // Cambiar vista
  showView(view: "menu" | "list" | "create" | "edit" | "search" | "profile"): void {
    this.currentView = view
    if (view === "list") {
      this.loadProperties()
    } else if (view === "create") {
      this.resetForm()
    }
  }

  // Cargar todas las propiedades
  loadProperties(): void {
    this.loading = true
    this.propertyService.getAllProperties().subscribe({
      next: (data) => {
        this.properties = data
        this.loading = false
      },
      error: (error) => {
        console.error("Error al cargar propiedades:", error)
        Swal.fire("Error", "No se pudieron cargar las propiedades", "error")
        this.loading = false
      },
    })
  }

  // Cargar propiedades disponibles
  loadAvailableProperties(): void {
    this.loading = true
    this.propertyService.getAvailableProperties().subscribe({
      next: (data) => {
        this.properties = data
        this.currentView = "list"
        this.loading = false
      },
      error: (error) => {
        console.error("Error al cargar propiedades disponibles:", error)
        Swal.fire("Error", "No se pudieron cargar las propiedades disponibles", "error")
        this.loading = false
      },
    })
  }

  // Crear propiedad
  createProperty(): void {
    this.loading = true
    this.propertyService.createProperty(this.propertyForm).subscribe({
      next: (response) => {
        Swal.fire("Éxito", "Propiedad creada correctamente", "success")
        this.resetForm()
        this.showView("list")
        this.loading = false
      },
      error: (error) => {
        console.error("Error al crear propiedad:", error)
        Swal.fire("Error", "No se pudo crear la propiedad", "error")
        this.loading = false
      },
    })
  }

  // Seleccionar propiedad para editar
  selectPropertyForEdit(property: PropertyResponse): void {
    this.editingPropertyId = property.id
    // Convertir PropertyResponse a PropertyRequest para el formulario
    this.propertyForm = {
      title: property.title,
      description: property.description,
      address: property.address,
      city: property.city,
      state: property.state,
      price: property.price,
      area: property.area,
      bedrooms: property.bedrooms,
      bathrooms: property.bathrooms,
      garages: property.garages,
      propertyType: property.propertyType,
      transactionType: property.transactionType,
      available: property.available,
      imageUrl: property.imageUrl || "",
      userId: property.userId || 0,
    }
    this.currentView = "edit"
  }

  // Actualizar propiedad
  updateProperty(): void {
    if (this.editingPropertyId === null) return

    this.loading = true
    this.propertyService.updateProperty(this.editingPropertyId, this.propertyForm).subscribe({
      next: (response) => {
        Swal.fire("Éxito", "Propiedad actualizada correctamente", "success")
        this.resetForm()
        this.showView("list")
        this.loading = false
      },
      error: (error) => {
        console.error("Error al actualizar propiedad:", error)
        Swal.fire("Error", "No se pudo actualizar la propiedad", "error")
        this.loading = false
      },
    })
  }

  // Eliminar propiedad
  deleteProperty(id: number): void {
    Swal.fire({
      title: "¿Estás seguro?",
      text: "Esta acción no se puede deshacer",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Sí, eliminar",
      cancelButtonText: "Cancelar",
    }).then((result) => {
      if (result.isConfirmed) {
        this.loading = true
        this.propertyService.deleteProperty(id).subscribe({
          next: () => {
            Swal.fire("Eliminado", "La propiedad ha sido eliminada", "success")
            this.loadProperties()
            this.loading = false
          },
          error: (error) => {
            console.error("Error al eliminar propiedad:", error)
            Swal.fire("Error", "No se pudo eliminar la propiedad", "error")
            this.loading = false
          },
        })
      }
    })
  }

  // Búsquedas
  searchByCity(): void {
    if (!this.searchCity) {
      Swal.fire("Advertencia", "Por favor ingresa una ciudad", "warning")
      return
    }
    this.loading = true
    this.propertyService.getPropertiesByCity(this.searchCity).subscribe({
      next: (data) => {
        this.properties = data
        this.currentView = "list"
        this.loading = false
      },
      error: (error) => {
        console.error("Error en búsqueda:", error)
        Swal.fire("Error", "No se pudo realizar la búsqueda", "error")
        this.loading = false
      },
    })
  }

  searchByType(): void {
    if (!this.searchType) {
      Swal.fire("Advertencia", "Por favor selecciona un tipo", "warning")
      return
    }
    this.loading = true
    this.propertyService.getPropertiesByType(this.searchType).subscribe({
      next: (data) => {
        this.properties = data
        this.currentView = "list"
        this.loading = false
      },
      error: (error) => {
        console.error("Error en búsqueda:", error)
        Swal.fire("Error", "No se pudo realizar la búsqueda", "error")
        this.loading = false
      },
    })
  }

  searchByTransaction(): void {
    if (!this.searchTransaction) {
      Swal.fire("Advertencia", "Por favor selecciona un tipo de transacción", "warning")
      return
    }
    this.loading = true
    this.propertyService.getPropertiesByTransaction(this.searchTransaction).subscribe({
      next: (data) => {
        this.properties = data
        this.currentView = "list"
        this.loading = false
      },
      error: (error) => {
        console.error("Error en búsqueda:", error)
        Swal.fire("Error", "No se pudo realizar la búsqueda", "error")
        this.loading = false
      },
    })
  }

  searchByPriceRange(): void {
    if (this.minPrice === null || this.maxPrice === null) {
      Swal.fire("Advertencia", "Por favor ingresa ambos precios", "warning")
      return
    }
    this.loading = true
    this.propertyService.getPropertiesByPriceRange(this.minPrice, this.maxPrice).subscribe({
      next: (data) => {
        this.properties = data
        this.currentView = "list"
        this.loading = false
      },
      error: (error) => {
        console.error("Error en búsqueda:", error)
        Swal.fire("Error", "No se pudo realizar la búsqueda", "error")
        this.loading = false
      },
    })
  }

  // Resetear formulario
  resetForm(): void {
    this.propertyForm = {
      title: "",
      description: "",
      address: "",
      city: "",
      state: "",
      price: 0,
      area: 0,
      bedrooms: 0,
      bathrooms: 0,
      garages: 0,
      propertyType: "HOUSE",
      transactionType: "SALE",
      available: true,
      imageUrl: "",
      userId: 0,
    }
    this.editingPropertyId = null
  }

  buscarUsuarioPorId(): void {
    Swal.fire({
      title: "Buscar Usuario",
      input: "number",
      inputLabel: "Ingresa el ID del usuario",
      inputPlaceholder: "ID del usuario",
      showCancelButton: true,
      confirmButtonText: "Buscar",
      cancelButtonText: "Cancelar",
      inputValidator: (value) => {
        if (!value) {
          return "Debes ingresar un ID"
        }
        return null
      },
    }).then((result) => {
      if (result.isConfirmed) {
        const userId = Number.parseInt(result.value)
        this.apiService.getUserById(userId).subscribe({
          next: (user: UserResponse) => {
            Swal.fire({
              title: "Usuario Encontrado",
              html: `
                <p><strong>ID:</strong> ${user.id}</p>
                <p><strong>Nombre:</strong> ${user.name}</p>
                <p><strong>Email:</strong> ${user.email}</p>
              `,
              icon: "success",
            })
          },
          error: (error: any) => {
            console.error("Error al buscar usuario:", error)
            Swal.fire("Error", "No se encontró el usuario", "error")
          },
        })
      }
    })
  }

  logout(): void {
    localStorage.removeItem("token")
    this.router.navigate(["/login"])
  }
}
