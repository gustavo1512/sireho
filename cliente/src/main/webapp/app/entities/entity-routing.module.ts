import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'cliente',
        data: { pageTitle: 'sirehoApp.cliente.home.title' },
        loadChildren: () => import('./cliente/cliente.routes'),
      },
      {
        path: 'habitaciones',
        data: { pageTitle: 'sirehoApp.habitaciones.home.title' },
        loadChildren: () => import('./habitaciones/habitaciones.routes'),
      },
      {
        path: 'reservaciones',
        data: { pageTitle: 'sirehoApp.reservaciones.home.title' },
        loadChildren: () => import('./reservaciones/reservaciones.routes'),
      },
      {
        path: 'factura',
        data: { pageTitle: 'sirehoApp.factura.home.title' },
        loadChildren: () => import('./factura/factura.routes'),
      },
      {
        path: 'eventos',
        data: { pageTitle: 'sirehoApp.eventos.home.title' },
        loadChildren: () => import('./eventos/eventos.routes'),
      },
      {
        path: 'colaborador',
        data: { pageTitle: 'sirehoApp.colaborador.home.title' },
        loadChildren: () => import('./colaborador/colaborador.routes'),
      },
      {
        path: 'tipo-cargo',
        data: { pageTitle: 'sirehoApp.tipoCargo.home.title' },
        loadChildren: () => import('./tipo-cargo/tipo-cargo.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
