import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { PropertyRequest, PropertyResponse } from "../models/property.model";
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class PropertyService {
  private apiUrl = `${environment.apiUrl}/api/properties`;

  constructor(private http: HttpClient) {}

  createProperty(request: PropertyRequest): Observable<PropertyResponse> {
    return this.http.post<PropertyResponse>(this.apiUrl, request);
  }

  getAllProperties(): Observable<PropertyResponse[]> {
    return this.http.get<PropertyResponse[]>(this.apiUrl);
  }

  getPropertyById(id: number): Observable<PropertyResponse> {
    return this.http.get<PropertyResponse>(`${this.apiUrl}/${id}`);
  }

  updateProperty(id: number, request: PropertyRequest): Observable<PropertyResponse> {
    return this.http.put<PropertyResponse>(`${this.apiUrl}/${id}`, request);
  }

  deleteProperty(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAvailableProperties(): Observable<PropertyResponse[]> {
    return this.http.get<PropertyResponse[]>(`${this.apiUrl}/available`);
  }

  getPropertiesByCity(city: string): Observable<PropertyResponse[]> {
    return this.http.get<PropertyResponse[]>(`${this.apiUrl}/city/${city}`);
  }

  getPropertiesByType(propertyType: string): Observable<PropertyResponse[]> {
    return this.http.get<PropertyResponse[]>(`${this.apiUrl}/type/${propertyType}`);
  }

  getPropertiesByTransaction(transactionType: string): Observable<PropertyResponse[]> {
    return this.http.get<PropertyResponse[]>(`${this.apiUrl}/transaction/${transactionType}`);
  }

  getPropertiesByPriceRange(minPrice: number, maxPrice: number): Observable<PropertyResponse[]> {
    const params = new HttpParams().set("minPrice", minPrice.toString()).set("maxPrice", maxPrice.toString());

    return this.http.get<PropertyResponse[]>(`${this.apiUrl}/price-range`, { params });
  }

  getPropertiesByUser(userId: number): Observable<PropertyResponse[]> {
    return this.http.get<PropertyResponse[]>(`${this.apiUrl}/user/${userId}`);
  }
}
