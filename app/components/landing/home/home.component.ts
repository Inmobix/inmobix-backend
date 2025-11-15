import { Component } from "@angular/core"
import { CommonModule } from "@angular/common"
import { RouterModule } from "@angular/router"
import { NavbarComponent } from "../../../estructrales/navbar/navbar.component"
import { HeroComponent } from "../../../estructrales/hero/hero.component"
import { PropertyBenefitsComponent } from "../../../estructrales/property-benefits/property-benefits.component"
import { LocationPropertiesComponent } from "../../../estructrales/location-properties/location-properties.component"
import { FooterComponent } from "../../../estructrales/footer/footer.component"


@Component({
  selector: "app-home",
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NavbarComponent,
    HeroComponent,
    PropertyBenefitsComponent,
    LocationPropertiesComponent,
    FooterComponent,
  ],
  templateUrl: "./home.component.html",
})
export class HomeComponent {
  // El navbar maneja su propio scroll, no necesitamos métodos aquí
}
