import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FacturaComponent } from './list/factura.component';
import { FacturaDetailComponent } from './detail/factura-detail.component';
import { FacturaUpdateComponent } from './update/factura-update.component';
import FacturaResolve from './route/factura-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const facturaRoute: Routes = [
  {
    path: '',
    component: FacturaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FacturaDetailComponent,
    resolve: {
      factura: FacturaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FacturaUpdateComponent,
    resolve: {
      factura: FacturaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FacturaUpdateComponent,
    resolve: {
      factura: FacturaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default facturaRoute;
