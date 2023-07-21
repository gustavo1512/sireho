import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TipoCargoComponent } from './list/tipo-cargo.component';
import { TipoCargoDetailComponent } from './detail/tipo-cargo-detail.component';
import { TipoCargoUpdateComponent } from './update/tipo-cargo-update.component';
import TipoCargoResolve from './route/tipo-cargo-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const tipoCargoRoute: Routes = [
  {
    path: '',
    component: TipoCargoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TipoCargoDetailComponent,
    resolve: {
      tipoCargo: TipoCargoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TipoCargoUpdateComponent,
    resolve: {
      tipoCargo: TipoCargoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TipoCargoUpdateComponent,
    resolve: {
      tipoCargo: TipoCargoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tipoCargoRoute;
