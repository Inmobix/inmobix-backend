import type { Routes } from "@angular/router"
import { HomeComponent } from "./components/landing/home/home.component"
import { LoginComponent } from "./components/auth/login/login.component"
import { RegisterComponent } from "./components/auth/register/register.component"
import { ForgotPasswordComponent } from "./components/auth/forgot-password/forgot-password.component"
import { VerifyEmailComponent } from "./components/auth/verify-email/verify-email.component"
import { DashboardComponent } from "./components/dashboard/dashboard/dashboard.component"
import { AuthGuard } from "./guards/auth.guard"
import { UserListComponent } from "./components/dashboard/user-list/user-list.component"
import { NavbarComponent } from "./estructrales/navbar/navbar.component"
import { FooterComponent } from "./estructrales/footer/footer.component"
import { LocationPropertiesComponent } from "./estructrales/location-properties/location-properties.component"
import { HeroComponent } from "./estructrales/hero/hero.component"
import { PropertyBenefitsComponent } from "./estructrales/property-benefits/property-benefits.component"
import { ResetPasswordComponent } from "./components/auth/reset-password/reset-password.component"

export const routes: Routes = [
  { path: "", component: HomeComponent },
  { path: "navbar", component: NavbarComponent },
  { path: "footer", component: FooterComponent },
  { path: "location-properties", component: LocationPropertiesComponent },
  { path: "hero", component: HeroComponent },
  { path: "property-benefits", component: PropertyBenefitsComponent },

  { path: "login", component: LoginComponent },
  { path: "register", component: RegisterComponent },
  { path: "forgot-password", component: ForgotPasswordComponent },
  { path: "reset-password", component: ResetPasswordComponent },
  { path: "verify-email", component: VerifyEmailComponent },
  { path: "dashboard", component: DashboardComponent, canActivate: [AuthGuard] },
  { path: "users", component: UserListComponent, canActivate: [AuthGuard] },
  { path: "**", redirectTo: "" },
]
