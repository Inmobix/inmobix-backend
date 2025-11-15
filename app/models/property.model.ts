// Interfaz que coincide con PropertyRequest.java del backend
export interface PropertyRequest {
  title: string
  description: string
  address: string
  city: string
  state: string
  price: number // BigDecimal en Java
  area: number // BigDecimal en Java
  bedrooms: number // Integer en Java, default 0
  bathrooms: number // Integer en Java, default 0
  garages: number // Integer en Java, default 0
  propertyType: string // HOUSE, APARTMENT, OFFICE, LOT, FARM
  transactionType: string // SALE, RENT
  available: boolean // default true
  imageUrl?: string
  userId?: number // Long en Java - ID del usuario propietario/agente
}

// Interfaz que coincide con PropertyResponse.java del backend
export interface PropertyResponse {
  id: number // Long en Java
  title: string
  description: string
  address: string
  city: string
  state: string
  price: number // BigDecimal en Java
  area: number // BigDecimal en Java
  bedrooms: number
  bathrooms: number
  garages: number
  propertyType: string
  transactionType: string
  available: boolean
  imageUrl?: string
  createdAt: string // LocalDateTime en Java, se recibe como string ISO
  updatedAt: string // LocalDateTime en Java, se recibe como string ISO

  // Información del usuario propietario
  userId?: number
  userName?: string
  userEmail?: string
  userPhone?: string
}

// Alias para mantener compatibilidad con código existente
export type Property = PropertyResponse
