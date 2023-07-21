import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HabitacionesComponent } from './list/habitaciones.component';
import { HabitacionesDetailComponent } from './detail/habitaciones-detail.component';
import { HabitacionesUpdateComponent } from './update/habitaciones-update.component';
import HabitacionesResolve from './route/habitaciones-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const habitacionesRoute: Routes = [
  {
    path: '',
    component: HabitacionesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HabitacionesDetailComponent,
    resolve: {
      habitaciones: HabitacionesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HabitacionesUpdateComponent,
    resolve: {
      habitaciones: HabitacionesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HabitacionesUpdateComponent,
    resolve: {
      habitaciones: HabitacionesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default habitacionesRoute;
