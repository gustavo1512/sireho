import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ReservacionesComponent } from './list/reservaciones.component';
import { ReservacionesDetailComponent } from './detail/reservaciones-detail.component';
import { ReservacionesUpdateComponent } from './update/reservaciones-update.component';
import ReservacionesResolve from './route/reservaciones-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const reservacionesRoute: Routes = [
  {
    path: '',
    component: ReservacionesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ReservacionesDetailComponent,
    resolve: {
      reservaciones: ReservacionesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ReservacionesUpdateComponent,
    resolve: {
      reservaciones: ReservacionesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ReservacionesUpdateComponent,
    resolve: {
      reservaciones: ReservacionesResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reservacionesRoute;
