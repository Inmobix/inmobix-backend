import { CommonModule } from "@angular/common"
import { Component, type OnInit } from "@angular/core"
import { FormsModule } from "@angular/forms"
import { PropertyService } from "../../services/property.service"
import { Property } from "../../models/property.model"

// <CHANGE> Created a separate interface for display properties
interface DisplayProperty {
  id: number
  title: string
  price: number
  rentType: string
  imgSrc: string
  isFavorite: boolean
  location: string
  beds: number
  baths: number
  area: number
  areaUnit: string
  type: "venta" | "alquiler"
  isPopular?: boolean
}

@Component({
  selector: "app-location-properties",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./location-properties.component.html",
  styleUrls: ["./location-properties.component.css"],
})
export class LocationPropertiesComponent implements OnInit {
  // Variables de colores y estilos
  colors = {
    primary: "#6366f1",
    secondary: "#1e293b",
    textLight: "#64748b",
    accent: "#3730a3",
    lightBg: "#f8fafc",
    white: "#ffffff",
    border: "#e2e8f0",
    favoriteActive: "#ef4444",
    popularBg: "#6366f1",
  }

  // Filtros activos
  activeFilter: "todos" | "venta" | "alquiler" = "alquiler"
  searchTerm = ""

  // Filtros por características
  minBeds: number | null = null
  maxBeds: number | null = null
  minBaths: number | null = null
  maxBaths: number | null = null
  minArea: number | null = null
  maxArea: number | null = null
  minPrice: number | null = null
  maxPrice: number | null = null

  // <CHANGE> Changed to DisplayProperty type
  properties: DisplayProperty[] = []

  loading = false
  error: string | null = null

  constructor(private propertyService: PropertyService) {}

  ngOnInit(): void {
    this.loadProperties()
  }

  loadProperties(): void {
    this.loading = true
    this.error = null

    this.propertyService.getAllProperties().subscribe({
      next: (response: Property[]) => {
        this.properties = this.mapResponseToProperties(response)
        this.loading = false
      },
      error: (err) => {
        console.error("Error al cargar propiedades:", err)
        this.error = "No se pudieron cargar las propiedades. Por favor, intenta de nuevo."
        this.loading = false
      },
    })
  }

  // <CHANGE> Fixed mapping function with proper types
  private mapResponseToProperties(responses: Property[]): DisplayProperty[] {
    return responses.map((prop) => ({
      id: prop.id || 0,
      title: prop.title,
      price: prop.price,
      rentType: prop.transactionType === "RENT" ? "Mes" : "Total",
      imgSrc: prop.imageUrl || "assets/images/default-property.jpg",
      isFavorite: false,
      location: `${prop.address}, ${prop.city}, ${prop.state}`,
      beds: prop.bedrooms || 0,
      baths: prop.bathrooms || 0,
      area: prop.area || 0,
      areaUnit: "m²",
      type: prop.transactionType?.toLowerCase() === "sale" ? "venta" : "alquiler",
      isPopular: false,
    }))
  }

  // Cambiar filtro activo
  setFilter(filter: "todos" | "venta" | "alquiler"): void {
    this.activeFilter = filter
  }

  // Alternar favorito
  toggleFavorite(property: DisplayProperty): void {
    property.isFavorite = !property.isFavorite
  }

  // Limpiar todos los filtros
  clearFilters(): void {
    this.searchTerm = ""
    this.minBeds = null
    this.maxBeds = null
    this.minBaths = null
    this.maxBaths = null
    this.minArea = null
    this.maxArea = null
    this.minPrice = null
    this.maxPrice = null
    this.activeFilter = "todos"
  }

  // Filtrar propiedades según todos los criterios
  get filteredProperties(): DisplayProperty[] {
    let filtered = this.properties

    // Filtrar por tipo
    if (this.activeFilter !== "todos") {
      filtered = filtered.filter((property) => property.type === this.activeFilter)
    }

    // Filtrar por término de búsqueda
    if (this.searchTerm.trim()) {
      const searchLower = this.searchTerm.toLowerCase()
      filtered = filtered.filter(
        (property) =>
          property.title.toLowerCase().includes(searchLower) || property.location.toLowerCase().includes(searchLower),
      )
    }

    // Filtrar por habitaciones
    if (this.minBeds !== null) {
      filtered = filtered.filter((property) => property.beds >= this.minBeds!)
    }
    if (this.maxBeds !== null) {
      filtered = filtered.filter((property) => property.beds <= this.maxBeds!)
    }

    // Filtrar por baños
    if (this.minBaths !== null) {
      filtered = filtered.filter((property) => property.baths >= this.minBaths!)
    }
    if (this.maxBaths !== null) {
      filtered = filtered.filter((property) => property.baths <= this.maxBaths!)
    }

    // Filtrar por área
    if (this.minArea !== null) {
      filtered = filtered.filter((property) => property.area >= this.minArea!)
    }
    if (this.maxArea !== null) {
      filtered = filtered.filter((property) => property.area <= this.maxArea!)
    }

    // Filtrar por precio
    if (this.minPrice !== null) {
      filtered = filtered.filter((property) => property.price >= this.minPrice!)
    }
    if (this.maxPrice !== null) {
      filtered = filtered.filter((property) => property.price <= this.maxPrice!)
    }

    return filtered
  }

  // Formatear precio
  formatPrice(price: number): string {
    return "$" + price.toLocaleString()
  }

  // Obtener conteo de propiedades por tipo
  getPropertyCount(type: "todos" | "venta" | "alquiler"): number {
    if (type === "todos") {
      return this.properties.length
    }
    return this.properties.filter((property) => property.type === type).length
  }
}