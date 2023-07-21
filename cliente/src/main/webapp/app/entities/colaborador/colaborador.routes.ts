import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ColaboradorComponent } from './list/colaborador.component';
import { ColaboradorDetailComponent } from './detail/colaborador-detail.component';
import { ColaboradorUpdateComponent } from './update/colaborador-update.component';
import ColaboradorResolve from './route/colaborador-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const colaboradorRoute: Routes = [
  {
    path: '',
    component: ColaboradorComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ColaboradorDetailComponent,
    resolve: {
      colaborador: ColaboradorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ColaboradorUpdateComponent,
    resolve: {
      colaborador: ColaboradorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ColaboradorUpdateComponent,
    resolve: {
      colaborador: ColaboradorResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default colaboradorRoute;
